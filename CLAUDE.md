# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Spring Boot 기반의 데모 애플리케이션으로 Admin, User, Notice 도메인을 관리하는 REST API 서비스입니다. JWT 인증 및 Spring Security를 사용한 권한 관리를 포함합니다.

## Build Commands
```bash
# 빌드 (포맷팅 포함)
./gradlew spotlessApply && ./gradlew build

# 실행 (local 프로파일 기본)
./gradlew bootRun

# 프로파일 지정 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# 코드 포맷 검증
./gradlew spotlessCheck

# 의존성 버전 체크
./gradlew dependencyUpdates

# JAR 생성 (결과물: build/libs/demo.jar)
./gradlew bootJar

# Docker
docker build -t demo:latest .
docker run -p 8000:8000 demo:latest
```

## Technology Stack
- Java 25 (Spring Boot 4.0.1)
- Spring Data JPA + MySQL
- Spring Security + JWT (com.auth0:java-jwt)
- Swagger/OpenAPI 3 (springdoc-openapi)
- Spotless (Google Java Format)
- P6Spy (SQL 로깅)
- Sentry (에러 모니터링)

## Architecture

### 레이어 구조
```
com.github.bestheroz
├── demo/                    # 비즈니스 도메인
│   ├── controllers/         # REST API 엔드포인트
│   ├── domain/              # JPA 엔티티 (Admin, User, Notice)
│   ├── dtos/                # DTO 클래스 (도메인별 하위 패키지)
│   ├── repository/          # JPA Repository
│   ├── services/            # 비즈니스 로직
│   └── specification/       # JPA Specification (동적 쿼리)
└── standard/                # 공통 프레임워크
    ├── common/
    │   ├── authenticate/    # JWT 필터, 토큰 처리
    │   ├── domain/          # 공통 엔티티 (IdCreated, IdCreatedUpdated)
    │   ├── dto/             # 공통 DTO (ListResult, TokenDto)
    │   ├── enums/           # AuthorityEnum, UserTypeEnum
    │   ├── exception/       # 예외 처리 (400, 401, 403, 500)
    │   └── security/        # Operator (현재 사용자 정보)
    └── config/              # 설정 클래스
```

### Security Architecture
- **JWT 기반 인증**: Access Token (5분, local: 1440분) + Refresh Token (30분)
- **Public Path**: `SecurityConfig`의 `GET_PUBLIC`, `POST_PUBLIC`, `DELETE_PUBLIC` 배열로 관리
- **권한 제어**: `@PreAuthorize` 메서드 레벨 권한, `@CurrentUser` 현재 사용자 주입
- **패스워드**: `PasswordUtil` + BCrypt

### Database
- MySQL (HikariCP 커넥션 풀)
- 수동 SQL 스크립트: `migration/` 디렉토리 (V1~V3)
- P6Spy SQL 로깅 (local 프로파일)

## Development Notes

### 트랜잭션 패턴 (필수 준수)
```java
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 읽기 전용
public class XxxService {

    public Entity getEntity() { ... }  // readOnly = true 상속

    @Transactional  // 수정 메서드만 오버라이드
    public Entity createEntity() { ... }
}
```

**트랜잭션 경계 원칙**:
- ✅ Controller → Service (with @Transactional) → Repository
- ✅ Service → Helper Service (without @Transactional)
- ❌ Service (@Transactional) → Service (@Transactional) 중첩 금지

### DTO 패턴
- **조회용**: `XxxDto.Request` (검색 조건), `XxxDto.Response` (응답)
- **생성/수정**: `XxxCreateDto.Request`, `XxxUpdateDto.Request` (별도 클래스)
- **Entity → DTO**: 정적 팩토리 메서드 `Response.of(entity)` 사용

### 엔티티 설계
- **IdCreated**: id, createdBy, createdAt
- **IdCreatedUpdated**: + updatedBy, updatedAt, removedFlag (Soft Delete)
- **update() 메서드**: 엔티티 내부에서 필드 변경 (setter 대신)

### 동적 쿼리
- JPA Specification 패턴 사용
- `XxxSpecification` 클래스에 정적 메서드로 조건 정의
- Stream으로 조건 조합 후 `reduce(Specification::and)`

## Configuration Profiles
| Profile | Port | Swagger | DB Pool | 용도 |
|---------|------|---------|---------|------|
| local | 8000 | ✅ | 3 | 개발 |
| sandbox | 8000 | ✅ | 10 | 샌드박스 |
| qa | 8000 | ✅ | 10 | QA |
| prod | 8000 | ❌ | 30 | 운영 |

## 주요 API 엔드포인트
- **Health**: `GET /api/v1/health/**` (인증 불필요)
- **Swagger**: `GET /swagger-ui.html` (prod 제외)
- **로그인**: `POST /api/v1/admins/login`, `POST /api/v1/users/login`
- **토큰 갱신**: `POST /api/v1/admins/renew-token`, `POST /api/v1/users/renew-token`

## 참고 사항
- **CORS**: localhost:3000 허용 (React 개발서버)
- **Virtual Threads**: 활성화됨 (`spring.threads.virtual.enabled=true`)
- **테스트**: 현재 테스트 코드 없음
- **Dockerfile**: amazoncorretto:21 사용 (Java 25와 불일치 - 주의)