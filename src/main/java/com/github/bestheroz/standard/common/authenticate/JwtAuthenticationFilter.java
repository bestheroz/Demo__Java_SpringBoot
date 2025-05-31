package com.github.bestheroz.standard.common.authenticate;

import static com.github.bestheroz.standard.config.SecurityConfig.*;

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
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  // SecurityConfig에서 정의된 문자열 배열(GET_PUBLIC, POST_PUBLIC, DELETE_PUBLIC)을 그대로 사용
  private final List<String> publicGetPaths = Arrays.asList(GET_PUBLIC);
  private final List<String> publicPostPaths = Arrays.asList(POST_PUBLIC);
  private final List<String> publicDeletePaths = Arrays.asList(DELETE_PUBLIC);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = new UrlPathHelper().getPathWithinApplication(request);

    // 루트 경로에 대해 404 처리
    if (requestURI.equals("/")) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // 헬스체크 엔드포인트가 아닌 경우만 로그 출력
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
      // 퍼블릭 경로라면 토큰 검증 없이 다음 필터로 이동
      if (isPublicPath(requestURI, request.getMethod())) {
        filterChain.doFilter(request, response);
        return;
      }

      // 토큰 조회
      String token = jwtTokenProvider.resolveAccessToken(request);
      if (token == null) {
        log.info("No access token found");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      // 토큰 유효성 검증
      if (!jwtTokenProvider.validateToken(token)) {
        log.info("Invalid access token - refresh token required");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      // 인증 정보 설정
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

  private boolean isPublicPath(String requestURI, String httpMethod) {
    if (HttpMethod.GET.matches(httpMethod)) {
      return publicGetPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    } else if (HttpMethod.POST.matches(httpMethod)) {
      return publicPostPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    } else if (HttpMethod.DELETE.matches(httpMethod)) {
      return publicDeletePaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
    return false;
  }
}
