package com.vanguard.codetest.weatherapi.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WeatherSummary {
  private String city;
  private String country;
  private String description;
}
