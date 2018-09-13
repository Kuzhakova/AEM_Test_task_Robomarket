package robo.market.core.services;

import org.apache.sling.api.resource.LoginException;

import javax.mail.MessagingException;
import java.util.Map;

public interface SendMailService {

    void sendSuccessEmailToCustomer(String customerEmail, Map<String, String> values) throws LoginException, MessagingException;

    void sendFailEmailToCustomer(String customerEmail) throws LoginException, MessagingException;
}
