package com.github.kevin.econnoisseur.exchanges.coinex.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 *
 * TradesDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 13:03:00
 */
public class TradesDto {
    private String amount;
    private Long date;
    private Long date_ms;
    private Integer id;
    private String price;
    private String type;

    public BigDecimal getAmount() {
        if (StringUtils.isBlank(amount)) {
            return null;
        }
        return new BigDecimal(amount);
    }

    public TradesDto setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public Long getDate() {
        return date;
    }

    public TradesDto setDate(Long date) {
        this.date = date;
        return this;
    }

    public Long getDate_ms() {
        return date_ms;
    }

    public TradesDto setDate_ms(Long date_ms) {
        this.date_ms = date_ms;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public TradesDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public BigDecimal getPrice() {
        if (StringUtils.isBlank(price)) {
            return null;
        }
        return new BigDecimal(price);
    }

    public TradesDto setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getType() {
        return type;
    }

    public TradesDto setType(String type) {
        this.type = type;
        return this;
    }
}
