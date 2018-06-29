package com.github.kevin.econnoisseur.exchanges.kkcoin.configuration;

import com.github.kevin.econnoisseur.exchanges.kkcoin.service.StockApi;
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
@EnableConfigurationProperties(KKCoinProperties.class)
public class KKCoinConfiguration {
    @Autowired
    private KKCoinProperties kkCoinProperties;

    @Bean
    public StockApi stockApi() {
        return new StockApi(kkCoinProperties.getUrlPrefix(), kkCoinProperties.getApiKey(), kkCoinProperties.getPrivateKey());
    }
}
