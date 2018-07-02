package com.github.f.plan.econnoisseur.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;

import java.math.BigDecimal;

/**
 * PreTradeInfo
 * 交易属性
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月01日 15:32:00
 */
public class PreTradeInfo {
    private BigDecimal price;
    private BigDecimal amount;

    private OrderOperation priority;
    private OrderOperation second;

    public BigDecimal getPrice() {
        return price;
    }

    public PreTradeInfo setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PreTradeInfo setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public OrderOperation getPriority() {
        return priority;
    }

    public PreTradeInfo setPriority(OrderOperation priority) {
        this.priority = priority;
        return this;
    }

    public OrderOperation getSecond() {
        return second;
    }

    public PreTradeInfo setSecond(OrderOperation second) {
        this.second = second;
        return this;
    }
}
