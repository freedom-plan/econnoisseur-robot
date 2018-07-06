package com.github.f.plan.econnoisseur.exchanges.gate.io.configuration;

import com.github.f.plan.econnoisseur.exchanges.gate.io.service.GateIoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GateIoConfiguration
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 20:32:00
 */
@Configuration
@ConditionalOnProperty(name = "econnoisseur.gateio.enabled", havingValue = "true")
@EnableConfigurationProperties(GateIoProperties.class)
public class GateIoConfiguration {
    @Autowired
    private GateIoProperties gateIoProperties;

    @Bean
    public GateIoApi gateIoApi() throws Exception {
        return new GateIoApi(gateIoProperties.getUrlPrefix(), gateIoProperties.getDataPrefix(), gateIoProperties.getApiKey(), gateIoProperties.getSecretKey());
    }
}