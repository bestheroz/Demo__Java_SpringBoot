# Demo__Java_SpringBoot

> 최신 트렌드를 학습/추적/테스트하기 위한 개인 프로젝트입니다.
>
> 또한 이 프레임워크 코드를 통해 개발을 즉시 시작할 수 있도록 작성하였습니다.

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/gradle-%2302303A.svg?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-%232496ED?style=for-the-badge&logo=docker&logoColor=white)

# Domain
- Admin - 관리자
- User - 사용자
- Notice - 공지

# Feature
- [x] Admin
    - [x] Admin CRUD
    - [x] Admin Login / Logout
    - [x] Admin JWT
- [x] User
    - [x] User CRUD
    - [x] User Login / Logout
    - [x] User JWT
- [x] Notice
    - [x] Notice CRUD

# Dependency Policy
- 신규 버전 선체험과 변화점 발견이 목적이라 사전 릴리스(M·RC·Beta·Alpha 등)를 허용하고 우선합니다.
- 의존성·플러그인 좌표는 `gradle/libs.versions.toml` 에 있습니다. 버전 없는 항목은 Spring Boot BOM 을 따르고, 버전을 적은 항목은 BOM 보다 앞서 최신을 씁니다.
- `./gradlew versionCatalogUpdate` 가 버전 있는 항목을 사전 릴리스 포함 최신으로 올립니다.
- 올린 버전이 깨지면 먼저 코드를 고칩니다. 고칠 수 없는 항목만 동작하는 최신 버전으로 내리고 `# @pin` 을 붙입니다.
