package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2018, 五维引力（上海）数据服务有限公司
 * Orders
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 17:02:00
 */
public class Orders extends BaseResp {
    private List<Order> orders = new ArrayList<>();

    public Orders(Code code) {
        super(code);
    }

    public Orders addOrder(Order order) {
        this.orders.add(order);
        return this;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
