package robo.market.core.servlets;

import com.google.gson.JsonParseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import robo.market.core.exceptions.PurchaseRequestException;
import robo.market.core.robomarketutils.constants.RobomarketJsonKeys;
import robo.market.core.services.RobomarketHandleClaimService;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Objects;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Robo.Market Product Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes=" + "robomarket-product/components/structure/page",
                "sling.servlet.extensions=" + "json"
        })
public class RobomarketProductServlet extends SlingAllMethodsServlet {

    private static final String SECRET_PHRASE = "PokaChtoNetu";
    private static final String HEADER_ROBOSIGNATURE = "RoboSignature";

    @Reference
    private RobomarketHandleClaimService robomarketHandleClaimService;

    private static String servletCallPath;

    public static String getServletCallPath() {
        return servletCallPath;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        servletCallPath = request.getResource().getPath();

        String contentType = request.getHeader("Content-Type");
        StringBuilder requestData = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        while (Objects.nonNull(line)) {
            requestData.append(line);
            line = reader.readLine();
        }
        String requestString = requestData.toString();

        String requestSignature = request.getHeader(HEADER_ROBOSIGNATURE);
        String calculatedRequestSignature = DigestUtils.md5Hex(requestString + SECRET_PHRASE);
        if (Objects.isNull(requestSignature) || !requestSignature.equalsIgnoreCase(calculatedRequestSignature)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String responseString = "";
        //TODO think if this check really needed and if yes, how to perform it
        if (Objects.nonNull(contentType) && contentType.toLowerCase().contains("application/json")) {
            try {
                JSONObject jsonObject = new JSONObject(requestString);
                JSONObject jsonRobomarket = jsonObject.getJSONObject(RobomarketJsonKeys.ROBOMARKET);
                Iterator<String> requestTypesIterator = jsonRobomarket.keys();
                String requestTypeName = requestTypesIterator.next();
                JSONObject jsonRequestBody = jsonRobomarket.getJSONObject(requestTypeName);

                switch (requestTypeName) {
                    case RobomarketJsonKeys.RESERVATION_REQUEST:
                        responseString = robomarketHandleClaimService.handleReservationRequest(jsonRequestBody);
                        break;
                    case RobomarketJsonKeys.YA_RESERVATION_REQUEST:
                        responseString = robomarketHandleClaimService.handleYaReservationRequest(jsonRequestBody);
                        break;
                    case RobomarketJsonKeys.CANCELLATION_REQUEST:
                        responseString = robomarketHandleClaimService.handleCancellationRequest(jsonRequestBody);
                        break;
                    case RobomarketJsonKeys.PURCHASE_REQUEST:
                        responseString = robomarketHandleClaimService.handlePurchaseRequest(jsonRequestBody);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                }

                String calculatedResponseSignature = DigestUtils.md5Hex(responseString + SECRET_PHRASE);
                PrintWriter printWriter = response.getWriter();
                printWriter.print(responseString);
                response.addHeader(HEADER_ROBOSIGNATURE, calculatedResponseSignature);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (JSONException | JsonParseException e) {

                // TODO change logger, see slf4j
                log("Error occurred during JSON processing.", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (PurchaseRequestException e) {
                log("Error occurred during processing " + PurchaseRequestException.REQUEST_TYPE, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception e) {
                log("Something went wrong...", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
