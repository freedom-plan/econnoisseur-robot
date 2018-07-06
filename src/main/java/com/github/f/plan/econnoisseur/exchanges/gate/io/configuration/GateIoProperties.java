package com.github.f.plan.econnoisseur.exchanges.gate.io.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GateIoProperties
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 20:33:00
 */
@ConfigurationProperties(prefix = "econnoisseur.gateio")
public class GateIoProperties {
    private String apiKey;
    private String secretKey;
    private String dataPrefix;
    private String urlPrefix;

    public String getApiKey() {
        return apiKey;
    }

    public GateIoProperties setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public GateIoProperties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getDataPrefix() {
        return dataPrefix;
    }

    public GateIoProperties setDataPrefix(String dataPrefix) {
        this.dataPrefix = dataPrefix;
        return this;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public GateIoProperties setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }
}
