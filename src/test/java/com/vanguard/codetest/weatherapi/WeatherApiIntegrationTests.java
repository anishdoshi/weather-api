package com.vanguard.codetest.weatherapi;

import com.vanguard.codetest.weatherapi.config.OpenWeatherMapConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
public class WeatherApiIntegrationTests {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MockRestServiceServer mockServer;

  @Autowired
  private OpenWeatherMapConfig openWeatherMapConfig;

  @BeforeEach
  void setUp() {
    this.mockServer.reset();
  }

  @Test
  void givenNoApiKeyIsUsed_whenGetWeatherIsCalled_thenUnauthorisedIsReturnedWithCorrectMessage() throws Exception {
    this.mockMvc.perform(get("/weather"))
      .andExpect(status().isUnauthorized())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("errorMessage", is("Invalid API Key")));
  }

  @Test
  void givenInvalidApiKeyIsUsed_whenGetWeatherIsCalled_thenUnauthorisedIsReturnedWithCorrectMessage() throws Exception {
    this.mockMvc.perform(get("/weather")
        .header("X-API-KEY", "InvalidKey"))
      .andExpect(status().isUnauthorized())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("errorMessage", is("Invalid API Key")));
  }

  @Test
  void givenValidApiKeyIsUsed_whenGetWeatherIsCalledWithoutCity_thenBadRequestIsReturnedWithCorrectMessage() throws Exception {
    this.mockMvc.perform(get("/weather")
        .header("X-API-KEY", "API-KEY-1"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("detail", is("Required parameter 'city' is not present.")));
  }

  @Test
  void givenValidApiKeyIsUsed_whenGetWeatherIsCalledWithCityOnly_thenCorrectResponseReturned() throws Exception {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "Melbourne"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"weather\": [{\"description\": \"clear sky\"}]}"));

    this.mockMvc.perform(get("/weather")
        .queryParam("city", "Melbourne")
        .header("X-API-KEY", "API-KEY-1"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("description", is("clear sky")));
  }

  @Test
  void givenValidApiKeyIsUsed_whenGetWeatherIsCalledWithInvalidCityOrCountry_thenBadRequestIsReturned() throws Exception {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "InvalidCity,uk"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"cod\": \"404\", \"message\": \"city not found\"}"));

    this.mockMvc.perform(get("/weather")
        .queryParam("city", "InvalidCity")
        .queryParam("country", "uk")
        .header("X-API-KEY", "API-KEY-1"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("errorMessage",
        is("Cannot lookup weather information for city: [InvalidCity] and country: [uk]")));
  }

  @Test
  void givenValidApiKeyIsUsed_whenGetWeatherIsCalledWithCityAndCountry_thenCorrectResponseReturned() throws Exception {
    this.mockServer
      .expect(request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "London,uk"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"weather\": [{\"description\": \"clear sky\"}]}"));

    this.mockMvc.perform(get("/weather")
        .queryParam("city", "London")
        .queryParam("country", "uk")
        .header("X-API-KEY", "API-KEY-1"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("description", is("clear sky")));
  }

  @Test
  void givenValidApiKeyIsUsed_whenGetWeatherIsCalledOverFiveTimesAnHour_thenRequestIsThrottled() throws Exception {
    this.mockServer
      .expect(times(5), request -> assertEquals("/data/2.5/weather", request.getURI().getPath()))
      .andExpect(method(HttpMethod.GET))
      .andExpect(queryParam("q", "London"))
      .andExpect(queryParam("appid", openWeatherMapConfig.getAppid()))
      .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"weather\": [{\"description\": \"clear sky\"}]}"));
    for (int i = 1; i <= 5; i++) {
      this.mockMvc.perform(get("/weather")
          .queryParam("city", "London")
          .header("X-API-KEY", "API-KEY-2"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().longValue("X-Rate-Limit-Remaining", 5 - i))
        .andExpect(jsonPath("description", is("clear sky")));
    }
    this.mockMvc.perform(get("/weather")
        .queryParam("city", "London")
        .header("X-API-KEY", "API-KEY-2"))
      .andExpect(status().isTooManyRequests())
      .andExpect(header().exists("X-Rate-Limit-Retry-After-Seconds"));
  }
}
