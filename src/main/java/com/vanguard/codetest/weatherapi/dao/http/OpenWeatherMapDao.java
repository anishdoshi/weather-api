package com.vanguard.codetest.weatherapi.dao.http;

import com.vanguard.codetest.weatherapi.config.OpenWeatherMapConfig;
import com.vanguard.codetest.weatherapi.dao.WeatherInfoDao;
import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;
import com.vanguard.codetest.weatherapi.exception.InvalidOpenWeatherMapRequestException;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class OpenWeatherMapDao implements WeatherInfoDao {

  private RestClient.Builder restClientBuilder;
  private OpenWeatherMapConfig openWeatherMapConfig;

  @Override
  public WeatherInfo getWeatherInfo(final String city, final String country) {
    final String response = this.restClientBuilder.build().get()
      .uri(uriBuilder -> {
        final URI uri = uriBuilder
          .scheme("https")
          .host(this.openWeatherMapConfig.getHostname())
          .path(this.openWeatherMapConfig.getPath())
          .queryParam("q", createQuery(city, country))
          .queryParam("appid", this.openWeatherMapConfig.getAppid())
          .build();
        return uri;
      })
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, (request, response1) -> {
        throw new InvalidOpenWeatherMapRequestException(city, country,
          IOUtils.toString(response1.getBody(), StandardCharsets.UTF_8));
      })
      .body(String.class);
    return WeatherInfo.builder()
      .city(city)
      .country(country)
      .response(response)
      .build();
  }

  private static String createQuery(final String city, final String country) {
    return country == null || country.isEmpty() ? city : city + "," + country;
  }
}

