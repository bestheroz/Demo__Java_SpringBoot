package com.github.bestheroz.standard.common.authenticate;

import com.github.bestheroz.standard.common.security.CommonUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String REQUEST_COMPLETE_EXECUTE_TIME =
      "{} ....... Request Complete Execute Time ....... : {}";
  private static final String REQUEST_PARAMETERS = "<{}>{}: {}";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final List<AntPathRequestMatcher> publicPaths;

  public JwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider,
      UserDetailsService userDetailsService,
      String[] publicPaths) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
    this.publicPaths = Arrays.stream(publicPaths).map(AntPathRequestMatcher::new).toList();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = new UrlPathHelper().getPathWithinApplication(request);

    if (requestURI.equals("/")) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    if (!requestURI.startsWith("/api/v1/health/")) {
      log.info(REQUEST_PARAMETERS, request.getMethod(), requestURI, request.getQueryString());
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
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access token found");
        return;
      }

      if (!jwtTokenProvider.validateAccessToken(token)) {
        log.info("Invalid access token - refresh token required");
        response.setHeader("refreshToken", "required");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
        return;
      }

      CommonUserDetails userDetails = jwtTokenProvider.getCustomUserDetails(token);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } finally {
      stopWatch.stop();
      if (!requestURI.startsWith("/api/v1/health/")) {
        log.info(REQUEST_COMPLETE_EXECUTE_TIME, requestURI, stopWatch);
      }
    }
  }

  private boolean isPublicPath(HttpServletRequest request) {
    return publicPaths.stream().anyMatch(matcher -> matcher.matches(request));
  }
}