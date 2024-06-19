package com.vanguard.codetest.weatherapi.dao.entity;

import com.jayway.jsonpath.JsonPath;
import com.vanguard.codetest.weatherapi.core.domain.WeatherSummary;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherInfo {

  @Id
  @GeneratedValue
  private Long id;
  private String city;
  private String country;
  @Column
  @Lob
  private String response;

  public WeatherSummary toDomain() {
    return WeatherSummary.builder()
      .city(this.city)
      .country(this.country)
      .description(JsonPath.parse(this.response)
        .read("$.weather[0].description"))
      .build();
  }
}
