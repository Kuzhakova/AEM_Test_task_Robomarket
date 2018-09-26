package robo.market.core.services;

import com.google.gson.JsonParseException;
import org.json.JSONObject;
import robo.market.core.exceptions.CancellationRequestException;
import robo.market.core.exceptions.PurchaseRequestException;

public interface RobomarketHandleClaimService {

    long RESERVATION_TIME = 1800000;

    String handleReservationRequest(JSONObject requestData) throws JsonParseException;

    String handleYaReservationRequest(JSONObject requestData) throws JsonParseException;

    String handlePurchaseRequest(JSONObject requestData) throws JsonParseException, PurchaseRequestException;

    String handleCancellationRequest(JSONObject requestData) throws JsonParseException, CancellationRequestException;
}