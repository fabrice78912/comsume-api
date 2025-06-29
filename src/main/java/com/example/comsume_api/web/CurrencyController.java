package com.example.comsume_api.web;

import com.example.comsume_api.dto.ExchangeRateResponse;
import com.example.comsume_api.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Tag(name = "Taux de change", description = "API pour récupérer les taux de change")
@RequestMapping("/api/rates")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    @Operation(summary = "Obtenir le taux de change courant")
    public Mono<ExchangeRateResponse> getRates(@RequestParam("source") String source,
                                               @RequestParam("currencies") String currencies) {
        return currencyService.getExchangeRate(source, currencies);
    }


    @GetMapping("/currencies")
    public Mono<Map<String, String>> getCurrencies() {
        return currencyService.getCurrencies();
    }

    @GetMapping("/currencies/count")
    public Mono<Map<String, Object>> countCurrencies() {
        return currencyService.countCurrencies()
                .map(count -> Map.of("count", count));
    }

}

