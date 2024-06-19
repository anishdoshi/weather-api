package com.vanguard.codetest.weatherapi.dao;

import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends CrudRepository<WeatherInfo, Long> {
  
  Optional<WeatherInfo> findByCity(String city);

  Optional<WeatherInfo> findByCityAndCountry(String city, String country);
}
