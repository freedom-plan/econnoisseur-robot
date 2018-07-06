package com.github.f.plan.econnoisseur.exchanges.common.iface;

import com.github.f.plan.econnoisseur.exchanges.common.dto.Balances;
import com.github.f.plan.econnoisseur.exchanges.common.dto.Order;
import com.github.f.plan.econnoisseur.exchanges.common.dto.Orders;
import com.github.f.plan.econnoisseur.exchanges.common.dto.Ticker;
import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;

import java.math.BigDecimal;

/**
 * IApi
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 12:49:00
 */
public interface IApi {
    Ticker ticker(CurrencyPair pair);

    Balances balances();

    Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount);
    Order market(CurrencyPair pair, OrderOperation operation, BigDecimal amount);

    Orders pandingOrders(CurrencyPair pair);
    Orders finishedOrders(CurrencyPair pair);

    Order getOrder(CurrencyPair pair, String orderId);
    Order cancelOrder(CurrencyPair pair, String orderId);
}
