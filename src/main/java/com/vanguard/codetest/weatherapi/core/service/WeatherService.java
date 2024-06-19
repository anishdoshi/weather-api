package com.vanguard.codetest.weatherapi.core.service;

import com.vanguard.codetest.weatherapi.core.domain.WeatherQuery;
import com.vanguard.codetest.weatherapi.core.domain.WeatherSummary;

public interface WeatherService {

  WeatherSummary getWeather(WeatherQuery weatherQuery);
}
