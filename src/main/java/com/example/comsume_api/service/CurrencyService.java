package com.example.comsume_api.service;


import com.example.comsume_api.config.ApiLayerProperties;
import com.example.comsume_api.dto.CurrencyListResponse;
import com.example.comsume_api.dto.ExchangeRateResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Service
public class CurrencyService {

    private final WebClient webClient;
    private final ApiLayerProperties props;

    public CurrencyService(WebClient webClient, ApiLayerProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    public Mono<ExchangeRateResponse> getExchangeRate(String source, String currencies) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/currency_data/live")
                        .queryParam("source", source)
                        .queryParam("currencies", currencies)
                        .build())
                .header("apikey", props.getApiKey())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return Mono.error(new RuntimeException("Erreur HTTP : " + response.statusCode()));
                })
                .bodyToMono(ExchangeRateResponse.class)
                .timeout(Duration.ofSeconds(5)) // timeout réseau
                .retryWhen(Retry.max(1).filter(e -> e instanceof java.io.IOException)) // 1 retry sur erreur réseau
                .onErrorResume(ex -> {
                    // Retourne un DTO avec message d'erreur au lieu de propager une exception
                    ExchangeRateResponse fallbackResponse = new ExchangeRateResponse();
                    fallbackResponse.setSuccess(false);
                    fallbackResponse.setMessage("Service indisponible : " + ex.getMessage());
                    fallbackResponse.setCodeStatus(503);
                    return Mono.just(fallbackResponse);
                });
    }


    public Mono<Map<String, String>> getCurrencies() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/currency_data/list")
                        .build())
                .header("apikey", props.getApiKey())
                .retrieve()
                .bodyToMono(CurrencyListResponse.class)
                .timeout(Duration.ofSeconds(5))
                .map(CurrencyListResponse::getCurrencies)
                .onErrorResume(ex -> {
                    System.err.println("Erreur lors de l'appel API : " + ex.getMessage());
                    return Mono.just(Collections.singletonMap("error", "Service indisponible"));
                });
    }

    public Mono<Map<String, Object>> countCurrencies() {
        return getCurrencies()
                .flatMap(map -> {
                    if (map.containsKey("error")) {
                        return Mono.just(Map.of("message", "service indisponible"));
                    }
                    return Mono.just(Map.of("count", map.size()));
                });
    }


}
