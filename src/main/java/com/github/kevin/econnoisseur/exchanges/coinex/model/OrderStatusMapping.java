package com.github.kevin.econnoisseur.exchanges.coinex.model;

import com.github.kevin.econnoisseur.model.OrderStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * OrderStatusMapping
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月30日 09:35:00
 */
public class OrderStatusMapping {
    public static Map<String, OrderStatus> map = new HashMap<>();
    static {
        map.put("done", OrderStatus.FILLED);
        map.put("part_deal", OrderStatus.PARTIALLY_FILLED);
        map.put("not_deal", OrderStatus.NEW);
    }

    public static OrderStatus get(String status) {
        OrderStatus orderStatus = map.get(status);
        if (null == orderStatus) {
            orderStatus = OrderStatus.FILLED;
        }
        return orderStatus;
    }
}
