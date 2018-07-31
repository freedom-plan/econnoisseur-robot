package com.github.f.plan.econnoisseur.exchanges.kucoin.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc KuCoinProperties
 * @date 2018/7/26
 */
@ConfigurationProperties(prefix = "econnoisseur.kucoin")
public class KuCoinProperties {
    /**
     * KuCoin 申请的apiKey
     */
    private String apiKey;

    /**
     * KuCoin 密钥
     */
    private String secretKey;

    private String urlPrefix;

    public String getApiKey() {
        return apiKey;
    }

    public KuCoinProperties setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public KuCoinProperties setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }
}
