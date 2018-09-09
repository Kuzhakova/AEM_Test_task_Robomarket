package robo.market.core.servlets;

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
                "sling.servlet.extensions=" + "html"
        })
public class RobomarketProductServlet extends SlingAllMethodsServlet {

    private static final String SECRET_PHRASE = "PokaChtoNetu";
    private static final String HEADER_ROBOSIGNATURE = "RoboSignature";

    @Reference
    private RobomarketHandleClaimService robomarketHandleClaimService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
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

        String responseData = "";
        if ("application/json".equalsIgnoreCase(contentType) || "application/json; charset=utf-8".equalsIgnoreCase(contentType)) {
            try {
                JSONObject jsonObject = new JSONObject(requestString);
                JSONObject robomarket = jsonObject.getJSONObject(RobomarketJsonKeys.ROBOMARKET);
                Iterator<String> requestTypesIterator = robomarket.keys();
                String requestTypeName = requestTypesIterator.next();
                JSONObject requestBody = robomarket.getJSONObject(requestTypeName);

                switch (requestTypeName) {
                    case RobomarketJsonKeys.RESERVATION_REQUEST:
                        responseData = robomarketHandleClaimService.processReservationRequest(requestBody);
                        break;
                    case RobomarketJsonKeys.YA_RESERVATION_REQUEST:
                        responseData = robomarketHandleClaimService.processYaReservationRequest(requestBody);
                        break;
                    case RobomarketJsonKeys.CANCELLATION_REQUEST:
                        responseData = robomarketHandleClaimService.processCancellationRequest(requestBody);
                        break;
                    case RobomarketJsonKeys.PURCHASE_REQUEST:
                        responseData = robomarketHandleClaimService.processPurchaseRequest(requestBody);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        break;
                }

                String calculatedResponceSignature = DigestUtils.md5Hex(responseData + SECRET_PHRASE);
                PrintWriter printWriter = response.getWriter();
                printWriter.print(responseData);

                response.addHeader(HEADER_ROBOSIGNATURE, calculatedResponceSignature);
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (JSONException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
