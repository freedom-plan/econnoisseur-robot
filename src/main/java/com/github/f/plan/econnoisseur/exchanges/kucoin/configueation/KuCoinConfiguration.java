package com.github.f.plan.econnoisseur.exchanges.kucoin.configueation;

import com.github.f.plan.econnoisseur.exchanges.kkcoin.service.KkcoinApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc KuCoinConfiguration
 * @date 2018/7/26
 */
@Configuration
@ConditionalOnProperty(name = "econnoisseur.kucoin.enabled", havingValue = "true")
@EnableConfigurationProperties(KuCoinProperties.class)
public class KuCoinConfiguration {
    @Autowired
    private KuCoinProperties kuCoinProperties;

    @Bean
    public KkcoinApi kkcoinApi() throws Exception {
        return new KkcoinApi(kuCoinProperties.getUrlPrefix(), kuCoinProperties.getApiKey(), kuCoinProperties.getSecretKey());
    }
}
