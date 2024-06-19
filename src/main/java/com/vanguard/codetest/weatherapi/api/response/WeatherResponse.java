package com.vanguard.codetest.weatherapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WeatherResponse {
  private String city;
  private String country;
  private String description;
}
