package com.github.kevin.econnoisseur.exchanges.coinex.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CoinexProperties
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 09:35:00
 */
@ConfigurationProperties(prefix = "econnoisseur.coinex")
public class CoinexProperties {
    private String accessId;  //CoinEx申请的apiKey
    private String secretKey;  //CoinEx 申请的secret_key
    private String urlPrefix;

    public String getAccessId() {
        return accessId;
    }

    public CoinexProperties setAccessId(String accessId) {
        this.accessId = accessId;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public CoinexProperties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public CoinexProperties setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }
}
