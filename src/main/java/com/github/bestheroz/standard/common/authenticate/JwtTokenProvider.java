package com.github.bestheroz.standard.common.authenticate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import com.github.bestheroz.standard.common.security.Operator;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class JwtTokenProvider {

  private final Algorithm algorithm;
  private final long accessTokenExpirationMinutes;
  private final long refreshTokenExpirationMinutes;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes,
      @Value("${jwt.refresh-token-expiration-minutes}") long refreshTokenExpirationMinutes) {
    this.algorithm = Algorithm.HMAC512(secret);
    this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
  }

  public String createAccessToken(Operator customOperator) {
    Assert.notNull(customOperator, "customUserDetails must not be null");
    return JWT.create()
        .withClaim("id", customOperator.getId())
        .withClaim("loginId", customOperator.getLoginId())
        .withClaim("name", customOperator.getName())
        .withClaim("type", customOperator.getType().name())
        .withClaim("managerFlag", customOperator.getManagerFlag())
        .withArrayClaim(
            "authorities",
            customOperator.getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .toArray(String[]::new))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpirationMinutes * 60)))
        .sign(algorithm);
  }

  public String createRefreshToken(Operator customOperator) {
    Assert.notNull(customOperator, "customUserDetails must not be null");
    return JWT.create()
        .withClaim("id", customOperator.getId())
        .withExpiresAt(Date.from(Instant.now().plusSeconds(refreshTokenExpirationMinutes * 60)))
        .sign(algorithm);
  }

  public Authentication getAuthentication(String token) {
    return new UsernamePasswordAuthenticationToken(
        getOperator(token), "", getOperator(token).getAuthorities());
  }

  public Long getId(String token) {
    return verifyToken(token).getClaim("id").asLong();
  }

  public UserDetails getOperator(String token) {
    DecodedJWT jwt = verifyToken(token);
    return new Operator(
        jwt.getClaim("id").asLong(),
        jwt.getClaim("loginId").asString(),
        jwt.getClaim("name").asString(),
        UserTypeEnum.valueOf(jwt.getClaim("type").asString()),
        jwt.getClaim("managerFlag").asBoolean(),
        jwt.getClaim("authorities").asList(String.class).stream()
            .map(AuthorityEnum::valueOf)
            .toList());
  }

  public String resolveAccessToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      verifyToken(token);
      return true;
    } catch (JWTVerificationException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  public boolean issuedRefreshTokenIn3Seconds(String refreshToken) {
    try {
      DecodedJWT jwt = JWT.require(algorithm).build().verify(refreshToken);
      Instant expiresAt = jwt.getExpiresAt().toInstant();
      return Instant.now().plusSeconds(3).isBefore(expiresAt);
    } catch (JWTVerificationException e) {
      log.warn("Invalid refresh token: {}", e.getMessage());
      return false;
    }
  }

  private DecodedJWT verifyToken(String token) {
    return JWT.require(algorithm).build().verify(token.replace("Bearer ", ""));
  }
}
