# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**waba-backend-univ** - 대학 축제 관리 백엔드 서비스 (Spring Boot 3.5.3, Java 17)

축제 정보, 스탬프 투어, 주차 관리, 위젯 기반 콘텐츠 기능을 제공하는 REST API 서버.

## Build Commands

```bash
./gradlew clean build           # 빌드
./gradlew test                  # 전체 테스트
./gradlew test --tests "*.StampServiceTest"              # 특정 클래스 테스트
./gradlew test --tests "*.StampServiceTest.testMethod"   # 특정 메서드 테스트
./gradlew spotlessApply         # 코드 포맷팅 (Palantir Java Format)
./gradlew jacocoTestReport      # 커버리지 리포트 (build/reports/jacoco/test/html/)
```

## Architecture

```
src/main/java/com/halo/eventer/
├── domain/          # 비즈니스 도메인
│   ├── stamp/       # 스탬프 투어 (핵심 기능)
│   ├── festival/    # 축제 정보 (중심 엔티티)
│   ├── member/      # 회원 인증 (SUPER_ADMIN, AGENCY, VISITOR)
│   ├── map/         # 지도/위치
│   ├── widget/      # UI 위젯 (다형성 상속)
│   ├── parking/     # 주차 관리
│   ├── program_reservation/  # 프로그램 예약 (멱등성, DB 락)
│   ├── fieldops/    # 현장 운영 (별도 인증 체계)
│   ├── alimtalk/    # 카카오 알림톡 발송
│   └── ...
├── global/          # 공통 모듈
│   ├── config/      # Spring 설정 (security/ 하위에 Security 관련)
│   ├── constants/   # SecurityConstants 등 상수
│   ├── security/    # JWT Provider, Filter, UserDetails
│   ├── error/       # BaseException, ErrorCode, GlobalExceptionHandler
│   ├── common/      # BaseTime, PageInfo, PagedResponse
│   └── utils/       # EncryptService (AES 암호화)
└── infra/           # 외부 연동 (Naver SMS)
```

각 도메인은 `Controller → Service → Repository → Entity` 패턴을 따름.

## Festival 중심 엔티티 구조

Festival이 대부분의 도메인 엔티티의 루트 엔티티:

```
Festival (1)
├── owner: Member (N:1)
├── stamps: Stamp[] (1:N)
├── baseWidgets: BaseWidget[] (1:N)
├── mapCategories: MapCategory[] (1:N)
├── notices: Notice[] (1:N)
├── parkingManagements: ParkingManagement[] (1:N)
├── durations: Duration[] (1:N)
├── inquiries, lostItems, managers, splashes, missingPersons ...
```

## Security Architecture

JWT 기반 인증. `JwtProvider.getAuthentication()`에서 3가지 사용자 타입 분기:

| Role | JWT Subject | 조회 방식 |
|------|-------------|-----------|
| `ROLE_VISITOR` | memberId (Long) | `loadVisitorById()` |
| `STAMP` | uuid (String) | `loadUserByUuid()` - 레거시 StampUser |
| 기타 (ADMIN 등) | loginId (String) | `loadUserByUsername()` |

공개 API 경로: `global/constants/SecurityConstants.java`에 정의

FieldOps(현장 운영)는 별도 인증 필터(`FieldOpsAuthenticationFilter`) 사용.

## Member-StampUser 통합

기존 축제별 `StampUser` → 통합 `Member` 시스템으로 전환 완료:

- `Member`에 `MemberRole.VISITOR` 추가 (SUPER_ADMIN, AGENCY, VISITOR)
- `StampUser`에 `Member` 연관관계 추가 (하위 호환 유지, nullable)
- VISITOR 인증 API: `/api/v1/auth/social-login`, `/api/v1/auth/signup`
- 소셜 로그인: `SocialOAuthClient` 인터페이스 → `KakaoOAuthClient`, `NaverOAuthClient` 구현

## Widget System (다형성)

`@Inheritance(SINGLE_TABLE)` + `@DiscriminatorColumn("widget_type")` 사용:

```
BaseWidget (abstract)
├── UpWidget (UP): PeriodFeature 임베디드
├── MainWidget (MAIN): ImageFeature + DescriptionFeature 임베디드
├── MiddleWidget (MIDDLE)
├── DownWidget (DOWN): 최대 3개 제한
└── SquareWidget (SQUARE)
```

위젯 타입별 별도 Service 존재 (UpWidgetService, MainWidgetService 등).
Feature는 `@Embeddable` 값 객체: ImageFeature, DescriptionFeature, PeriodFeature.

## Program Reservation 패턴

- **멱등성**: IDEMPOTENCY_KEY_REUSED 에러코드로 중복 예약 방지
- **DB 락**: `MySqlDbLockRepository`로 슬롯 용량 동시성 제어
- **자동 만료**: `ReservationExpireScheduler`로 TTL 기반 정리
- **엑셀 내보내기**: Apache POI 사용

## Error Handling

`BaseException` 상속 → `ErrorCode` enum 사용. 접두사별 도메인 구분:
- `C`: 공통, `A`: 인증, `F`: 축제, `V`: 방문자, `ST`: 스탬프
- `PR`: 프로그램 예약, `SO`: 소셜 로그인, `FO`: 현장 운영, `S`: SMS, `I`: 인프라

구체 예외: `AuthException`, `EntityNotFoundException`, `ForbiddenException`, `InvalidInputException`, `ConflictException`, `InfraException`

## DTO 컨벤션

- `*Request` / `*ReqDto`: 요청, `*Response` / `*ResDto`: 응답
- 중첩 DTO 허용 (예: Mission DTO 안에 MissionDetailsTemplateResDto)

## PII 암호화

전화번호, 이름 등 개인정보는 AES 암호화 후 저장 (`global/utils/EncryptService`).

## Testing

- JUnit 5 + Mockito, AssertJ
- Fixture 패턴: `src/test/.../fixture/` 디렉토리에 한글 정적 팩토리 메서드 사용
  - 예: `스탬프1_생성(Festival festival)`, `축제_엔티티()`
- `@SuppressWarnings("NonAsciiCharacters")` 로 한글 메서드명 허용
- `@DataJpaTest` + H2 인메모리 DB

## Key Notes

- **Timezone**: `Asia/Seoul` (EventerApplication.java @PostConstruct)
- **Code Style**: 커밋 전 `./gradlew spotlessApply` 필수
- **API Docs**: `/swagger-ui.html`
- **Import Order**: java/jakarta → org → com.halo.eventer → 나머지 → static
- **ID 생성**: ULID 사용 (`ulid-creator` 라이브러리)
- **외부 연동**: Naver SMS (`infra/sms/`), 카카오 알림톡 (`domain/alimtalk/`), AWS S3 (이미지 업로드)
- **모니터링**: Actuator + Prometheus (`/actuator/prometheus`)
