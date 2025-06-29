package com.example.comsume_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponse {
    private boolean success;
    private long timestamp;
    private String source;
    private String message;
    private int codeStatus;
    private Map<String, Double> quotes;

}

