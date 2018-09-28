package robo.market.core.services;

import com.google.gson.JsonParseException;
import org.json.JSONObject;
import robo.market.core.exceptions.NoSuchOrderException;
import robo.market.core.exceptions.OrderOverdueException;

public interface RobomarketHandleClaimService {

    String handleReservationRequest(JSONObject requestData) throws JsonParseException;

    String handleYaReservationRequest(JSONObject requestData) throws JsonParseException;

    String handlePurchaseRequest(JSONObject requestData) throws JsonParseException, OrderOverdueException, NoSuchOrderException;

    String handleCancellationRequest(JSONObject requestData) throws JsonParseException, NoSuchOrderException;
}