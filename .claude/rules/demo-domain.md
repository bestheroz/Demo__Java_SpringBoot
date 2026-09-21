---
paths:
  - "src/main/java/com/github/bestheroz/demo/**"
  - "src/test/java/com/github/bestheroz/demo/**"
---

# demo 도메인 작성 규칙

Admin·User·Notice 는 레이어별 폴더에 흩어져 있다. 새 도메인을 추가할 때도 같은 배치를 따른다.

## Service
루트 CLAUDE.md 의 트랜잭션 경계 규칙을 이 프로젝트에서 적용한 형태:

```java
@Service
@Transactional(readOnly = true)
public class XxxService {

  public Entity getEntity() { ... }        // readOnly = true 상속

  @Transactional                            // 수정 메서드만 오버라이드
  public Entity createEntity() { ... }
}
```

## DTO
- 조회: `XxxDto.Request`(검색 조건), `XxxDto.Response`(응답)
- 생성/수정: `XxxCreateDto.Request`, `XxxUpdateDto.Request` 를 별도 클래스로 만든다. 조회 DTO 에 필드를 덧붙이지 않는다
- Entity → DTO 변환은 정적 팩토리 `Response.of(entity)` 로만 한다. 생성자 직접 호출 금지
- DTO 는 `dtos/<도메인>/` 하위에 둔다

## Entity
- 생성 정보만 필요하면 `IdCreated`, 수정·소프트 삭제까지 필요하면 `IdCreatedUpdated` 를 상속한다
- 필드 변경은 엔티티 내부 `update()` 메서드로만 한다. setter 를 노출하지 않는다 — 변경 지점이 흩어지면 감사 필드가 누락된다
- `IdCreatedUpdated` 를 쓰면 삭제는 `removedFlag` 로 처리한다. 물리 삭제 쿼리를 쓰지 않는다

## 동적 쿼리
- 검색 조건은 JPA Specification 으로 만든다. Repository 에 조건 조합 메서드를 늘리지 않는다
- 조건은 `XxxSpecification` 의 정적 메서드로 정의하고, Stream 으로 모아 `reduce(Specification::and)` 로 합친다
