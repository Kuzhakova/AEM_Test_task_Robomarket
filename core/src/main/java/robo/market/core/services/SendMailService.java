package robo.market.core.services;

import java.util.Map;

public interface SendMailService {

    void sendSuccessEmailToCustomer(String customerEmail, Map<String, String> values);

    void sendFailEmailToCustomer(String customerEmail);
}
