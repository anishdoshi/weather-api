package com.vanguard.codetest.weatherapi.dao.http;

import com.vanguard.codetest.weatherapi.config.OpenWeatherMapConfig;
import com.vanguard.codetest.weatherapi.dao.entity.WeatherInfo;
import com.vanguard.codetest.weatherapi.exception.InvalidOpenWeatherMapRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@AutoConfigureMockRestServiceServer
class OpenWeatherMapDaoTest {
  @Autowired
  private OpenWeatherMapDao openWeatherMapDao;

  @Autowired
  private MockRestServiceServer mockServer;

  @Autowired
  private OpenWeatherMapConfig openWeatherMapConfig;

  @Test
  void givenCityAndCountryIsRequested_whenGetWeatherInfoCalled_thenBothCityAndCountryAreRequestedToOpenWeatherMap() {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "London,uk"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("responseBody"));

    final WeatherInfo weatherInfo = this.openWeatherMapDao.getWeatherInfo("London", "uk");

    assertThat(weatherInfo.getCity()).isEqualTo("London");
    assertThat(weatherInfo.getCountry()).isEqualTo("uk");
    assertThat(weatherInfo.getResponse()).isEqualTo("responseBody");
  }

  @Test
  void givenOnlyCityIsRequested_whenGetWeatherInfoCalled_thenOnlyCityIsRequestedToOpenWeatherMap() {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "London"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("responseBody"));

    final WeatherInfo weatherInfo = this.openWeatherMapDao.getWeatherInfo("London", null);

    assertThat(weatherInfo.getCity()).isEqualTo("London");
    assertThat(weatherInfo.getCountry()).isNull();
    assertThat(weatherInfo.getResponse()).isEqualTo("responseBody");
  }

  @Test
  void givenInvalidCityIsRequested_whenGetWeatherInfoCalled_thenExceptionThrown() {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "InvalidCity"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body("errorBody"));

    assertThatThrownBy(() -> this.openWeatherMapDao.getWeatherInfo("InvalidCity", null))
      .isInstanceOf(InvalidOpenWeatherMapRequestException.class)
      .hasMessage("Cannot lookup weather information for city: [InvalidCity]");
  }
}
