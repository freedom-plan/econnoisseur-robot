package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderStatus;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderType;

import java.math.BigDecimal;

/**
 * Order
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 15:27:00
 */
public class Order extends BaseResp {
    private String id;
    // 交易的
    private BigDecimal amount;
    // 交易的
    private BigDecimal price;
    private OrderType type;
    private OrderOperation operation;
    // 各个平台不一样 status
    private OrderStatus status;
    private BigDecimal totalAmount;
    // 交易的
    private BigDecimal totalPrice;

    public Order() {
        super(Code.OK);
    }

    public Order(Code code) {
        super(code);
    }

    public String getId() {
        return id;
    }

    public Order setId(String id) {
        this.id = id;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Order setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Order setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderType getType() {
        return type;
    }

    public Order setType(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderOperation getOperation() {
        return operation;
    }

    public Order setOperation(OrderOperation operation) {
        this.operation = operation;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Order setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Order setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Order setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
}
