package com.github.kevin.econnoisseur.exchanges.coinex.dto;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 *
 * TickerDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 12:53:00
 */
public class TickerDto {
    private Long date;
    private Ticker ticker;

    public Long getDate() {
        return date;
    }

    public TickerDto setDate(Long date) {
        this.date = date;
        return this;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public TickerDto setTicker(Ticker ticker) {
        this.ticker = ticker;
        return this;
    }

    public static class Ticker {
        private String buy;
        private String high;
        private String last;
        private String low;
        private String sell;
        private String vol;
        private String open;

        public BigDecimal getBuy() {
            if (StringUtils.isBlank(buy)) {
                return null;
            }
            return new BigDecimal(buy);
        }

        public Ticker setBuy(String buy) {
            this.buy = buy;
            return this;
        }

        public BigDecimal getHigh() {
            if (StringUtils.isBlank(high)) {
                return null;
            }
            return new BigDecimal(high);
        }

        public Ticker setHigh(String high) {
            this.high = high;
            return this;
        }

        public BigDecimal getLast() {
            if (StringUtils.isBlank(last)) {
                return null;
            }
            return new BigDecimal(last);
        }

        public Ticker setLast(String last) {
            this.last = last;
            return this;
        }

        public BigDecimal getLow() {
            if (StringUtils.isBlank(low)) {
                return null;
            }
            return new BigDecimal(low);
        }

        public Ticker setLow(String low) {
            this.low = low;
            return this;
        }

        public BigDecimal getSell() {
            if (StringUtils.isBlank(sell)) {
                return null;
            }
            return new BigDecimal(sell);
        }

        public Ticker setSell(String sell) {
            this.sell = sell;
            return this;
        }

        public BigDecimal getVol() {
            if (StringUtils.isBlank(vol)) {
                return null;
            }
            return new BigDecimal(vol);
        }

        public Ticker setVol(String vol) {
            this.vol = vol;
            return this;
        }

        public BigDecimal getOpen() {
            if (StringUtils.isBlank(open)) {
                return null;
            }
            return new BigDecimal(open);
        }

        public Ticker setOpen(String open) {
            this.open = open;
            return this;
        }
    }
}
