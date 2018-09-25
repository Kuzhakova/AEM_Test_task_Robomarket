package robo.market.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.services.RobomarketOrdersService;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Robo.Market Email Confirmation Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + EmailConfirmationServlet.SERVLET_PATH,
        })
public class EmailConfirmationServlet extends SlingAllMethodsServlet {

    public static final String SERVLET_PATH = "/bin/robomarket-product/emailconfirmation";

    @Reference
    private RobomarketOrdersService robomarketOrdersService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String parameter = request.getParameter("u");
        String url = "/content/robomarket-product/ru.html";
        try {
            if (robomarketOrdersService.confirmRobomarketOrder(parameter)) {
                url += "?u=success";
            }
        } catch (ConfirmationException e) {
            url += "?u=failure";
        } finally {
            response.sendRedirect(url);
        }
    }

}
