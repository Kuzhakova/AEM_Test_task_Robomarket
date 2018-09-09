package robo.market.core.services.impl;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import robo.market.core.beans.RobomarketOrder;
import robo.market.core.services.RobomarketOrderService;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for operations with orders."},
        service = RobomarketOrderService.class)

public class RobomarketOrderServiceImpl implements RobomarketOrderService {
    
    @Override
    public RobomarketOrder createRobomarketOrder() {
        return null;
    }

    @Override
    public RobomarketOrder getRobomarketOrder() {
        return null;
    }

    @Override
    public RobomarketOrder updateRobomarketOrder() {
        return null;
    }

    @Override
    public RobomarketOrder cancelRobomarketOrder() {
        return null;
    }
}
