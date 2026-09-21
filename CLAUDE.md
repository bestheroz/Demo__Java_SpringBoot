# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
```bash
# 빌드 (포맷팅 포함) — 커밋 전 반드시 통과시킨다
./gradlew spotlessApply && ./gradlew build

# 코드 포맷 검증만
./gradlew spotlessCheck

# 실행 (local 프로파일 기본)
./gradlew bootRun
./gradlew bootRun --args='--spring.profiles.active=local'

# JAR 생성 (결과물: build/libs/demo.jar)
./gradlew bootJar

# 의존성 버전 올리기 (아래 "의존성 관리" 정책을 따른다)
./gradlew dependencyUpdates                    # 갱신 후보 리포트
./gradlew versionCatalogUpdate --interactive   # 후보를 gradle/libs.versions.updates.toml 에 쓴다
./gradlew versionCatalogApplyUpdates           # 위 파일에 남긴 항목만 카탈로그에 반영
```

## 의존성 관리
- **Demo 정책**: 신규 버전 선체험과 변화점 발견이 목적이라 사전 릴리스(M·RC·Beta·Alpha 등)를 허용하고 우선한다. `versionCatalogUpdate` 선택기는 `LATEST`, `dependencyUpdates` 는 `rejectPreReleases = false` 다.
- 좌표는 전부 `gradle/libs.versions.toml` 에 있고 `build.gradle` 은 `libs.xxx` / `alias(libs.plugins.xxx)` 로만 참조한다. `build.gradle` 에 버전 문자열을 직접 쓰지 않는다.
- 버전을 적지 않은 항목은 `{ module = "g:a" }` 로 두어 Spring Boot BOM 을 따른다. Boot 플러그인을 올리면 함께 따라간다.
- BOM 관리 좌표에 버전을 새로 적는 것은 **BOM 보다 앞선 버전을 선체험하려는 경우에만** 한다. 직접 적은 버전이 BOM 을 이기므로, 의도 없이 적으면 BOM 추종이 조용히 끊긴다.
- `versionCatalogUpdate` 는 버전 없는 항목을 건너뛰고 버전 있는 항목만 사전 릴리스 포함 최신으로 올린다.
- 올린 버전이 `./gradlew spotlessApply && ./gradlew build` 를 실패시키면 먼저 코드를 고친다. 고칠 수 없는 좌표만 빌드가 통과하는 최신 버전으로 내리고 카탈로그 항목 바로 위 줄에 `# @pin` 을 달며, 사유는 `build.gradle` 주석에 적는다.
- `versionCatalogUpdate` 가 카탈로그를 다시 쓸 때 항목 옆 주석을 지운다. 남겨야 할 설명은 `build.gradle` 에 두고, 카탈로그에는 `@pin` / `@keep` 애노테이션만 쓴다.
- `gradle.properties` 가 설정 캐시·빌드 캐시·병렬 실행을 켠다. 그래서 CI 워크플로의 gradlew 명령에는 이 플래그를 따로 주지 않는다. `versionCatalogUpdate` 는 설정 캐시와 호환되지 않아 실행할 때마다 캐시 항목이 버려진다(빌드는 성공한다).

## 전역 규칙
- **트랜잭션 경계**: Service 는 클래스 레벨 `@Transactional(readOnly = true)` 를 달고 수정 메서드만 `@Transactional` 로 오버라이드한다.
  - ✅ Controller → Service(`@Transactional`) → Repository
  - ✅ Service → Helper Service(`@Transactional` 없음)
  - ❌ Service(`@Transactional`) → Service(`@Transactional`) 중첩. 바깥 트랜잭션에 합류해 안쪽 롤백 의도가 사라진다.
- **의존 방향**: `demo` → `standard` 가 정방향이다. 역방향은 `standard` 가 감사 필드·`Operator` 때문에 `demo.domain.Admin`/`User` 를 참조하는 기존 지점(`common/domain`, `common/dto`, `common/security`, `common/health`)만 허용한다. 새 코드에서 `standard` → `demo` 의존을 늘리지 않는다.
- **인증 경로 추가**: 인증 없이 열어야 하는 엔드포인트는 컨트롤러가 아니라 `SecurityConfig` 의 `GET_PUBLIC` / `POST_PUBLIC` / `DELETE_PUBLIC` 배열에 경로를 추가해야 열린다. 배열에 없으면 `anyRequest().authenticated()` 에 걸린다.
- **CI 는 테스트를 돌리지 않는다**: `.github/workflows/test.yml` 이 `assemble -x test` 로 실행한다. 테스트를 추가하면 이 워크플로도 함께 고쳐야 실제로 검증된다.

## CLAUDE.md 관리 규칙
- 이 파일은 200줄 이하 유지. 매 세션 필요한 내용만 둔다: 빌드/테스트 명령, 전역 컨벤션, 도메인 간 의존 규칙, 함정과 그 이유
- 코드에서 유추 가능한 내용(디렉터리 구조, 의존성 목록, 아키텍처 개요)은 쓰지 않는다
- 지시는 검증 가능한 수준으로 구체적으로 쓴다 (X "포맷 잘 맞춰라" / O "2-space 들여쓰기")
- 특정 도메인/경로에만 해당하는 규칙은 이 파일에 넣지 않는다
  - 도메인이 단일 폴더로 분리돼 있으면 → 해당 폴더의 CLAUDE.md
  - 여러 폴더에 흩어져 있으면 → `.claude/rules/<topic>.md` + `paths` frontmatter
  - 다단계 절차는 → 스킬
- 하위 CLAUDE.md 와 rules 에는 루트 규칙을 재진술하지 않는다. 충돌/중복 발견 시 사용자에게 알린다
- 도메인 규칙을 분리하면 아래 "도메인 인덱스"에 한 줄 추가한다
- 지시 파일을 추가/수정할 때는 변경 전 사용자에게 위치와 내용을 먼저 제안한다

## 도메인 인덱스
<!-- 형식: `경로/` — 한 줄 설명, 규칙 파일 위치 -->
<!-- 이 프로젝트는 레이어 우선 배치라 도메인이 단일 폴더로 모이지 않는다.
     따라서 도메인 규칙은 하위 CLAUDE.md 가 아니라 .claude/rules/ + paths 로 분리한다. -->
- `demo/**/Admin*` — 관리자 계정·로그인·권한(AuthorityEnum) 도메인. 규칙 파일: `.claude/rules/demo-domain.md`
- `demo/**/User*` — 일반 사용자 계정·로그인 도메인. 규칙 파일: `.claude/rules/demo-domain.md`
- `demo/**/Notice*` — 공지사항 CRUD 도메인. 규칙 파일: `.claude/rules/demo-domain.md`
- `standard/common/authenticate/`, `standard/common/security/`, `standard/config/SecurityConfig.java` — JWT 발급·검증과 현재 사용자 주입. 규칙 파일: `.claude/rules/auth.md`
- `standard/common/` (domain, dto, exception, util) — 전 도메인이 공유하는 기반 타입. 규칙 파일: 없음
- `standard/config/` — OpenAPI·P6Spy 등 애플리케이션 설정. 규칙 파일: 없음
- `migration/` — 수동 적용 SQL 스크립트(V 접두사 순번). 규칙 파일: 없음
- `.claude/rules/` — 지시 파일 자체의 작성·수정 기준. 규칙 파일: `.claude/rules/claude-md-maintenance.md`
