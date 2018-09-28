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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import robo.market.core.exceptions.NoSuchOrderException;
import robo.market.core.exceptions.OrderOverdueException;
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

    private static final String SECRET_PHRASE = "super_secret_phrase";
    private static final String HEADER_ROBOSIGNATURE = "RoboSignature";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private RobomarketHandleClaimService robomarketHandleClaimService;

    public static final String SERVLET_PATH = "/content/robomarket-product/ru/jcr:content";

    /**
     * Reads the body of the SlingHttpServletRequest request using the BufferedReader. It is possible to use only
     * once with the same request, because BufferedReader closes.
     * @param request SlingHttpServletRequest request
     * @return request body string
     * @throws IOException if an input or output exception occurred
     */
    private String readRequestBody(SlingHttpServletRequest request) throws IOException {
        StringBuilder requestData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(request.getReader())) {
            String line = reader.readLine();
            while (Objects.nonNull(line)) {
                requestData.append(line);
                line = reader.readLine();
            }
            return requestData.toString();
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String requestString = readRequestBody(request);
        String requestSignature = request.getHeader(HEADER_ROBOSIGNATURE);
        String calculatedRequestSignature = DigestUtils.md5Hex(requestString + SECRET_PHRASE);
        if (Objects.isNull(requestSignature) || !requestSignature.equalsIgnoreCase(calculatedRequestSignature)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String responseString = "";
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
            logger.error("Error occurred during JSON processing.", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchOrderException | OrderOverdueException e) {
            logger.error("Error occurred during processing request: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
