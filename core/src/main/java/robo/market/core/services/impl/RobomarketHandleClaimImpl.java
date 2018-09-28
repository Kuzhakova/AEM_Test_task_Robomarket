package robo.market.core.services.impl;

import com.google.gson.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import robo.market.core.exceptions.*;
import robo.market.core.jsondatabind.adapters.RequestsAnnotatedDeserializer;
import robo.market.core.jsondatabind.cancellation.*;
import robo.market.core.jsondatabind.purchase.*;
import robo.market.core.jsondatabind.requestparameters.Customer;
import robo.market.core.jsondatabind.responseparameters.ErrorJson;
import robo.market.core.jsondatabind.requestparameters.Item;
import robo.market.core.jsondatabind.reservation.*;
import robo.market.core.jsondatabind.yareservation.YaReservationRequest;
import robo.market.core.models.RobomarketProductModel;
import robo.market.core.robomarketutils.constants.RobomarketJsonKeys;
import robo.market.core.services.*;
import robo.market.core.servlets.EmailConfirmationServlet;
import robo.market.core.servlets.RobomarketProductServlet;

import java.util.*;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for processing requests."},
        service = RobomarketHandleClaimService.class, immediate = true)
public class RobomarketHandleClaimImpl implements RobomarketHandleClaimService {

    private long RESERVATION_TIME = 1800000;

    private static final String ROBOMARKET_PRODUCT_RESOURCE_TYPE = "robomarket-product/components/content/product";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SendMailService sendMailService;

