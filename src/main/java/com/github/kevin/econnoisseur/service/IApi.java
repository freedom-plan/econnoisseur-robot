package com.github.kevin.econnoisseur.service;

import com.github.kevin.econnoisseur.dto.Balances;
import com.github.kevin.econnoisseur.dto.Order;
import com.github.kevin.econnoisseur.dto.Orders;
import com.github.kevin.econnoisseur.dto.Ticker;
import com.github.kevin.econnoisseur.model.CurrencyPair;
import com.github.kevin.econnoisseur.model.OrderOperation;
import com.github.kevin.econnoisseur.model.OrderType;

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

    Order trade(CurrencyPair pair, OrderType type, OrderOperation operation, BigDecimal price, BigDecimal amount);

    Orders pandingOrders(CurrencyPair pair);
    Orders finishedOrders(CurrencyPair pair);

    Order getOrder(CurrencyPair pair, String orderId);
    Order cancelOrder(CurrencyPair pair, String orderId);
}
