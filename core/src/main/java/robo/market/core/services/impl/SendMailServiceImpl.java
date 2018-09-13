package robo.market.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import robo.market.core.robomarketutils.constants.RobomarketJcrConstants;
import robo.market.core.services.SendMailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for sending emails to customers."},
        service = SendMailService.class, immediate = true)
public class SendMailServiceImpl implements SendMailService {
    //TODO: Config
    private final String USER_NAME = "robomarketproduct@gmail.com";
    private final String PASSWORD = "aemtop000";
    private static final String DEFAULT_TEMPLATE_LETTER_PATH = "/apps/robomarket-product/templates/emails/sample-template-email.txt";

    private Session session;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    protected void activate() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER_NAME, PASSWORD);
                    }
                });
    }

    private String getPathToTemplate() {
        String path = DEFAULT_TEMPLATE_LETTER_PATH;
        Resource responsivegrid;
        try {
            responsivegrid = resolverFactory.getResourceResolver(null).getResource(RobomarketJcrConstants.RESPONSIVEGRID_PATH);
        } catch (LoginException e) {
            return path;
        }
        if (Objects.nonNull(responsivegrid)) {
            ValueMap responsivegridValueMap = responsivegrid.getValueMap();
            path = (String) responsivegridValueMap.get("letterTemplatePath");
        }

        return path;
    }

    @Override
    public void sendSuccessEmailToCustomer(String customerEmail, Map<String, String> templateValuesMap) throws LoginException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USER_NAME));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(customerEmail));

        String specialSymbolSubject = "subject::";
        String specialSymbolBody = "body::";
        String templateString;

        String pathToTemplate = getPathToTemplate();
        Resource resource = resolverFactory.getResourceResolver(null).getResource(pathToTemplate + "/" + JcrConstants.JCR_CONTENT);
        if (Objects.nonNull(resource)) {
            ValueMap valueMap = resource.getValueMap();
            if (valueMap.get(JcrConstants.JCR_PRIMARYTYPE).equals(JcrConstants.NT_RESOURCE)) {
                InputStream inputStream = (InputStream) valueMap.get(JcrConstants.JCR_DATA);

                templateString = readStringFromTemplate(inputStream);

                String subject = templateString.substring(templateString.indexOf(specialSymbolSubject) + specialSymbolSubject.length(), templateString.indexOf(specialSymbolBody));
                String bodyWithoutValues = templateString.substring(templateString.indexOf(specialSymbolBody) + specialSymbolBody.length());
                Pattern pattern = Pattern.compile("%(\\w+)%");
                Matcher matcher = pattern.matcher(bodyWithoutValues);
                StringBuffer bodyWithValues = new StringBuffer();
                while (matcher.find()) {
                    String group = matcher.group(1);
                    String value = templateValuesMap.get(group);
                    if (Objects.nonNull(value)) {
                        matcher.appendReplacement(bodyWithValues, templateValuesMap.get(group));
                    } else {
                        matcher.appendReplacement(bodyWithValues, "");
                    }
                }
                matcher.appendTail(bodyWithValues);

                message.setHeader("Content-Type", "text/plain; charset=UTF-8");
                message.setSubject(subject, "UTF-8");
                message.setText(bodyWithValues.toString(), "UTF-8");
                Transport.send(message);
            }
        }


    }

    @Override
    public void sendFailEmailToCustomer(String customerEmail) throws LoginException, MessagingException {

    }

    private String readStringFromTemplate(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine())
                    .append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
