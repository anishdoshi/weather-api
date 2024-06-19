package com.vanguard.codetest.weatherapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("open-weather-map")
public class OpenWeatherMapConfig {
  private String hostname;
  private String path;
  private String appid;
}
