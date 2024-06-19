package com.vanguard.codetest.weatherapi.api.security;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationService {

  private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
  private static final Set<String> ALLOWED_API_KEYS = new HashSet<>() {{
    this.add("API-KEY-1");
    this.add("API-KEY-2");
    this.add("API-KEY-3");
    this.add("API-KEY-4");
    this.add("API-KEY-5");
  }};

  public static Authentication getAuthentication(final HttpServletRequest request) {
    final String requestApiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
    if (requestApiKey == null || !ALLOWED_API_KEYS.contains(requestApiKey)) {
      throw new BadCredentialsException("Invalid API Key");
    }
    return new ApiKeyAuthentication(requestApiKey, AuthorityUtils.NO_AUTHORITIES);
  }
}
