package com.vanguard.codetest.weatherapi.core.service.impl;

import com.vanguard.codetest.weatherapi.core.domain.WeatherQuery;
import com.vanguard.codetest.weatherapi.dao.WeatherInfoDao;
import com.vanguard.codetest.weatherapi.dao.WeatherRepository;
import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class WeatherServiceImplTest {

  private final WeatherRepository weatherRepository = mock();

  private final WeatherInfoDao weatherInfoDao = mock();

  private WeatherServiceImpl serviceToTest;

  @BeforeEach
  void setUp() {
    this.serviceToTest = new WeatherServiceImpl(this.weatherRepository, this.weatherInfoDao);
  }

  @ParameterizedTest
  @NullAndEmptySource
  public void givenWeatherQueryHasNoCity_whenGetWeatherCalled_thenErrorIsThrown(final String city) {
    assertThatThrownBy(() -> this.serviceToTest.getWeather(
      WeatherQuery.builder()
        .city(city)
        .build()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("City is required");
  }

  @Test
  void givenWeatherQueryHasCityAndCountry_whenGetWeatherCalled_thenBothUsedForDownstreamCalls() {
    final String queryCity = "city1";
    final String queryCountry = "country1";
    final String queryResponse = "{\"weather\": [{\"description\": \"clear sky\"}]}";
    final WeatherInfo weatherInfo = WeatherInfo.builder().city(queryCity).country(queryCountry).response(queryResponse).build();
    when(this.weatherRepository.findByCityAndCountry(queryCity, queryCountry)).thenReturn(Optional.empty())
      .thenReturn(Optional.of(weatherInfo));
    this.serviceToTest.getWeather(WeatherQuery.builder().city(queryCity).country(queryCountry).build());

    verify(this.weatherRepository, times(2)).findByCityAndCountry(queryCity, queryCountry);
    verify(this.weatherRepository, times(1)).save(any());
    verify(this.weatherRepository, never()).findByCity(queryCity);

    verify(this.weatherInfoDao, times(1)).getWeatherInfo(queryCity, queryCountry);

    verifyNoMoreInteractions(this.weatherRepository, this.weatherInfoDao);
  }

  @Test
  void givenWeatherQueryHasCityOnly_whenGetWeatherCalled_thenOnlyCityUsedForDownstreamCalls() {
    final String queryCity = "city1";
    final String queryResponse = "{\"weather\": [{\"description\": \"clear sky\"}]}";
    final WeatherInfo weatherInfo = WeatherInfo.builder().city(queryCity).response(queryResponse).build();
    when(this.weatherRepository.findByCity(queryCity)).thenReturn(Optional.empty())
      .thenReturn(Optional.of(weatherInfo));
    this.serviceToTest.getWeather(WeatherQuery.builder().city(queryCity).build());

    verify(this.weatherRepository, times(2)).findByCity(queryCity);
    verify(this.weatherRepository, times(1)).save(any());
    verify(this.weatherRepository, never()).findByCityAndCountry(eq(queryCity), anyString());

    verify(this.weatherInfoDao, times(1)).getWeatherInfo(queryCity, null);

    verifyNoMoreInteractions(this.weatherRepository, this.weatherInfoDao);
  }

  @Test
  void givenWeatherQueryIsForCityAlreadyInDB_whenGetWeatherCalled_thenCurrentRecordIsUpdated() {
    final String queryCity = "city1";
    final String queryCountry = "country1";
    final String queryResponse1 = "{\"weather\": [{\"description\": \"clear sky\"}]}";
    final String queryResponse2 = "{\"weather\": [{\"description\": \"few clouds\"}]}";
    final WeatherInfo weatherInfo1 = WeatherInfo.builder().city(queryCity).response(queryResponse1).build();
    final WeatherInfo weatherInfo2 = WeatherInfo.builder().city(queryCity).response(queryResponse2).build();
    when(this.weatherRepository.findByCityAndCountry(queryCity, queryCountry)).thenReturn(Optional.of(weatherInfo1))
      .thenReturn(Optional.of(weatherInfo2));
    when(this.weatherInfoDao.getWeatherInfo(queryCity, queryCountry)).thenReturn(weatherInfo2);

    this.serviceToTest.getWeather(WeatherQuery.builder().city(queryCity).country(queryCountry).build());

    final ArgumentCaptor<WeatherInfo> weatherInfoSavedToDbCaptor = ArgumentCaptor.forClass(WeatherInfo.class);
    verify(this.weatherRepository).save(weatherInfoSavedToDbCaptor.capture());

    assertThat(weatherInfoSavedToDbCaptor.getValue().getResponse())
      .isEqualTo(weatherInfo2.getResponse());

  }
}
