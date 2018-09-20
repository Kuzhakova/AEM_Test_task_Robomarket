package robo.market.core.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface RobomarketHandleClaimService {

    long RESERVATION_TIME = 1800000;

    String handleReservationRequest(JSONObject requestData) throws IOException, JSONException;

    String handleYaReservationRequest(JSONObject requestData) throws IOException, JSONException;

    String handlePurchaseRequest(JSONObject requestData) throws IOException, JSONException;

    String handleCancellationRequest(JSONObject requestData) throws IOException, JSONException;
}