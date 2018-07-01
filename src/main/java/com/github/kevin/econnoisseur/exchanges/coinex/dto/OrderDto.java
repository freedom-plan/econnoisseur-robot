package com.github.kevin.econnoisseur.exchanges.coinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kevin.econnoisseur.dto.Order;
import com.github.kevin.econnoisseur.exchanges.coinex.model.OrderStatusMapping;
import com.github.kevin.econnoisseur.model.OrderOperation;
import com.github.kevin.econnoisseur.model.OrderType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 *
 * OrderDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月27日 12:55:00
 */
public class OrderDto {
    private Long id;
    private String amount;
    private String price;
    private String type;

    @JsonProperty("asset_fee")
    private String assetFee;
    @JsonProperty("avg_price")
    private String avgPrice;
    @JsonProperty("create_time")
    private Long createTime;
    @JsonProperty("deal_amount")
    private String dealAmount;
    @JsonProperty("deal_fee")
    private String dealFee;
    @JsonProperty("deal_money")
    private String dealMoney;
    @JsonProperty("fee_discount")
    private String feeDiscount;
    private String left;
    @JsonProperty("maker_fee_rate")
    private String makerFeeRate;
    private String market;
    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("source_id")
    private String sourceId;
    // not_deal
    private String status;
    @JsonProperty("taker_fee_rate")
    private String takerFeeRate;

    public Long getId() {
        return id;
    }

    public OrderDto setId(Long id) {
        this.id = id;
        return this;
    }

    public BigDecimal getAmount() {
        if (StringUtils.isBlank(amount)) {
            return null;
        }
        return new BigDecimal(amount);
    }

    public OrderDto setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getType() {
        return type;
    }

    public OrderDto setType(String type) {
        this.type = type;
        return this;
    }

    public String getAssetFee() {
        return assetFee;
    }

    public OrderDto setAssetFee(String assetFee) {
        this.assetFee = assetFee;
        return this;
    }

    public BigDecimal getAvgPrice() {
        if (null == avgPrice) {
            return null;
        }
        return new BigDecimal(avgPrice);
    }

    public OrderDto setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public OrderDto setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public BigDecimal getDealAmount() {
        if (null == dealAmount) {
            return null;
        }
        return new BigDecimal(dealAmount);
    }

    public OrderDto setDealAmount(String dealAmount) {
        this.dealAmount = dealAmount;
        return this;
    }

    public String getDealFee() {
        return dealFee;
    }

    public OrderDto setDealFee(String dealFee) {
        this.dealFee = dealFee;
        return this;
    }

    public String getDealMoney() {
        return dealMoney;
    }

    public OrderDto setDealMoney(String dealMoney) {
        this.dealMoney = dealMoney;
        return this;
    }

    public String getFeeDiscount() {
        return feeDiscount;
    }

    public OrderDto setFeeDiscount(String feeDiscount) {
        this.feeDiscount = feeDiscount;
        return this;
    }

    public String getLeft() {
        return left;
    }

    public OrderDto setLeft(String left) {
        this.left = left;
        return this;
    }

    public String getMakerFeeRate() {
        return makerFeeRate;
    }

    public OrderDto setMakerFeeRate(String makerFeeRate) {
        this.makerFeeRate = makerFeeRate;
        return this;
    }

    public String getMarket() {
        return market;
    }

    public OrderDto setMarket(String market) {
        this.market = market;
        return this;
    }

    public String getOrderType() {
        return orderType;
    }

    public OrderDto setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }

    public BigDecimal getPrice() {
        if (StringUtils.isBlank(price)) {
            return null;
        }
        return new BigDecimal(price);
    }

    public OrderDto setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getSourceId() {
        return sourceId;
    }

    public OrderDto setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OrderDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTakerFeeRate() {
        return takerFeeRate;
    }

    public OrderDto setTakerFeeRate(String takerFeeRate) {
        this.takerFeeRate = takerFeeRate;
        return this;
    }

    public static Order convert(OrderDto dto) {
        Order order = null;
        if (null != dto) {
            order = new Order()
                    .setId(String.valueOf(dto.getId()))
                    .setAmount(dto.getDealAmount())
                    .setPrice(dto.getAvgPrice())
                    .setType(OrderType.get(dto.getOrderType()))
                    .setOperation(OrderOperation.get(dto.getType()))
                    .setStatus(OrderStatusMapping.get(dto.getStatus()))
                    .setTotalAmount(dto.getAmount())
                    .setTotalPrice(dto.getPrice());
        }
        return order;
    }
}
