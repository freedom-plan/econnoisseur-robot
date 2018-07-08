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

    private BigDecimal baseAmount;
    private BigDecimal counterAmount;

    private boolean suspend = false;

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

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public PreTradeInfo setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
        return this;
    }

    public BigDecimal getCounterAmount() {
        return counterAmount;
    }

    public PreTradeInfo setCounterAmount(BigDecimal counterAmount) {
        this.counterAmount = counterAmount;
        return this;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public PreTradeInfo setSuspend(boolean suspend) {
        this.suspend = suspend;
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
