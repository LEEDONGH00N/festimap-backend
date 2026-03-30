# waba-backend-univ

대학 축제 관리 백엔드 서비스 (Spring Boot 3.5.3, Java 17)

축제 정보, 스탬프 투어, 주차 관리, 위젯 기반 콘텐츠 기능을 제공하는 REST API 서버.

---

## 목차

- [회원 통합 (Member Integration)](#회원-통합-member-integration)
  - [배경 및 목적](#배경-및-목적)
  - [회원 역할 구조](#회원-역할-구조)
  - [Member 엔티티 설계](#member-엔티티-설계)
  - [소셜 로그인 (OAuth)](#소셜-로그인-oauth)
  - [인증 플로우](#인증-플로우)
  - [StampUser 통합](#stampuser-통합)
  - [JWT 인증 분기](#jwt-인증-분기)
  - [회원 API 명세](#회원-api-명세)
- [스탬프 투어 (Stamp Tour)](#스탬프-투어-stamp-tour)
  - [기능 개요](#기능-개요)
  - [엔티티 구조](#엔티티-구조)
  - [사용자 참여 플로우](#사용자-참여-플로우)
  - [미션 완료 로직](#미션-완료-로직)
  - [경품 시스템](#경품-시스템)
  - [인증 방식](#인증-방식)
  - [UI 커스터마이징](#ui-커스터마이징)
  - [스탬프 투어 API 명세 - 사용자](#스탬프-투어-api-명세---사용자)
  - [스탬프 투어 API 명세 - 관리자](#스탬프-투어-api-명세---관리자)

---

# 회원 통합 (Member Integration)

기존 축제별 `StampUser` 기반 인증 체계를 통합 `Member` 시스템으로 전환한 설계 문서입니다.

---

## 배경 및 목적

### 기존 문제

- `StampUser`는 축제(Stamp)마다 독립적으로 생성되어, 동일 사용자가 여러 축제에 참여할 때마다 새로운 계정을 만들어야 했음
- 전화번호 + 이름 기반 인증으로 소셜 로그인 등 현대적 인증 방식 적용 불가
- 사용자 데이터가 축제 단위로 파편화되어 통합 관리 불가능

### 해결 방향

- 통합 `Member` 엔티티를 도입하여 한 번 가입 후 모든 축제에서 사용
- 카카오/네이버 소셜 로그인 지원
- 기존 `StampUser` UUID 기반 인증은 하위 호환 유지

---

## 회원 역할 구조

### MemberRole

| 역할 | 설명 | 인증 방식 |
|------|------|-----------|
| `SUPER_ADMIN` | 시스템 관리자 | loginId + password |
| `AGENCY` | 축제 운영 업체 | loginId + password |
| `VISITOR` | 축제 방문자 | 소셜 로그인 (카카오/네이버) |

### MemberStatus

| 상태 | 설명 |
|------|------|
| `ACTIVE` | 정상 사용 |
| `DORMANT` | 휴면 상태 |
| `WITHDRAWN` | 탈퇴 |

---

## Member 엔티티 설계

### 핵심 필드

```
Member
├── id (Long, PK)
├── loginId (String)          # SUPER_ADMIN, AGENCY 전용
├── password (String)         # SUPER_ADMIN, AGENCY 전용
├── phone (String)            # 전화번호
├── name (String)             # 이름
├── role (MemberRole)         # SUPER_ADMIN | AGENCY | VISITOR
├── status (MemberStatus)     # ACTIVE | DORMANT | WITHDRAWN
│
├── [소셜 로그인]
│   ├── provider (SocialProvider)   # KAKAO | NAVER
│   └── providerId (String)         # 소셜 플랫폼 고유 ID
│
├── [동의 정보]
│   ├── termsAgreed (Boolean)       # 서비스 이용약관
│   ├── privacyAgreed (Boolean)     # 개인정보 처리방침
│   └── marketingAgreed (Boolean)   # 마케팅 수신 동의
│
├── [설문 정보 - VISITOR]
│   ├── residenceType (String)      # 거주 형태
│   ├── residenceRegion (String)    # 거주 지역
│   ├── residenceDistrict (String)  # 거주 구/군
│   ├── visitType (String)          # 방문 유형
│   ├── gender (Gender)             # MALE | FEMALE
│   └── birthDate (LocalDate)       # 생년월일
│
├── [업체 정보 - AGENCY]
│   ├── companyName (String)
│   ├── companyEmail (String)
│   ├── companyPhone (String)
│   └── managerPosition (String)
│
└── [연관관계]
    ├── authorities (List<Authority>)   # 권한 목록
    └── stampUsers (List<StampUser>)     # 참여한 스탬프 투어 목록
```

### 팩토리 메서드

```java
// VISITOR 생성 (소셜 로그인)
Member.createVisitor(phone, name, provider, providerId)

// AGENCY 생성 (아이디/비밀번호)
Member.createAgency(loginId, password, companyEmail, ...)
```

---

## 소셜 로그인 (OAuth)

### Strategy 패턴 구조

```
SocialOAuthClient (interface)
├── provider(): SocialProvider
└── getUserInfo(accessToken): SocialUserInfo
        │
        ├── KakaoOAuthClient
        │   └── API: https://kapi.kakao.com/v2/user/me
        │
        └── NaverOAuthClient
            └── API: https://openapi.naver.com/v1/nid/me

SocialOAuthService (dispatcher)
└── Map<SocialProvider, SocialOAuthClient> clients
    └── getUserInfo(provider, accessToken) → 해당 client에 위임
```

### SocialUserInfo

| 필드 | 타입 | 설명 |
|------|------|------|
| `provider` | SocialProvider | KAKAO / NAVER |
| `providerId` | String | 소셜 플랫폼에서 발급한 사용자 고유 ID |

### 에러 처리

| 에러 코드 | 상황 |
|-----------|------|
| `INVALID_SOCIAL_TOKEN` (SO001, 401) | 소셜 액세스 토큰 검증 실패 |
| `INVALID_SOCIAL_PROVIDER` (SO002, 400) | 지원하지 않는 소셜 로그인 제공자 |
| `SOCIAL_LOGIN_FAILED` (SO003, 500) | 소셜 로그인 처리 중 서버 오류 |

---

## 인증 플로우

### 소셜 로그인 플로우 (VISITOR)

```
[프론트엔드]
    │  카카오/네이버 SDK로 accessToken 획득
    ▼
POST /api/v1/auth/social-login
    { provider: "KAKAO", accessToken: "..." }
    │
    ▼
[SocialOAuthService]
    │  소셜 플랫폼 API로 사용자 정보 조회
    ▼
[MemberRepository]
    │  findByProviderAndProviderId()
    │
    ├── 기존 회원 (ACTIVE) ──→ JWT 토큰 발급
    │   { isMember: true, token: "eyJ..." }
    │
    ├── 기존 회원 (비활성) ──→ MEMBER_NOT_ACTIVE 에러
    │
    └── 미가입 ──→ 회원가입 필요 응답
        { isMember: false, provider: "KAKAO", providerId: "123456" }
```

### 회원가입 플로우 (VISITOR)

```
[프론트엔드]
    │  SMS 인증 완료 후
    ▼
POST /api/v1/auth/signup
    {
      provider, providerId,
      phone, name,
      termsAgreed, privacyAgreed, marketingAgreed,
      residenceType, gender, birthDate, ...
    }
    │
    ▼
[VisitorAuthService]
    ├── SMS 인증 여부 검증
    ├── 전화번호 중복 확인 (VISITOR 역할 내)
    ├── Member.createVisitor() + Authority("ROLE_VISITOR")
    ├── 설문 정보 저장
    └── JWT 토큰 발급
        { token: "eyJ..." }
```

### AGENCY/ADMIN 로그인 플로우

```
POST /api/v1/admin/auth/login
    { loginId, password }
    │
    ▼
[MemberService]
    ├── loginId로 Member 조회
    ├── 비밀번호 검증 (PasswordEncoder)
    └── JWT 토큰 발급 (subject = loginId)
```

---

## StampUser 통합

### 통합 전후 비교

| 항목 | 기존 (StampUser 단독) | 통합 후 (Member + StampUser) |
|------|----------------------|-------------------------------|
| 식별자 | UUID (축제별) | Member ID (전역) |
| 인증 방식 | 전화번호 + 이름 | 소셜 로그인 |
| JWT Subject | uuid | memberId |
| JWT Role | `STAMP` | `ROLE_VISITOR` |
| 개인정보 | StampUser에 암호화 저장 | Member에 저장 |
| 축제 간 공유 | 불가 | 가능 (1 Member → N StampUser) |

### StampUser 변경 사항

```java
@Entity
public class StampUser {
    // 기존 필드 (하위 호환)
    private String uuid;          // 레거시 인증용
    private String phone;         // nullable (Member 기반 시 미사용)
    private String name;          // nullable (Member 기반 시 미사용)

    // 신규 필드
    @ManyToOne(fetch = LAZY)
    private Member member;        // nullable (레거시 사용자는 null)

    // Member 기반 생성
    public static StampUser createForMember(Member member, int participantCount, String extraText) { ... }
}
```

### 인덱스

| 인덱스 | 컬럼 | 용도 |
|--------|------|------|
| `idx_uuid` | uuid | 레거시 인증 |
| `idx_phone_name_stamp` | phone, name, stamp_id | 레거시 로그인 |
| `idx_member_stamp` | member_id, stamp_id | Member 기반 조회 |

---

## JWT 인증 분기

### JwtProvider.getAuthentication() 분기 로직

```
JWT Token 파싱
    │
    ├── role == "STAMP"
    │   └── loadUserByUuid(subject)
    │       → CustomStampUserDetails (레거시)
    │
    ├── role == "ROLE_VISITOR"
    │   └── loadMemberById(Long.parseLong(subject))
    │       → CustomUserDetails (Member 기반)
    │
    └── role == "ROLE_SUPER_ADMIN" | "ROLE_AGENCY"
        └── loadUserByUsername(subject)
            → CustomUserDetails (Member 기반)
```

### 토큰 구조

| 역할 | Subject | Roles Claim | 만료 |
|------|---------|-------------|------|
| VISITOR | memberId (Long) | `["ROLE_VISITOR"]` | 7일 |
| STAMP (레거시) | uuid (String) | `["STAMP"]` | 7일 |
| AGENCY | loginId (String) | `["ROLE_AGENCY"]` | 7일 |
| SUPER_ADMIN | loginId (String) | `["ROLE_SUPER_ADMIN"]` | 7일 |

---

## 회원 API 명세

### VISITOR 인증 API

> **공개 엔드포인트** (인증 불필요)

#### 1. 소셜 로그인

```
POST /api/v1/auth/social-login
```

**Request Body**

```json
{
  "provider": "KAKAO",
  "accessToken": "카카오/네이버에서 발급받은 액세스 토큰"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `provider` | String | Y | `KAKAO` / `NAVER` |
| `accessToken` | String | Y | 소셜 플랫폼 액세스 토큰 |

**Response Body -- 기존 회원**

```json
{
  "isMember": true,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "provider": null,
  "providerId": null
}
```

**Response Body -- 미가입 사용자**

```json
{
  "isMember": false,
  "token": null,
  "provider": "KAKAO",
  "providerId": "1234567890"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `isMember` | boolean | 기존 회원 여부 |
| `token` | String | JWT 토큰 (기존 회원만) |
| `provider` | String | 소셜 제공자 (미가입 시만) |
| `providerId` | String | 소셜 고유 ID (미가입 시만, 회원가입에 필요) |

---

#### 2. 회원가입

```
POST /api/v1/auth/signup
```

**사전 조건**: SMS 인증 완료 (`/api/v1/auth/sms/send` → `/api/v1/auth/sms/verify`)

**Request Body**

```json
{
  "provider": "KAKAO",
  "providerId": "1234567890",
  "phone": "01012345678",
  "name": "홍길동",
  "termsAgreed": true,
  "privacyAgreed": true,
  "marketingAgreed": false,
  "residenceType": "자취",
  "residenceRegion": "서울",
  "residenceDistrict": "강남구",
  "visitType": "친구와 함께",
  "gender": "MALE",
  "birthDate": "2000-01-15"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `provider` | String | Y | `KAKAO` / `NAVER` |
| `providerId` | String | Y | 소셜 로그인에서 받은 ID |
| `phone` | String | Y | 전화번호 |
| `name` | String | Y | 이름 |
| `termsAgreed` | Boolean | Y | 서비스 이용약관 동의 |
| `privacyAgreed` | Boolean | Y | 개인정보 처리방침 동의 |
| `marketingAgreed` | Boolean | N | 마케팅 수신 동의 |
| `residenceType` | String | N | 거주 형태 |
| `residenceRegion` | String | N | 거주 지역 |
| `residenceDistrict` | String | N | 거주 구/군 |
| `visitType` | String | N | 방문 유형 |
| `gender` | String | N | `MALE` / `FEMALE` |
| `birthDate` | String | N | 생년월일 (`yyyy-MM-dd`) |

**Response Body**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### VISITOR 프로필 API

> **인증 필요** (ROLE_VISITOR)

#### 3. 내 정보 조회

```
GET /api/v1/member/profile/me
```

**Response Body**

```json
{
  "name": "홍길동",
  "phone": "01012345678"
}
```

#### 4. 마케팅 수신 동의 변경

```
PATCH /api/v1/member/profile/marketing-consent
```

```json
{
  "marketingAgreed": true
}
```

#### 5. 설문 정보 변경

```
PATCH /api/v1/member/profile/survey
```

```json
{
  "residenceType": "자취",
  "residenceRegion": "서울",
  "residenceDistrict": "강남구",
  "visitType": "친구와 함께",
  "gender": "MALE",
  "birthDate": "2000-01-15"
}
```

---

### AGENCY 인증 API

> **공개 엔드포인트** (인증 불필요)

#### 6. AGENCY 로그인

```
POST /api/v1/admin/auth/login
```

```json
{
  "loginId": "agency01",
  "password": "password123"
}
```

#### 7. AGENCY 회원가입

```
POST /api/v1/admin/auth/signup
```

**사전 조건**: SMS 인증 완료

```json
{
  "loginId": "agency01",
  "password": "password123",
  "companyEmail": "company@example.com",
  "companyName": "축제운영(주)",
  "managerName": "김매니저",
  "managerPosition": "팀장",
  "managerPhone": "01098765432"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `loginId` | String | Y | 4~50자 |
| `password` | String | Y | 비밀번호 |
| `companyEmail` | String | Y | 유효한 이메일 형식 |
| `companyName` | String | Y | 업체명 (최대 100자) |
| `managerName` | String | Y | 담당자명 (최대 50자) |
| `managerPosition` | String | Y | 직위 (최대 50자) |
| `managerPhone` | String | Y | `01[0-9]{8,9}` 형식 |

#### 8. 아이디 중복 확인

```
GET /api/v1/admin/auth/check-login-id?loginId=agency01
```

```json
{
  "available": true
}
```

---

---

# 스탬프 투어 (Stamp Tour)

축제 방문자가 미션을 수행하고 스탬프를 모아 경품을 수령하는 기능의 설계 문서입니다.

---

## 기능 개요

### 핵심 흐름

```
[관리자] 스탬프 투어 생성 → 미션 등록 → 경품 설정 → 활성화
    ↓
[방문자] 참여 신청 → 미션 수행 (QR 스캔) → 스탬프 수집 → 투어 완료 → 경품 수령
```

### 주요 기능

- 축제별 스탬프 투어 생성/관리
- 미션 등록 및 QR 코드 기반 완료 처리
- 미션 달성 수에 따른 다단계 경품 시스템
- 랜딩 페이지, 메인 페이지, 참여 가이드 등 UI 커스터마이징
- Member 기반 통합 인증 + 레거시 UUID 인증 하위 호환

---

## 엔티티 구조

### 관계도

```
Festival (1)
    │
    └── Stamp (N) ─── 스탬프 투어 루트
            │
            ├── Mission (N) ─── 개별 미션
            │       │
            │       ├── UserMission (N) ─── 사용자별 미션 진행 상태
            │       │
            │       └── MissionDetailsTemplate (N) ─── 미션 상세 UI 템플릿
            │               ├── MissionExtraInfo (N)
            │               └── Button (N)
            │
            ├── StampUser (N) ─── 참여 사용자
            │       ├── UserMission (N)
            │       ├── Custom (1) ─── 추가 정보 (학번 등)
            │       └── Member (N:1, nullable) ─── 통합 회원 연동
            │
            ├── StampMissionPrize (N) ─── 경품 등급
            │
            ├── StampNotice (N) ─── 참여 주의사항/개인정보 안내
            │
            ├── PageTemplate (N) ─── 랜딩/메인 페이지 템플릿
            │       └── Button (N)
            │
            └── ParticipateGuide (N) ─── 참여 가이드
                    └── ParticipateGuidePage (N)
```

### Stamp (스탬프 투어)

| 필드 | 타입 | 설명 |
|------|------|------|
| `title` | String | 투어 이름 |
| `active` | Boolean | 활성화 여부 |
| `showStamp` | Boolean | 노출 여부 |
| `finishCount` | Integer | 투어 완료에 필요한 미션 수 |
| `missionCount` | Integer | 전체 미션 수 |
| `maxParticipantCount` | Integer | 참여 시 최대 인원 수 |
| `authMethod` | AuthMethod | `TAG_SCAN` / `USER_CODE_PRESENT` |
| `joinVerificationMethod` | JoinVerificationMethod | `NONE` / `SMS` / `PASS` |
| `prizeReceiptAuthPassword` | String | 경품 수령 인증 비밀번호 |
| `mainColor`, `subColor` | String | UI 테마 색상 |
| `backgroundColor`, `backgroundSubColor` | String | 배경 색상 |
| `defaultDetailLayout` | MissionDetailsDesignLayout | 기본 미션 상세 레이아웃 |
| `extraInfoTemplate` | String (TEXT) | 참여 시 추가 정보 입력 폼 템플릿 (JSON) |
| `prizeExchangeImgType` | PrizeExchangeImgType | `DEFAULT` / `CUSTOM` |

### Mission (미션)

| 필드 | 타입 | 설명 |
|------|------|------|
| `boothId` | Long | 연결된 부스/장소 ID |
| `title` | String | 미션명 |
| `content` | String | 미션 설명 |
| `place` | String | 미션 장소 |
| `time` | String | 운영 시간 |
| `clearedThumbnail` | String | 완료 시 표시 이미지 |
| `notClearedThumbnail` | String | 미완료 시 표시 이미지 |
| `showMission` | Boolean | 노출 여부 |
| `requiredSuccessCount` | Integer | 완료에 필요한 성공 횟수 (기본 1) |

### UserMission (사용자 미션 진행)

| 필드 | 타입 | 설명 |
|------|------|------|
| `complete` | Boolean | 미션 완료 여부 |
| `successCount` | Integer | 현재 성공 횟수 |

### StampUser (참여 사용자)

| 필드 | 타입 | 설명 |
|------|------|------|
| `uuid` | String (unique) | 레거시 인증용 식별자 |
| `phone` | String (encrypted) | 전화번호 (레거시, nullable) |
| `name` | String (encrypted) | 이름 (레거시, nullable) |
| `member` | Member (FK, nullable) | 통합 회원 연동 |
| `finished` | Boolean | 투어 완료 여부 |
| `participantCount` | Integer | 참여 인원 수 |
| `extraText` | String | 추가 정보 응답 |
| `receivedPrizeName` | String | 수령한 경품명 |

---

## 사용자 참여 플로우

### 1. 참여 신청

```
[방문자 - Member 기반]
POST /api/v3/user/festivals/{festivalId}/stamp-tours/{stampId}/participate
    { participantCount: 2, extraText: "..." }
    │
    ▼
[MemberStampTourService.participate()]
    ├── VISITOR 회원 확인
    ├── 스탬프 투어 활성 상태 확인
    ├── 중복 참여 확인 (existsByMemberIdAndStampId)
    ├── StampUser.createForMember(member, count, extraText)
    ├── 전체 미션에 대해 UserMission 생성 (미완료 상태)
    └── 저장
```

```
[방문자 - 레거시]
POST /api/v2/user/festivals/{festivalId}/stamp-tours/{stampId}/signup
    { phone, name, participantCount, extraText }
    │
    ▼
[StampTourUserService.signup()]
    ├── 전화번호/이름 AES 암호화
    ├── UUID 생성
    ├── StampUser 생성 + Authority("STAMP") 추가
    ├── 전체 미션에 대해 UserMission 생성
    └── JWT 토큰 발급 (subject=uuid, role="STAMP")
```

### 2. 미션 수행 (QR 스캔)

```
[방문자]
    QR 코드 스캔
    │
    ▼
PATCH /api/v3/.../missions/{missionId}/verify  (Member 기반)
PATCH /api/v2/.../verify/qr                     (레거시)
    │
    ▼
[UserMission.increaseSuccess()]
    ├── successCount++
    ├── if successCount >= mission.requiredSuccessCount
    │   └── complete = true
    └── 저장
```

### 3. 투어 완료 확인

```
POST /api/v3/.../finish  (Member 기반)
PATCH /stamp/user/check/v2/{uuid}  (레거시)
    │
    ▼
[StampUser.canFinishTour()]
    ├── 완료된 미션 수 카운트
    ├── if 완료 수 >= stamp.finishCount
    │   └── finished = true
    └── else → 미완료 상태 유지
```

---

## 미션 완료 로직

### 단건 성공 미션 (기본)

```
requiredSuccessCount = 1
    │
    QR 스캔 1회 → successCount = 1 → complete = true
```

### 다건 성공 미션

```
requiredSuccessCount = 3
    │
    QR 스캔 1회 → successCount = 1 → complete = false
    QR 스캔 2회 → successCount = 2 → complete = false
    QR 스캔 3회 → successCount = 3 → complete = true
```

### 투어 완료 조건

| 버전 | 조건 |
|------|------|
| v1 | **모든** UserMission이 complete = true |
| v2/v3 | 완료된 미션 수 >= `stamp.finishCount` |

---

## 경품 시스템

### StampMissionPrize (경품 등급)

다단계 경품 시스템으로, 미션 달성 수에 따라 서로 다른 경품 수령 가능:

```
Stamp
├── StampMissionPrize { requiredCount: 3, description: "스티커 세트" }
├── StampMissionPrize { requiredCount: 5, description: "에코백" }
└── StampMissionPrize { requiredCount: 8, description: "텀블러" }
```

### 경품 수령 플로우

```
[방문자]
    투어 완료 (finished = true)
    │
    ▼
GET /api/v3/.../prizes/qr
    │  이름, 전화번호, 참여 인원, 추가 정보 반환
    ▼
[운영진]
    QR 정보 확인 + 인증 비밀번호 확인 (선택)
    │
    ▼
PATCH /api/v3/.../prize
    { prizeName: "에코백" }
    │
    ▼
[StampUser.receivedPrizeName = "에코백"]
```

---

## 인증 방식

### 두 가지 인증 체계 공존

| 구분 | Member 기반 (v3) | UUID 기반 (v2, 레거시) |
|------|-------------------|------------------------|
| 인증 | 소셜 로그인 → JWT (ROLE_VISITOR) | 전화번호+이름 → JWT (STAMP) |
| 식별자 | memberId (Long) | uuid (String) |
| 개인정보 | Member 엔티티에 저장 | StampUser에 AES 암호화 저장 |
| StampUser.member | 연결됨 | null |
| 축제 간 공유 | 가능 (동일 Member) | 불가 (축제마다 새 UUID) |

### 참여 여부 확인

```java
// Member 기반
stampUserRepository.existsByMemberIdAndStampId(memberId, stampId)

// 레거시
stampUserRepository.findByEncryptedPhoneAndNameAndStamp(encPhone, encName, stamp)
```

---

## UI 커스터마이징

### 페이지 구성

| 페이지 | 설정 항목 |
|--------|-----------|
| 랜딩 페이지 | 배경 이미지, 아이콘, 설명, 버튼 (0~2개) |
| 메인 페이지 | 그리드 레이아웃 (Nx2, Nx3, LIST, CARD), 배경 이미지 |
| 참여 가이드 | 슬라이드/스텝 방식, 제목/미디어/요약/상세/추가 정보 |
| 미션 상세 | 카드/디테일 레이아웃 (+슬라이드), 미디어, 추가 정보, 버튼 |

### 디자인 Enum 목록

| Enum | 값 |
|------|----|
| `LandingPageDesignTemplate` | NONE, SIMPLE, FULL |
| `MainPageDesignTemplate` | GRID_Nx2, GRID_Nx3, LIST, CARD |
| `MissionDetailsDesignLayout` | CARD, DETAIL, CARD_SLIDE, DETAIL_SLIDE |
| `GuideDesignTemplate` | FULL, SIMPLE |
| `GuideSlideMethod` | SLIDE, STEP |
| `MediaSpec` | NONE, IMAGE, VIDEO, SLIDESHOW |
| `ButtonLayout` | NONE, ONE, TWO_SYM, TWO_ASYM |
| `ButtonAction` | OPEN_URL |
| `ExtraInfoLayout` | LIST, GRID |

---

## 스탬프 투어 API 명세 - 사용자

### Member 기반 (v3)

> **Base URL**: `/api/v3/user/festivals/{festivalId}/stamp-tours`
>
> **인증**: JWT (ROLE_VISITOR)

#### 1. 스탬프 투어 참여

```
POST /{stampId}/participate
```

**Request Body**

```json
{
  "participantCount": 2,
  "extraText": "추가 정보"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `participantCount` | Integer | N | 참여 인원 수 |
| `extraText` | String | N | 추가 정보 (커스텀 폼 응답) |

---

#### 2. 미션 보드 조회

```
GET /{stampId}/missions
```

**Response Body**

```json
{
  "clearCount": 3,
  "totalCount": 8,
  "finished": false,
  "missions": [
    {
      "userMissionId": 1,
      "missionId": 10,
      "title": "포토존에서 사진 찍기",
      "clear": true,
      "clearedThumbnail": "https://...",
      "notClearedThumbnail": "https://..."
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `clearCount` | int | 완료한 미션 수 |
| `totalCount` | int | 전체 미션 수 |
| `finished` | boolean | 투어 완료 여부 |
| `missions[].userMissionId` | Long | 사용자 미션 ID |
| `missions[].missionId` | Long | 미션 ID |
| `missions[].title` | String | 미션명 |
| `missions[].clear` | boolean | 미션 완료 여부 |
| `missions[].clearedThumbnail` | String | 완료 시 이미지 |
| `missions[].notClearedThumbnail` | String | 미완료 시 이미지 |

---

#### 3. 미션 상세 조회

```
GET /{stampId}/missions/{missionId}
```

미션 상세 정보, UI 템플릿 (미디어, 추가 정보, 버튼), 사용자 완료 상태를 함께 반환합니다.

---

#### 4. QR 미션 인증

```
PATCH /{stampId}/missions/{missionId}/verify
```

QR 코드 스캔으로 미션을 완료 처리합니다. `successCount`를 증가시키고, `requiredSuccessCount`에 도달하면 자동 완료됩니다.

---

#### 5. 경품 수령 QR 정보 조회

```
GET /{stampId}/prizes/qr
```

**Response Body**

```json
{
  "name": "홍길동",
  "phone": "01012345678",
  "participantCount": 2,
  "extraText": "추가 정보"
}
```

운영진에게 보여줄 경품 수령 확인용 정보입니다.

---

#### 6. 투어 완료 확인

```
POST /{stampId}/finish
```

완료된 미션 수가 `stamp.finishCount` 이상이면 `finished = true`로 변경합니다.

---

#### 7. 경품 수령 기록

```
PATCH /{stampId}/prize
```

```json
{
  "prizeName": "에코백"
}
```

---

### 레거시 (v2)

> **Base URL**: `/api/v2/user/festivals/{festivalId}/stamp-tours`
>
> **인증**: JWT (STAMP role, UUID 기반)

#### 1. 회원가입

```
POST /{stampId}/signup
```

```json
{
  "phone": "01012345678",
  "name": "홍길동",
  "participantCount": 1,
  "extraText": ""
}
```

**Response**: JWT 토큰 (subject=uuid, role=STAMP)

#### 2. 로그인

```
POST /{stampId}/login
```

```json
{
  "phone": "01012345678",
  "name": "홍길동"
}
```

**Response**: JWT 토큰

#### 3~6. 미션 보드/상세/QR 인증/경품 QR

v3와 동일한 구조, 인증만 UUID 기반으로 다릅니다.

---

## 스탬프 투어 API 명세 - 관리자

> **Base URL**: `/api/v2/admin/festivals/{festivalId}/stamp-tours`
>
> **인증**: JWT (ROLE_SUPER_ADMIN 또는 ROLE_AGENCY)

### 스탬프 투어 관리

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/{stampId}` | 스탬프 투어 생성 |
| GET | | 스탬프 투어 목록 조회 |
| DELETE | `/{stampId}` | 스탬프 투어 삭제 |
| PATCH | `/{stampId}/showStamp` | 노출 토글 |
| GET | `/{stampId}/settings/basic` | 기본 설정 조회 |
| PATCH | `/{stampId}/settings/basic` | 기본 설정 수정 |
| GET | `/{stampId}/settings/user-info` | 참여 조건 조회 |
| PATCH | `/{stampId}/settings/user-info` | 참여 조건 수정 |
| GET | `/{stampId}/settings/notice` | 공지/개인정보 조회 |
| PUT | `/{stampId}/settings/notice` | 공지/개인정보 수정 |
| GET | `/{stampId}/settings/landing` | 랜딩 페이지 조회 |
| PUT | `/{stampId}/settings/landing` | 랜딩 페이지 수정 |
| GET | `/{stampId}/settings/main` | 메인 페이지 조회 |
| PUT | `/{stampId}/settings/main` | 메인 페이지 수정 |
| GET | `/{stampId}/participateGuide` | 참여 가이드 조회 |
| PUT | `/{stampId}/participateGuide` | 참여 가이드 수정 |
| PATCH | `/{stampId}/prizeTicketImg` | 경품 교환권 이미지 설정 |

### 미션 관리

> **Base URL**: `.../stamp-tours/{stampId}/missions`

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | | 미션 생성 |
| GET | | 미션 목록 조회 |
| DELETE | `/{missionId}` | 미션 삭제 |
| PATCH | `/show` | 전체 미션 노출 설정 |
| PATCH | `/{missionId}/show` | 개별 미션 노출 토글 |
| GET | `/settings` | 미션 기본 설정 조회 |
| PUT | `/settings` | 미션 기본 설정 수정 (missionCount, designLayout) |
| GET | `/prizes` | 경품 목록 조회 |
| POST | `/prizes` | 경품 추가 |
| PATCH | `/prizes/{prizeId}` | 경품 수정 |
| DELETE | `/prizes/{prizeId}` | 경품 삭제 |
| PATCH | `/details/{missionId}` | 미션 상세 템플릿 수정 |

### 참여자 관리

> **Base URL**: `.../stamp-tours/{stampId}/users`

관리자가 참여자 목록 조회, 검색, 상태 관리를 할 수 있는 API입니다.
