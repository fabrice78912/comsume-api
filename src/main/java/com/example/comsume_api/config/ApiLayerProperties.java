package com.example.comsume_api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "apilayer")
public class ApiLayerProperties {
    private String baseUrl;
    private String apiKey;
}
