package com.vanguard.codetest.weatherapi.api.exception;

import com.vanguard.codetest.weatherapi.api.response.ErrorResponse;
import com.vanguard.codetest.weatherapi.exception.InvalidOpenWeatherMapRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
  @org.springframework.web.bind.annotation.ExceptionHandler(value = InvalidOpenWeatherMapRequestException.class)
  protected ResponseEntity<Object> handleBadRequest(
    final RuntimeException ex, final WebRequest request) {
    final ErrorResponse errorResponse = ErrorResponse.builder().errorMessage(ex.getMessage()).build();
    return this.handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
