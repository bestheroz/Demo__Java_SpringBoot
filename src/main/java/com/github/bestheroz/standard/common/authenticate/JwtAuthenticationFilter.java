package com.github.bestheroz.standard.common.authenticate;

import static com.github.bestheroz.standard.config.SecurityConfig.GET_PUBLIC;
import static com.github.bestheroz.standard.config.SecurityConfig.POST_PUBLIC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String REQUEST_COMPLETE_EXECUTE_TIME =
      "{} ....... Request Complete Execute Time ....... : {}";
  private static final String REQUEST_PARAMETERS = "<{}>{}?{}";

  private final JwtTokenProvider jwtTokenProvider;
  private final List<AntPathRequestMatcher> publicGetPaths =
      Arrays.stream(GET_PUBLIC).map(AntPathRequestMatcher::new).toList();
  private final List<AntPathRequestMatcher> publicPostPaths =
      Arrays.stream(POST_PUBLIC).map(AntPathRequestMatcher::new).toList();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = new UrlPathHelper().getPathWithinApplication(request);

    if (requestURI.equals("/")) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    if (!requestURI.startsWith("/api/v1/health/")) {
      log.info(
          REQUEST_PARAMETERS,
          request.getMethod(),
          requestURI,
          StringUtils.defaultString(request.getQueryString()));
    }

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    try {
      if (isPublicPath(request)) {
        filterChain.doFilter(request, response);
        return;
      }

      String token = jwtTokenProvider.resolveAccessToken(request);
      if (token == null) {
        log.info("No access token found");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      if (!jwtTokenProvider.validateToken(token)) {
        log.info("Invalid access token - refresh token required");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      UserDetails userDetails = jwtTokenProvider.getOperator(token);
      SecurityContextHolder.getContext()
          .setAuthentication(
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities()));

      filterChain.doFilter(request, response);
    } finally {
      stopWatch.stop();
      if (!requestURI.startsWith("/api/v1/health/")) {
        log.info(REQUEST_COMPLETE_EXECUTE_TIME, requestURI, stopWatch);
      }
    }
  }

  private boolean isPublicPath(HttpServletRequest request) {
    if (request.getMethod().equals(HttpMethod.GET.toString())) {
      return publicGetPaths.stream().anyMatch(matcher -> matcher.matches(request));
    } else if (request.getMethod().equals(HttpMethod.POST.toString())) {
      return publicPostPaths.stream().anyMatch(matcher -> matcher.matches(request));
    } else {
      return false;
    }
  }
}
