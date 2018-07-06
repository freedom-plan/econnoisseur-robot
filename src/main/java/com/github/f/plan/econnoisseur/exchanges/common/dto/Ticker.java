package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;

import java.math.BigDecimal;

/**
 * Ticker
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 14:21:00
 */
public class Ticker extends BaseResp {
    // buy
    private BigDecimal bid;
    // sell
    private BigDecimal ask;

    private BigDecimal last;

    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal vol;

    public Ticker(Code code) {
        super(code);
    }

    public BigDecimal getBid() {
        return bid;
    }

    public Ticker setBid(BigDecimal bid) {
        this.bid = bid;
        return this;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public Ticker setAsk(BigDecimal ask) {
        this.ask = ask;
        return this;
    }

    public BigDecimal getLast() {
        return last;
    }

    public Ticker setLast(BigDecimal last) {
        this.last = last;
        return this;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public Ticker setHigh(BigDecimal high) {
        this.high = high;
        return this;
    }

    public BigDecimal getLow() {
        return low;
    }

    public Ticker setLow(BigDecimal low) {
        this.low = low;
        return this;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public Ticker setVol(BigDecimal vol) {
        this.vol = vol;
        return this;
    }
}