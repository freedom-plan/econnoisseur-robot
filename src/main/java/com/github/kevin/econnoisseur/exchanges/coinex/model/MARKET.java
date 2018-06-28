package com.github.kevin.econnoisseur.exchanges.coinex.model;

/**
 *
 * MARKET
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:57:00
 */
public enum MARKET {
    MARKET_ETHCNY("ETHCNY"),
    MARKET_ETHBTC("ETHBTC"),
    MARKET_BTCCNY("BTCCNY"),
    MARKET_BBNBTC("BBNBTC"),
    MARKET_BBNBCH("BBNBCH"),
    MARKET_NANOBTC("NANOBTC"),
    ;

    private String typeName;

    private MARKET(String typeName) {
        this.typeName = typeName;
    }

    public String toString() {
        return this.typeName;
    }
}
