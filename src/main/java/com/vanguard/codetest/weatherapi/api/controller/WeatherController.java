package com.vanguard.codetest.weatherapi.api.controller;

import com.vanguard.codetest.weatherapi.api.response.WeatherResponse;
import com.vanguard.codetest.weatherapi.core.domain.WeatherQuery;
import com.vanguard.codetest.weatherapi.core.domain.WeatherSummary;
import com.vanguard.codetest.weatherapi.core.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@AllArgsConstructor
public class WeatherController {

  private final WeatherService weatherService;

  @GetMapping
  public WeatherResponse getWeather(
    @RequestParam final String city,
    @RequestParam(required = false) final String country) {
    final WeatherSummary weatherSummary = this.weatherService.getWeather(WeatherQuery.builder()
      .city(city)
      .country(country)
      .build());
    return WeatherResponse.builder()
      .city(weatherSummary.getCity())
      .country(weatherSummary.getCountry())
      .description(weatherSummary.getDescription())
      .build();
  }
}
