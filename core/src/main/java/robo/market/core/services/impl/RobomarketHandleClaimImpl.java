package robo.market.core.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import robo.market.core.gsondatabind.purchase.PurchaseRequest;
import robo.market.core.gsondatabind.purchase.PurchaseResponce;
import robo.market.core.gsondatabind.requstparameters.Customer;
import robo.market.core.gsondatabind.requstparameters.Item;
import robo.market.core.gsondatabind.reservation.ReservationFailure;
import robo.market.core.gsondatabind.reservation.ReservationRequest;
import robo.market.core.gsondatabind.reservation.ReservationSuccess;
import robo.market.core.models.RobomarketProductModel;
import robo.market.core.robomarketutils.constants.RobomarketJsonKeys;
import robo.market.core.services.RobomarketHandleClaimService;
import robo.market.core.services.SendMailService;
import robo.market.core.servlets.EmailConfirmationServlet;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for processing requests."},
        service = RobomarketHandleClaimService.class, immediate = true)
public class RobomarketHandleClaimImpl implements RobomarketHandleClaimService {

    private static final String ROBOMARKET_PRODUCT_RESOURCE_TYPE = "robomarket-product/components/content/product";
    private static final String ERROR_CODE_FAIL = "Fail";
    private static final String ERROR_CODE_OK = "Ok";
    private static final String ERROR_CODE_NOT_ENOUGH_GOODS = "NotEnoughGoodsInStock";
    private static final List<String> invoiceIdList = new LinkedList();
    private static final Map<String, Date> invoiceIdPaymentDueMap = new HashMap<>(); // ублюдское название

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX")
            .create();

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SendMailService sendMailService;

    @Override
    public String processReservationRequest(JSONObject requestData) throws IOException, JSONException {
        ReservationRequest reservationRequest = gson.fromJson(requestData.toString(), ReservationRequest.class);
        if (Objects.isNull(reservationRequest)) {
            throw new JSONException("Error parsing JSON request string");
        }

        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;
        int orderId = reservationRequest.getOrderId();
        try {
            if (productExistsOnPage(reservationRequest.getItems())) {
                ReservationSuccess reservationSuccess = new ReservationSuccess(orderId);
                jsonResponseFields = gson.toJsonTree(reservationSuccess);
                jsonStatus.add(RobomarketJsonKeys.RESERVATION_SUCCESS, jsonResponseFields);
                // добавить в "базу"
                invoiceIdPaymentDueMap.put(reservationSuccess.getInvoiceId(), reservationSuccess.getPaymentDue());
            } else {
                //TODO: custom exception
                throw new Exception("Product doesn't exist");
            }

        } catch (Exception e) {
            ReservationFailure reservationFailure = new ReservationFailure(orderId, ERROR_CODE_FAIL);
            jsonResponseFields = gson.toJsonTree(reservationFailure);
            jsonStatus.add(RobomarketJsonKeys.RESERVATION_FAILURE, jsonResponseFields);
        }
        jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);

        return gson.toJson(jsonResponce);
    }

    private boolean productExistsOnPage(List<Item> items) throws LoginException {
        //TODO: изменить способ извлечения ресурса
        Resource responsivegrid = resolverFactory.getResourceResolver(null).getResource("/content/robomarket-product/en/jcr:content/root/responsivegrid");
        if (Objects.nonNull(responsivegrid)) {
            Iterator<Resource> resourceIterator = responsivegrid.listChildren();
            for (Item item : items) {
                while (resourceIterator.hasNext()) {
                    Resource resource = resourceIterator.next();
                    if (resource.isResourceType(ROBOMARKET_PRODUCT_RESOURCE_TYPE)) {
                        RobomarketProductModel product = resource.adaptTo(RobomarketProductModel.class);
                        if (item.getOfferId().equals(product.getOfferId())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String processYaReservationRequest(JSONObject requestData) throws IOException, JSONException {
        return "";
    }

    @Override
    public String processPurchaseRequest(JSONObject requestData) throws IOException, JSONException {
        PurchaseRequest purchaseRequest = gson.fromJson(requestData.toString(), PurchaseRequest.class);
        if (Objects.isNull(purchaseRequest)) {
            throw new JSONException("Error parsing JSON request string");
        }
        //TODO PATH
        String path = "/apps/robomarket-product/templates/emails/sample-template-email.txt";
        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;
        Date paymentDue = invoiceIdPaymentDueMap.get(purchaseRequest.getInvoiceId());
        Date now = new Date();
        if (Objects.nonNull(paymentDue) && now.before(paymentDue)){
            try {
                PurchaseResponce purchaseResponce = new PurchaseResponce(purchaseRequest.getOrderId(), ERROR_CODE_OK);
                jsonResponseFields = gson.toJsonTree(purchaseResponce);
                jsonStatus.add(RobomarketJsonKeys.PURCHASE_RESPONSE, jsonResponseFields);

                Customer customer = purchaseRequest.getCustomer();
                Map<String, String> templateValuesMap = createTemplateValuesMap(purchaseRequest.getItems());
                sendMailService.sendSuccessEmailToCustomer(customer.getEmail(), path, templateValuesMap);

                jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);
                return gson.toJson(jsonResponce);
            }
            catch (LoginException | MessagingException e) {
                //TODO
            }
        }

        return "";
    }

    private Map<String, String> createTemplateValuesMap(List<Item> itemList){
        Map<String, String> templateValuesMap = new HashMap<>();
        for (Item item: itemList){
            templateValuesMap.put("product_name", item.getItemTitle().getValue());
            templateValuesMap.put("product_description", "Cool product");           //TODO: УЗНАТЬ
            templateValuesMap.put("product_price", String.valueOf(item.getPrice()));

            String generatedConfirmationLink = UUID.randomUUID().toString();
            EmailConfirmationServlet.addConfirmationLink(generatedConfirmationLink);

            templateValuesMap.put("confirmation_link", generatedConfirmationLink);
        }
        return templateValuesMap;
    }

    @Override
    public String processCancellationRequest(JSONObject requestData) throws IOException, JSONException {
        return "";
    }

}
