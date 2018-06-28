package com.github.kevin.econnoisseur.exchanges.coinex.configuration;

import com.github.kevin.econnoisseur.exchanges.coinex.service.StockApi;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableConfigurationProperties(CoinexProperties.class)
public class CoinexConfiguration {
    @Autowired
    private CoinexProperties coinexProperties;

    @Bean
    public StockApi stockApi() {
        return new StockApi(coinexProperties.getUrlPrefix(), coinexProperties.getAccessId(), coinexProperties.getSecretKey());
    }
}