    @Reference
    private RobomarketOrdersService robomarketOrdersService;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ReservationRequest.class, new RequestsAnnotatedDeserializer<ReservationRequest>())
            .registerTypeAdapter(PurchaseRequest.class, new RequestsAnnotatedDeserializer<PurchaseRequest>())
            .registerTypeAdapter(CancellationRequest.class, new RequestsAnnotatedDeserializer<CancellationRequest>())
            .registerTypeAdapter(YaReservationRequest.class, new RequestsAnnotatedDeserializer<YaReservationRequest>())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX")
            .create();

    @Override
    public String handleReservationRequest(JSONObject requestData) throws JsonParseException {
        ReservationRequest reservationRequest = gson.fromJson(requestData.toString(), ReservationRequest.class);
        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;
        int orderId = reservationRequest.getOrderId();
        if (productExistsOnPage(reservationRequest.getItems())) {
            Date paymentDue = new Date(RESERVATION_TIME + reservationRequest.getMinPaymentDue().getTime());
            ReservationSuccess reservationSuccess = new ReservationSuccess(orderId, paymentDue);
            jsonResponseFields = gson.toJsonTree(reservationSuccess);
            jsonStatus.add(RobomarketJsonKeys.RESERVATION_SUCCESS, jsonResponseFields);

            robomarketOrdersService.reserveRobomarketOrder(reservationRequest, reservationSuccess);
        } else {
            ReservationFailure reservationFailure = new ReservationFailure(orderId, ErrorJson.ERROR_CODE_FAIL);
            jsonResponseFields = gson.toJsonTree(reservationFailure);
            jsonStatus.add(RobomarketJsonKeys.RESERVATION_FAILURE, jsonResponseFields);
        }
        jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);

        return gson.toJson(jsonResponce);
    }

    private boolean productExistsOnPage(List<Item> items) {
        for (Item item : items) {
            if (Objects.nonNull(getProductFromPage(item))) {
                return true;
            }
        }
        return false;
    }

    private RobomarketProductModel getProductFromPage(Item item) {
        Resource productparsys = null;
        try {
            productparsys = resolverFactory.getResourceResolver(null).getResource(RobomarketProductServlet.SERVLET_PATH + "/root/responsivegrid/productparsys/productpar");
        } catch (LoginException e) {
            return null;
        }
        if (Objects.nonNull(productparsys)) {
            Iterator<Resource> resourceIterator = productparsys.listChildren();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();
                if (resource.isResourceType(ROBOMARKET_PRODUCT_RESOURCE_TYPE)) {
                    RobomarketProductModel product = resource.adaptTo(RobomarketProductModel.class);
                    if (item.getOfferId().equals(product.getOfferId())) {
                        return product;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String handleYaReservationRequest(JSONObject requestData) throws JsonParseException {
        YaReservationRequest yaReservationRequest = gson.fromJson(requestData.toString(), YaReservationRequest.class);
        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;
        try {
            Date paymentDueOrder = robomarketOrdersService.getMinPaymentDueByInvoiceId(yaReservationRequest.getInvoiceId());
            Date paymentDue = new Date(RESERVATION_TIME + paymentDueOrder.getTime());
            ReservationSuccess reservationSuccess = new ReservationSuccess(yaReservationRequest.getOrderId(), yaReservationRequest.getInvoiceId(), paymentDue);
            robomarketOrdersService.yaReserveRobomarketOrder(yaReservationRequest, paymentDue);
            jsonResponseFields = gson.toJsonTree(reservationSuccess);
            jsonStatus.add(RobomarketJsonKeys.RESERVATION_SUCCESS, jsonResponseFields);

        } catch (NoSuchOrderException e) {
            ReservationFailure reservationFailure = new ReservationFailure(yaReservationRequest.getOrderId(), ErrorJson.ERROR_CODE_FAIL);
            jsonResponseFields = gson.toJsonTree(reservationFailure);
            jsonStatus.add(RobomarketJsonKeys.RESERVATION_FAILURE, jsonResponseFields);
        }
        jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);
        return gson.toJson(jsonResponce);
    }

    @Override
    public String handlePurchaseRequest(JSONObject requestData) throws JsonParseException, OrderOverdueException, NoSuchOrderException {
        PurchaseRequest purchaseRequest = gson.fromJson(requestData.toString(), PurchaseRequest.class);
        robomarketOrdersService.registerPurchaseRobomarketOrder(purchaseRequest);

        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;

        PurchaseResponce purchaseResponce = new PurchaseResponce(purchaseRequest.getOrderId(), ErrorJson.ERROR_CODE_OK);
        jsonResponseFields = gson.toJsonTree(purchaseResponce);
        jsonStatus.add(RobomarketJsonKeys.PURCHASE_RESPONSE, jsonResponseFields);

        Customer customer = purchaseRequest.getCustomer();
        Map<String, String> templateValuesMap = createTemplateValuesMap(purchaseRequest);
        sendMailService.sendSuccessEmailToCustomer(customer.getEmail(), templateValuesMap);

        jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);

        return gson.toJson(jsonResponce);

    }

    private Map<String, String> createTemplateValuesMap(PurchaseRequest purchaseRequest) throws NoSuchOrderException {
        Map<String, String> templateValuesMap = new HashMap<>();
        for (Item item : purchaseRequest.getItems()) {
            templateValuesMap.put("%product_name%", item.getItemTitle().getValue());

            RobomarketProductModel product = getProductFromPage(item);
            templateValuesMap.put("%product_description%", Objects.nonNull(product) ? product.getDescription() : "");
            templateValuesMap.put("%product_price%", String.valueOf(item.getPrice()));

            String confirmationLinkParam = robomarketOrdersService.getConfirmationLinkParameterByInvoiceId(purchaseRequest.getInvoiceId());
            String confirmationLink = "http://localhost:4502" + EmailConfirmationServlet.SERVLET_PATH +
                    "?u=" + confirmationLinkParam;
            templateValuesMap.put("%confirmation_link%", confirmationLink);
        }
        return templateValuesMap;
    }

    @Override
    public String handleCancellationRequest(JSONObject requestData) throws JsonParseException, NoSuchOrderException {
        CancellationRequest cancellationRequest = gson.fromJson(requestData.toString(), CancellationRequest.class);
        robomarketOrdersService.cancelRobomarketOrder(cancellationRequest);
        JsonObject jsonResponce = new JsonObject();
        JsonObject jsonStatus = new JsonObject();
        JsonElement jsonResponseFields;

        CancellationResponse cancellationResponse = new CancellationResponse(cancellationRequest.getInvoiceId(),
                cancellationRequest.getOrderId(), ErrorJson.ERROR_CODE_OK);
        jsonResponseFields = gson.toJsonTree(cancellationResponse);
        jsonStatus.add(RobomarketJsonKeys.CANCELLATION_RESPONSE, jsonResponseFields);
        jsonResponce.add(RobomarketJsonKeys.ROBOMARKET, jsonStatus);

        return gson.toJson(jsonResponce);
    }
}
