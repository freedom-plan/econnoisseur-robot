package com.github.f.plan.econnoisseur.exchanges.coinex.configuration;

import com.github.f.plan.econnoisseur.exchanges.coinex.service.CoinexApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CoinexConfiguration
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 09:32:00
 */
@Configuration
@ConditionalOnProperty(name = "econnoisseur.coinex.enabled", havingValue = "true")
@EnableConfigurationProperties(CoinexProperties.class)
public class CoinexConfiguration {
    @Autowired
    private CoinexProperties coinexProperties;

    @Bean
    public CoinexApi coinexApi() {
        return new CoinexApi(coinexProperties.getUrlPrefix(), coinexProperties.getAccessId(), coinexProperties.getSecretKey());
    }
}
