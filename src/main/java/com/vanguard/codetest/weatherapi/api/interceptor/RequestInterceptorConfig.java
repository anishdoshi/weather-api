package com.vanguard.codetest.weatherapi.api.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RequestInterceptorConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(new RateLimitInterceptor())
      .addPathPatterns("/weather");
  }
}
