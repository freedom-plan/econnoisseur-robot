package com.github.f.plan.econnoisseur.exchanges.coinpark.configuration;

import com.github.f.plan.econnoisseur.exchanges.coinpark.service.CoinParkApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CoinParkConfiguration
 *
 * @author jimmy
 * @since version
 * 2018年07月13日 09:32:00
 */
@Configuration
@ConditionalOnProperty(name = "econnoisseur.coinpark.enabled", havingValue = "true")
@EnableConfigurationProperties(CoinParkProperties.class)
public class CoinParkConfiguration {
    @Autowired
    private CoinParkProperties coinParkProperties;

    @Bean
    public CoinParkApi kkcoinApi() throws Exception {
        return new CoinParkApi(coinParkProperties.getUrlPrefix(), coinParkProperties.getApiKey(), coinParkProperties.getApiSecret());
    }
}
