package com.vanguard.codetest.weatherapi.core.service.impl;

import com.vanguard.codetest.weatherapi.core.domain.WeatherQuery;
import com.vanguard.codetest.weatherapi.core.domain.WeatherSummary;
import com.vanguard.codetest.weatherapi.core.service.WeatherService;
import com.vanguard.codetest.weatherapi.dao.WeatherInfoDao;
import com.vanguard.codetest.weatherapi.dao.WeatherRepository;
import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class WeatherServiceImpl implements WeatherService {

  private final WeatherRepository weatherRepository;
  private final WeatherInfoDao weatherInfoDao;

  @Override
  public WeatherSummary getWeather(final WeatherQuery weatherQuery) {
    if (weatherQuery.getCity() == null || weatherQuery.getCity().isEmpty()) {
      throw new IllegalArgumentException("City is required");
    }
    final Optional<WeatherInfo> weatherInfoFromRepository = this.findWeatherInfoFromRepository(weatherQuery);
    final WeatherInfo weatherInfoFromClient = this.weatherInfoDao.getWeatherInfo(weatherQuery.getCity(), weatherQuery.getCountry());
    weatherInfoFromRepository.ifPresentOrElse(
      weatherInfo -> {
        weatherInfo.setResponse(weatherInfoFromClient.getResponse());
        this.weatherRepository.save(weatherInfo);
      },
      () -> this.weatherRepository.save(weatherInfoFromClient));
    return this.findWeatherInfoFromRepository(weatherQuery).get().toDomain();
  }

  private Optional<WeatherInfo> findWeatherInfoFromRepository(final WeatherQuery weatherQuery) {
    return findByCityAndCountry(weatherQuery)
      ? this.weatherRepository.findByCityAndCountry(weatherQuery.getCity(), weatherQuery.getCountry())
      : this.weatherRepository.findByCity(weatherQuery.getCity());
  }

  private static boolean findByCityAndCountry(final WeatherQuery weatherQuery) {
    return weatherQuery.getCountry() != null;
  }
}
