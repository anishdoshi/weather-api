package com.vanguard.codetest.weatherapi.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanguard.codetest.weatherapi.api.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class AuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
    throws IOException, ServletException {
    try {
      final Authentication authentication = AuthenticationService.getAuthentication((HttpServletRequest) request);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (final BadCredentialsException exp) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      final ErrorResponse errorResponse = ErrorResponse.builder().errorMessage(exp.getMessage()).build();
      final PrintWriter writer = response.getWriter();
      writer.write(new ObjectMapper().writeValueAsString(errorResponse));
      writer.flush();
      writer.close();
      return;
    }

    filterChain.doFilter(request, response);
  }
}
