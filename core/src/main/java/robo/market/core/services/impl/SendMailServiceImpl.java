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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import robo.market.core.services.SendMailService;
import robo.market.core.servlets.RobomarketProductServlet;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for sending emails to customers."},
        service = SendMailService.class, immediate = true)
public class SendMailServiceImpl implements SendMailService {

    private final String USER_NAME = "robomarketproduct@gmail.com";
    private final String PASSWORD = "aemtop000";
    private static final String DEFAULT_TEMPLATE_LETTER_PATH = "/apps/robomarket-product/templates/emails/sample-template-email.txt";

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            responsivegrid = resolverFactory.getResourceResolver(null).getResource(RobomarketProductServlet.getServletCallPath() + "/root/responsivegrid");
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
    public void sendSuccessEmailToCustomer(String customerEmail, Map<String, String> templateValuesMap) {
        try {
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
                    String body = templateString.substring(templateString.indexOf(specialSymbolBody) + specialSymbolBody.length());
                    for (Map.Entry<String, String> entry : templateValuesMap.entrySet()) {
                        body = body.replaceAll(entry.getKey(), entry.getValue());
                    }

                    message.setHeader("Content-Type", "text/plain; charset=UTF-8");
                    message.setSubject(subject, "UTF-8");
                    message.setText(body, "UTF-8");
                    Transport.send(message);
                }
            }
        } catch (LoginException | MessagingException e) {
            logger.error("Error with sending message");
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
