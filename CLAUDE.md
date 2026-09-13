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

# 의존성·플러그인 버전은 gradle/libs.versions.toml(버전 카탈로그). 버전 없는 항목은 Spring Boot BOM 관리
./gradlew dependencyUpdates                    # ben-manes 리포트, 사전 릴리스 포함(Demo 정책)
./gradlew versionCatalogUpdate --interactive   # 올릴 후보를 gradle/libs.versions.updates.toml 에 쓴다
./gradlew versionCatalogApplyUpdates           # 위 파일에 남긴 항목만 카탈로그에 반영

# JAR 생성 (결과물: build/libs/demo.jar)
./gradlew bootJar

# Docker
docker build -t demo:latest .
docker run -p 8000:8000 demo:latest
```

## Technology Stack
- Java 25 (Spring Boot 4.2.0-M1)
- Gradle 9.8.0-rc-1
- Spring Data JPA + MySQL (mysql-connector-j 26.7.0, BOM 보다 앞선 명시 버전)
- Spring Security + JWT (com.auth0:java-jwt 4.6.1)
- Swagger/OpenAPI 3 (springdoc-openapi 3.1.1)
- Spotless 8.10.2 (Google Java Format)
- P6Spy (p6spy-spring-boot-starter 2.0.1, SQL 로깅)
- Sentry 8.56.0 (에러 모니터링)

## 의존성 관리
- **Demo 정책**: 신규 버전 선체험과 변화점 발견이 목적이라 사전 릴리스(M·RC·Beta·Alpha 등)를 허용하고 우선한다. `versionCatalogUpdate` 선택기는 `LATEST`, `dependencyUpdates` 는 `rejectPreReleases = false` 다.
- 좌표는 전부 `gradle/libs.versions.toml` 에 있고 `build.gradle` 은 `libs.xxx` / `alias(libs.plugins.xxx)` 로만 참조한다.
- 버전 없이 넣은 항목(spring-boot-starter-*, spring-boot-configuration-processor, lombok, aspectjweaver)은 `{ module = "g:a" }` 로 두어 Spring Boot BOM 을 따른다. Boot 플러그인을 사전 릴리스 포함 최신으로 올리면 함께 따라간다.
- BOM 이 관리하지만 BOM 보다 앞서 체험하려고 버전을 적은 항목(mysql-connector-j)은 명시 버전을 유지한 채 최신으로 올린다. 직접 적은 버전은 BOM 을 이긴다. BOM 관리 좌표에 버전을 새로 적는 것은 그 의도가 있을 때만 한다.
- `versionCatalogUpdate` 는 버전 없는 항목을 건너뛰고 버전 있는 항목만 사전 릴리스 포함 최신으로 올린다.
- 올린 버전이 깨지면 먼저 코드를 고친다. 고칠 수 없는 좌표만 동작하는 최신 버전으로 내리고 카탈로그 항목 바로 위 줄에 `# @pin` 을 달며, 사유는 `build.gradle` 주석에 적는다.
- `versionCatalogUpdate` 가 카탈로그를 다시 쓸 때 항목 옆 주석을 지운다. 남겨야 할 설명은 `build.gradle` 에 두고, 카탈로그에는 `@pin` / `@keep` 애노테이션만 쓴다.
- `gradle.properties` 가 설정 캐시(`org.gradle.configuration-cache`)·빌드 캐시(`org.gradle.caching`)·병렬 실행(`org.gradle.parallel`)을 켠다. 그래서 CI 워크플로의 gradlew 명령에는 이 플래그를 따로 주지 않는다. `versionCatalogUpdate` 는 설정 캐시와 호환되지 않아 실행할 때마다 캐시 항목이 버려진다(빌드는 성공한다).

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
- **Dockerfile**: eclipse-temurin:25 멀티스테이지 빌드 (JDK builder + JRE runtime)