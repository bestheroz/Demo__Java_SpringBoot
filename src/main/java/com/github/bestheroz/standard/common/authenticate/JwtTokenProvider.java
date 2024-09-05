package com.github.bestheroz.standard.common.authenticate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import com.github.bestheroz.standard.common.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

  public String createAccessToken(CustomUserDetails customCustomUserDetails) {
    Assert.notNull(customCustomUserDetails, "customUserDetails must not be null");
    return JWT.create()
        .withClaim("id", customCustomUserDetails.getId())
        .withClaim("loginId", customCustomUserDetails.getLoginId())
        .withClaim("name", customCustomUserDetails.getName())
        .withClaim("type", customCustomUserDetails.getType().name())
        .withClaim("managerFlag", customCustomUserDetails.getManagerFlag())
        .withArrayClaim(
            "authorities",
            customCustomUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .toArray(String[]::new))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpirationMinutes * 60)))
        .sign(algorithm);
  }

  public String createRefreshToken(CustomUserDetails customCustomUserDetails) {
    Assert.notNull(customCustomUserDetails, "customUserDetails must not be null");
    return JWT.create()
        .withClaim("id", customCustomUserDetails.getId())
        .withExpiresAt(Date.from(Instant.now().plusSeconds(refreshTokenExpirationMinutes * 60)))
        .sign(algorithm);
  }

  public Authentication getAuthentication(String token) {
    CustomUserDetails customUserDetails = getCustomUserDetails(token);
    return new UsernamePasswordAuthenticationToken(
        customUserDetails, "", customUserDetails.getAuthorities());
  }

  public Long getId(String token) {
    return getClaim(token, "id").asLong();
  }

  public CustomUserDetails getCustomUserDetails(String token) {
    DecodedJWT jwt = verifyToken(token);
    return new CustomUserDetails(
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

  public boolean validateAccessToken(String token) {
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
    return JWT.require(algorithm).build().verify(token);
  }

  private Claim getClaim(String token, String claimName) {
    return verifyToken(token).getClaim(claimName);
  }
}
