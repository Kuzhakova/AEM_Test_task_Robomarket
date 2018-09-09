package robo.market.core.services;

import robo.market.core.beans.RobomarketOrder;

public interface RobomarketOrderService {

    RobomarketOrder createRobomarketOrder();

    RobomarketOrder getRobomarketOrder();

    RobomarketOrder updateRobomarketOrder();

    RobomarketOrder cancelRobomarketOrder();
}
