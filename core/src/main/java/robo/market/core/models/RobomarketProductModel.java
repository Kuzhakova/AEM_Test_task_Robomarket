package robo.market.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;

import javax.inject.Inject;
import java.math.BigDecimal;

@Model(adaptables = Resource.class)
public class RobomarketProductModel {

    @Inject
    @Required
    private String title;

    @Inject
    @Required
    private String description;

    @Inject
    @Required
    private BigDecimal price;

    @Inject
    @Required
    private String offerId;

    @Inject
    @Required
    private String letterTemplatePath;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getLetterTemplatePath() {
        return letterTemplatePath;
    }
}
