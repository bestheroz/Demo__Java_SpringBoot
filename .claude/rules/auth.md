---
paths:
  - "src/main/java/com/github/bestheroz/standard/common/authenticate/**"
  - "src/main/java/com/github/bestheroz/standard/common/security/**"
  - "src/main/java/com/github/bestheroz/standard/config/SecurityConfig.java"
---

# 인증·인가 규칙

## 토큰
- Access / Refresh 만료값은 `application.yml` 의 `access-token-expiration-minutes`, `refresh-token-expiration-minutes` 로만 조정한다. 코드에 분 단위를 하드코딩하지 않는다
- `JwtTokenProvider` 의 만료 검사는 현재 시각에 3초를 더해 비교한다. 시계 오차로 만료 직전 토큰이 통과하는 것을 막기 위한 여유값이므로, 만료 로직을 고칠 때 이 여유를 없애지 않는다

## 인가
- 메서드 단위 권한은 `@PreAuthorize` 로 건다. Service 안에서 `Operator` 의 권한을 직접 if 로 분기하지 않는다
- 현재 사용자는 `@CurrentUser` 파라미터로 주입받는다. `SecurityContextHolder` 를 컨트롤러·서비스에서 직접 읽지 않는다

## 패스워드
- 해시·검증은 `PasswordUtil`(BCrypt 직접 호출) 만 사용한다. `SecurityConfig` 의 `PasswordEncoder` 빈을 주입해 따로 해시하지 않는다 — 해시 경로가 두 갈래가 된다
- `isPasswordValid` 는 평문을 앞 72자로 잘라 비교하지만 `getPasswordHash` 는 자르지 않는다. 길이 제한을 손볼 때 두 메서드를 함께 맞춘다
- 평문 패스워드를 로그나 예외 메시지에 넣지 않는다
