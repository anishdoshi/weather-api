package com.vanguard.codetest.weatherapi.exception;

import lombok.Getter;

@Getter
public class InvalidOpenWeatherMapRequestException extends RuntimeException {

  private final String city;
  private final String country;
  private final String errorResponse;

  private static final String MESSAGE_FORMAT_PREFIX = "Cannot lookup weather information for city: [%s]";
  private static final String MESSAGE_FORMAT_COUNTRY_SUFFIX = " and country: [%s]";

  public InvalidOpenWeatherMapRequestException(final String city, final String country, final String errorResponse) {
    super(String.format(MESSAGE_FORMAT_PREFIX, city) + ((country != null) ? String.format(MESSAGE_FORMAT_COUNTRY_SUFFIX, country) : ""));
    this.city = city;
    this.country = country;
    this.errorResponse = errorResponse;
  }
}
