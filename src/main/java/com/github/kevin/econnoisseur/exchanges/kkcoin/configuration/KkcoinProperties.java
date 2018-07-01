package com.github.kevin.econnoisseur.exchanges.kkcoin.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * KkcoinProperties
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 09:35:00
 */
@ConfigurationProperties(prefix = "econnoisseur.kkcoin")
public class KkcoinProperties {
    /**
     * KKcoin申请的apiKey
     */
    private String apiKey;

    /**
     * KKcoin 上传的公钥对应私钥
     */
    private String privateKey;

    private String urlPrefix;

    public String getApiKey() {
        return apiKey;
    }

    public KkcoinProperties setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public KkcoinProperties setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public KkcoinProperties setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }
}
