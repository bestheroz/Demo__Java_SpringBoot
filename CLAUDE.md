# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Spring Boot 기반의 데모 애플리케이션으로 Admin, User, Notice 도메인을 관리하는 REST API 서비스입니다. JWT 인증 및 Spring Security를 사용한 권한 관리를 포함합니다.

## Build Commands
- **빌드**: `./gradlew build`
- **실행**: `./gradlew bootRun`
- **JAR 생성**: `./gradlew bootJar` (결과물: build/libs/demo.jar)
- **코드 포맷팅**: `./gradlew spotlessApply`
- **의존성 버전 체크**: `./gradlew dependencyUpdates`
- **프로파일별 실행**: `./gradlew bootRun --args='--spring.profiles.active=local'`
- **Docker 빌드**: `docker build -t demo:latest .`
- **Docker 실행**: `docker run -p 8000:8000 demo:latest`

## Technology Stack
- Java 25
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Security
- MySQL
- JWT Authentication
- Swagger/OpenAPI 3
- Lombok
- Spotless (코드 포맷터)

## Architecture

### Package Structure
```
com.github.bestheroz
├── demo/                    # 비즈니스 로직 레이어
│   ├── controllers/         # REST 컨트롤러
│   ├── domain/             # JPA 엔티티
│   ├── dtos/               # 데이터 전송 객체 (Request/Response)
│   ├── repository/         # JPA 레포지토리
│   ├── services/           # 비즈니스 서비스
│   └── specification/      # JPA Specification (동적 쿼리)
└── standard/               # 공통 프레임워크 레이어
    ├── common/             # 공통 유틸리티
    │   ├── authenticate/   # JWT 인증 필터 및 토큰 처리
    │   ├── domain/         # 공통 엔티티 (IdCreated, IdCreatedUpdated)
    │   ├── dto/           # 공통 DTO (ListResult, TokenDto 등)
    │   ├── enums/         # 열거형 (AuthorityEnum, UserTypeEnum)
    │   ├── exception/     # 예외 처리
    │   └── security/      # 보안 관련 (Operator)
    └── config/            # 설정 클래스들
```

### Domain Model
- **Admin**: 관리자 도메인 - CRUD, 로그인/로그아웃, JWT 토큰 관리
- **User**: 사용자 도메인 - CRUD, 로그인/로그아웃, JWT 토큰 관리  
- **Notice**: 공지사항 도메인 - CRUD

### Security Architecture
- **JWT 기반 인증** (Access Token + Refresh Token)
  - Access Token: 기본 5분 (local: 1440분)
  - Refresh Token: 30분
  - Token 갱신: 3초 이내 발급된 Refresh Token은 재사용
- **Spring Security FilterChain**: `JwtAuthenticationFilter`로 요청 검증
  - Public Path: `SecurityConfig`의 `GET_PUBLIC`, `POST_PUBLIC`, `DELETE_PUBLIC` 배열로 관리
  - 루트 경로(/) 접근 시 404 반환
- **권한 제어**
  - `@PreAuthorize` 어노테이션으로 메서드 레벨 권한 제어
  - `@CurrentUser` 커스텀 어노테이션으로 현재 사용자 정보 주입
- **패스워드 관리**: `PasswordUtil`로 암호화 및 검증

### Database
- MySQL 사용
- Flyway 마이그레이션 파일: `migration/` 디렉토리
- P6Spy로 SQL 로깅 (local 프로파일에서 활성화)

## Configuration Profiles
- **local**: 개발용 (포트 8000, 외부 MySQL DB 연결)
- **sandbox**: 샌드박스 환경  
- **qa**: QA 환경
- **prod**: 운영 환경 (Swagger 비활성화)

## API Documentation
- Swagger UI: http://localhost:8000/swagger-ui.html
- OpenAPI 3 스펙 사용
- 모든 컨트롤러에 `@Tag`, `@Operation` 어노테이션 적용

## Development Notes

### 트랜잭션 관리 패턴
- **Service 클래스**: `@Transactional(readOnly = true)` (클래스 레벨)
- **수정 메서드**: `@Transactional` (메서드 레벨로 readOnly 오버라이드)
- **트랜잭션 경계**: Controller → Service (with @Transactional) → Repository

### 코딩 규칙
- **코드 스타일**: Google Java Format 사용 (Spotless 플러그인)
- **DTO 패턴**: Request/Response 내부 클래스로 그룹화 (예: `AdminDto.Request`, `AdminDto.Response`)
- **엔티티 설계**:
  - 공통 필드: `IdCreated` (id, createdBy, createdAt), `IdCreatedUpdated` (+ updatedBy, updatedAt, removedFlag)
  - Soft Delete: `removedFlag`로 논리 삭제
- **동적 쿼리**: JPA Specification 사용 (Specification 패키지)

### 기타
- **테스트**: 현재 테스트 코드가 없음 (src/test 디렉토리 없음)
- **로깅**: Logback + Sentry 연동, 헬스체크 엔드포인트는 로그 제외
- **CORS**: React 개발서버 (localhost:3000) 허용
- **세션**: Stateless (JWT 토큰 기반)
- **Virtual Threads**: Spring Boot 3.x의 Virtual Thread 활성화 (`spring.threads.virtual.enabled=true`)

## Important Commands for Claude Code
- **코드 포맷 적용 후 빌드**: `./gradlew spotlessApply && ./gradlew build`
- **변경사항 확인**: `./gradlew spotlessCheck` (포맷 위반 체크)
- **애플리케이션 상태 확인**: `curl http://localhost:8000/actuator/health`
- **Flyway 마이그레이션**: 애플리케이션 시작 시 자동 실행 (migration/ 디렉토리)

## 주요 엔드포인트
- **Health Check**: `GET /api/v1/health/**` (인증 불필요, 로그 제외)
- **Swagger UI**: `GET /swagger-ui.html` (prod 제외)
- **Admin 로그인**: `POST /api/v1/admins/login`
- **User 로그인**: `POST /api/v1/users/login`
- **Token 갱신**: `POST /api/v1/admins/renew-token`, `POST /api/v1/users/renew-token`