package robo.market.core.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface RobomarketHandleClaimService {

    long RESERVATION_TIME = 1800000;

    String processReservationRequest(JSONObject requestData) throws IOException, JSONException;

    String processYaReservationRequest(JSONObject requestData) throws IOException, JSONException;

    String processPurchaseRequest(JSONObject requestData) throws IOException, JSONException;

    String processCancellationRequest(JSONObject requestData) throws IOException, JSONException;
}