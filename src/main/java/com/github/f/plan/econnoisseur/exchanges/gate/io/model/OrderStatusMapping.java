package com.github.f.plan.econnoisseur.exchanges.gate.io.model;

import com.github.f.plan.econnoisseur.exchanges.common.model.OrderStatus;

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
        map.put("cancelled", OrderStatus.CANCELED);
        map.put("open", OrderStatus.NEW);
    }

    public static OrderStatus get(String status) {
        OrderStatus orderStatus = map.get(status);
        if (null == orderStatus) {
            orderStatus = OrderStatus.NEW;
        }
        return orderStatus;
    }
}
