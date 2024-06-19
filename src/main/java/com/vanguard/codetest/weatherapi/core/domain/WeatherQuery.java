package com.vanguard.codetest.weatherapi.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WeatherQuery {
  private String city;
  private String country;
}
