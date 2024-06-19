package com.vanguard.codetest.weatherapi.api.interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitInterceptor implements HandlerInterceptor {

  private static final String HEADER_NAME_API_KEY = "X-API-KEY";
  private static final String HEADER_NAME_X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
  private static final String HEADER_NAME_X_RATE_LIMIT_RETRY_AFTER_SECONDS = "X-Rate-Limit-Retry-After-Seconds";
  private static final int ACCESS_LIMIT_PER_HOUR = 5;
  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
    final String apiKey = request.getHeader(HEADER_NAME_API_KEY);
    if (apiKey == null || apiKey.isEmpty()) {
      response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing Header: X-api-key");
      return false;
    }
    final Bucket tokenBucket = this.resolveBucket(apiKey);
    final ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      response.addHeader(
        HEADER_NAME_X_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
      return true;
    } else {
      final long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
      response.addHeader(HEADER_NAME_X_RATE_LIMIT_RETRY_AFTER_SECONDS, String.valueOf(waitForRefill));
      response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
        "You have exhausted your API Request Quota");
      return false;
    }
  }

  private Bucket resolveBucket(final String apiKey) {
    return this.cache.computeIfAbsent(apiKey, RateLimitInterceptor::newBucket);
  }

  private static Bucket newBucket(final String apiKey) {
    return Bucket.builder()
      .addLimit(limit -> limit.capacity(5).refillIntervally(ACCESS_LIMIT_PER_HOUR, Duration.ofHours(1)))
      .build();
  }
}
