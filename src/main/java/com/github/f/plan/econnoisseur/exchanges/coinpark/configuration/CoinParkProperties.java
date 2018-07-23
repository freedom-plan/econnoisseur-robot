package com.github.f.plan.econnoisseur.exchanges.coinpark.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CoinParkProperties
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 09:35:00
 */
@ConfigurationProperties(prefix = "econnoisseur.coinpark")
public class CoinParkProperties {
    /**
     * KKcoin申请的apiKey
     */
    private String apiKey;

    private String apiSecret;

    private String urlPrefix;

    public String getApiKey() {
        return apiKey;
    }

    public CoinParkProperties setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public CoinParkProperties setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public CoinParkProperties setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
        return this;
    }
}
