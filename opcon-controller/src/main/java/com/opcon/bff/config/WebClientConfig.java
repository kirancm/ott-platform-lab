package com.opcon.bff.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Bean
    @Qualifier("cwmWebClient")
    public WebClient cwmWebClient(
            WebClient.Builder builder,
            DownstreamProperties downstreamProperties,
            OpConWebClientProperties webClientProperties,
            ExchangeStrategies exchangeStrategies
    ) {
        return baseClient(builder, downstreamProperties.cwm().baseUrl(), webClientProperties, exchangeStrategies);
    }

    @Bean
    @Qualifier("searchWebClient")
    public WebClient searchWebClient(
            WebClient.Builder builder,
            DownstreamProperties downstreamProperties,
            OpConWebClientProperties webClientProperties,
            ExchangeStrategies exchangeStrategies
    ) {
        return baseClient(builder, downstreamProperties.search().baseUrl(), webClientProperties, exchangeStrategies);
    }

    @Bean
    @Qualifier("contentWebClient")
    public WebClient contentWebClient(
            WebClient.Builder builder,
            DownstreamProperties downstreamProperties,
            OpConWebClientProperties webClientProperties,
            ExchangeStrategies exchangeStrategies
    ) {
        return baseClient(builder, downstreamProperties.content().baseUrl(), webClientProperties, exchangeStrategies);
    }

    private WebClient baseClient(
            WebClient.Builder builder,
            String baseUrl,
            OpConWebClientProperties webClientProperties,
            ExchangeStrategies exchangeStrategies
    ) {
        int timeoutSeconds = webClientProperties.timeoutSeconds();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));

        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
