package com.vanguard.codetest.weatherapi.dao;

import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;

public interface WeatherInfoDao {
  WeatherInfo getWeatherInfo(String city, String country);
}
