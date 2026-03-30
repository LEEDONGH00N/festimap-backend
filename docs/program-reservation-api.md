# 프로그램 예약 관리 API 명세서

---

## 목차

### 사용자 API (`/programs`)
1. [프로그램 리스트 조회](#1-프로그램-리스트-조회)
2. [프로그램 단건 조회](#2-프로그램-단건-조회)
3. [예약 날짜 목록 조회](#3-예약-날짜-목록-조회-모달-1단계)
4. [예약 슬롯 목록 조회](#4-예약-슬롯-목록-조회-모달-2단계)
5. [예약 HOLD](#5-예약-hold-모달-3단계)
6. [체크아웃 렌더링](#6-체크아웃-렌더링)
7. [예약 확정](#7-예약-확정)
8. [단건 예약 조회](#8-단건-예약-조회)
9. [다건 예약 조회](#9-다건-예약-조회)
10. [예약 취소](#10-예약-취소)

### 관리자 API (`/admin`)
11. [Enum 정의](#enum-정의)
12. [프로그램 관리 API](#프로그램-관리-api) (`/admin/programs`)
13. [예약 회차 관리 API](#예약-회차-관리-api) (`/admin/reservations`)

---

## 사용자 프로그램 예약 API

> **Base URL**: `/programs`
>
> 모든 엔드포인트는 사용자 인증(VISITOR 또는 SUPER_ADMIN)이 필요합니다.

---

### 1. 프로그램 리스트 조회

사용자에게 노출 가능한 프로그램 목록을 조회합니다.

**노출 조건:**
- `isActive = true`
- 현재 시간이 `activeStartAt` ~ `activeEndAt` 범위 내
- 현재 시간이 `bookingOpenAt` ~ `bookingCloseAt` 범위 내

```
GET /programs?festivalId={festivalId}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `festivalId` | Long | Y | 축제 ID |

**Response Body**

```json
{
  "responses": [
    {
      "id": 1,
      "name": "도자기 체험",
      "thumbnailUrl": "https://example.com/thumb.jpg",
      "price": 0,
      "durationTime": "2시간",
      "availableAge": "만 8세 이상",
      "maxPersonCount": 4,
      "tags": [
        { "tagId": 1, "tagName": "체험" }
      ],
      "dates": [
        { "date": "2026-03-01", "isReservable": true },
        { "date": "2026-03-02", "isReservable": false }
      ]
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `responses[].id` | Long | 프로그램 ID |
| `responses[].name` | String | 프로그램명 |
| `responses[].thumbnailUrl` | String | 썸네일 이미지 URL |
| `responses[].price` | int | 이용료 (원) |
| `responses[].durationTime` | String | 소요시간 텍스트 |
| `responses[].availableAge` | String | 이용 가능 연령 텍스트 |
| `responses[].maxPersonCount` | int | 1회 최대 예약 인원 |
| `responses[].tags[]` | Array | 태그 목록 |
| `responses[].tags[].tagId` | Long | 태그 ID |
| `responses[].tags[].tagName` | String | 태그명 |
| `responses[].dates[]` | Array | 예약 가능 날짜 목록 |
| `responses[].dates[].date` | String | 날짜 (`yyyy-MM-dd`) |
| `responses[].dates[].isReservable` | boolean | 예약 가능 여부 |

---

### 2. 프로그램 단건 조회

특정 프로그램의 상세 정보를 조회합니다. 리스트 조회보다 상세한 정보(요약, 설명, 주의사항, 공통 템플릿)를 포함합니다.

```
GET /programs/{programId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "id": 1,
  "name": "도자기 체험",
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "price": 0,
  "durationTime": "2시간",
  "availableAge": "만 8세 이상",
  "maxPersonCount": 4,
  "tags": [
    { "tagId": 1, "tagName": "체험" }
  ],
  "dates": [
    { "date": "2026-03-01", "isReservable": true }
  ],
  "summaries": [
    { "label": "소요시간", "value": "약 2시간" },
    { "label": "정원", "value": "회당 20명" }
  ],
  "descriptions": [
    {
      "oneLine": "직접 도자기를 만들어보세요",
      "detail": "전문 강사와 함께하는 도자기 체험 프로그램입니다.",
      "imageUrl": "https://example.com/desc.jpg"
    }
  ],
  "cautions": [
    { "content": "체험 시작 10분 전까지 도착해주세요." }
  ],
  "templates": [
    {
      "id": 1,
      "sortOrder": 0,
      "title": "환불 규정",
      "content": "체험 3일 전 100% 환불 가능"
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 프로그램 ID |
| `name` | String | 프로그램명 |
| `thumbnailUrl` | String | 썸네일 이미지 URL |
| `price` | int | 이용료 (원) |
| `durationTime` | String | 소요시간 텍스트 |
| `availableAge` | String | 이용 가능 연령 텍스트 |
| `maxPersonCount` | int | 1회 최대 예약 인원 |
| `tags[]` | Array | 태그 목록 |
| `dates[]` | Array | 예약 가능 날짜 목록 |
| `summaries[]` | Array | 요약 정보 목록 (SUMMARY 블록) |
| `summaries[].label` | String | 요약 항목명 |
| `summaries[].value` | String | 요약 정보 |
| `descriptions[]` | Array | 설명 목록 (DESCRIPTION 블록) |
| `descriptions[].oneLine` | String | 한줄 설명 |
| `descriptions[].detail` | String | 상세 설명 |
| `descriptions[].imageUrl` | String | 설명 이미지 URL |
| `cautions[]` | Array | 주의사항 목록 (CAUTION 블록) |
| `cautions[].content` | String | 주의사항 내용 |
| `templates[]` | Array | 축제 공통 템플릿 목록 |
| `templates[].id` | Long | 템플릿 ID |
| `templates[].sortOrder` | int | 정렬 순서 |
| `templates[].title` | String | 템플릿 제목 |
| `templates[].content` | String | 템플릿 내용 |

---

### 3. 예약 날짜 목록 조회 (모달 1단계)

프로그램의 예약 가능 날짜 목록을 조회합니다.

```
GET /programs/{programId}/dates
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "programId": 1,
  "maxPersonCount": 4,
  "holdMinutes": 10,
  "selectedDate": "2026-03-01",
  "dates": [
    { "date": "2026-03-01", "isReservable": true },
    { "date": "2026-03-02", "isReservable": false }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |
| `maxPersonCount` | int | 1회 최대 예약 인원 |
| `holdMinutes` | Integer | HOLD 유지 시간 (분) |
| `selectedDate` | String | 기본 선택 날짜 (첫 번째 날짜) |
| `dates[]` | Array | 날짜 목록 |
| `dates[].date` | String | 날짜 (`yyyy-MM-dd`) |
| `dates[].isReservable` | boolean | 해당 날짜에 예약 가능한 슬롯 존재 여부 |

---

### 4. 예약 슬롯 목록 조회 (모달 2단계)

선택한 날짜의 예약 가능 회차 목록을 조회합니다.

```
GET /programs/{programId}/reservation-slots?date={date}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Query Parameters**

| 이름 | 타입 | 필수 | 형식 | 설명 |
|---|---|---|---|---|
| `date` | String | Y | `yyyy-MM-dd` | 조회할 날짜 |

**Response Body**

```json
{
  "programId": 1,
  "date": "2026-03-01",
  "maxPersonCount": 4,
  "slots": [
    {
      "slotId": 1,
      "startTime": "10:00",
      "remaining": 15,
      "isReservable": true
    },
    {
      "slotId": 2,
      "startTime": "14:00",
      "remaining": 0,
      "isReservable": false
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |
| `date` | String | 조회한 날짜 (`yyyy-MM-dd`) |
| `maxPersonCount` | int | 1회 최대 예약 인원 |
| `slots[]` | Array | 슬롯(회차) 목록 |
| `slots[].slotId` | Long | 슬롯 ID |
| `slots[].startTime` | String | 시작 시간 (`HH:mm`) |
| `slots[].remaining` | int | 잔여 정원 |
| `slots[].isReservable` | boolean | 예약 가능 여부 (`remaining > 0`) |

---

### 5. 예약 HOLD (모달 3단계)

재고를 선점하고 HOLD 예약을 생성합니다.

**멱등성:**
- 같은 예약 의도(재시도, 중복 클릭)라면 동일한 `Idempotency-Key`를 재사용하세요.
- 새로운 예약 시도마다 새 UUID를 생성합니다.

```
POST /programs/reservations/holds
```

**Headers**

| 이름 | 필수 | 설명 |
|---|---|---|
| `Idempotency-Key` | Y | 멱등성 키 (UUID 권장) |

**Request Body**

```json
{
  "programId": 1,
  "slotId": 1,
  "headcount": 2
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `programId` | Long | Y | 프로그램 ID |
| `slotId` | Long | Y | 슬롯 ID |
| `headcount` | Integer | Y | 예약 인원 (최소 1) |

**Response Body**

```json
{
  "reservationId": 1,
  "status": "HOLD",
  "expiresAt": "2026-03-01T10:10:00",
  "holdMinutes": 10,
  "summary": {
    "programId": 1,
    "slotId": 1,
    "name": "도자기 체험",
    "date": "2026-03-01",
    "startTime": "10:00",
    "durationTime": "2시간",
    "headcount": 2,
    "priceAmount": 0
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |
| `status` | String | 예약 상태 (`HOLD`) |
| `expiresAt` | String | HOLD 만료 시간 (ISO 8601) |
| `holdMinutes` | Integer | HOLD 유지 시간 (분) |
| `summary` | Object | 예약 요약 정보 |
| `summary.programId` | Long | 프로그램 ID |
| `summary.slotId` | Long | 슬롯 ID |
| `summary.name` | String | 프로그램명 |
| `summary.date` | String | 예약 날짜 (`yyyy-MM-dd`) |
| `summary.startTime` | String | 시작 시간 (`HH:mm`) |
| `summary.durationTime` | String | 소요시간 텍스트 |
| `summary.headcount` | int | 예약 인원 |
| `summary.priceAmount` | int | 이용료 |

---

### 6. 체크아웃 렌더링

체크아웃 화면에 필요한 주의사항과 공통 템플릿을 조회합니다.

**만료 처리:**
- HOLD가 만료된 경우 서버가 즉시 EXPIRED 처리 및 재고 복구를 수행합니다.
- 만료 상태에서는 `RESERVATION_EXPIRED` 에러를 반환합니다.

```
POST /programs/reservations/{reservationId}/checkout
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

**Response Body**

```json
{
  "reservationId": 1,
  "status": "HOLD",
  "expiresAt": "2026-03-01T10:10:00",
  "display": {
    "cautions": [
      { "id": 1, "content": "체험 시작 10분 전까지 도착해주세요." }
    ],
    "templates": [
      { "id": 1, "title": "환불 규정", "content": "체험 3일 전 100% 환불 가능" }
    ]
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |
| `status` | String | 예약 상태 |
| `expiresAt` | String | HOLD 만료 시간 (ISO 8601) |
| `display.cautions[]` | Array | 주의사항 목록 |
| `display.cautions[].id` | Long | 블록 ID |
| `display.cautions[].content` | String | 주의사항 내용 |
| `display.templates[]` | Array | 공통 템플릿 목록 |
| `display.templates[].id` | Long | 템플릿 ID |
| `display.templates[].title` | String | 템플릿 제목 |
| `display.templates[].content` | String | 템플릿 내용 |

---

### 7. 예약 확정

HOLD 상태의 예약을 확정합니다.

```
POST /programs/reservations/{reservationId}/confirm
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

**Request Body**

```json
{
  "bookerName": "홍길동",
  "bookerPhone": "010-1234-5678",
  "visitorName": "홍길동",
  "visitorPhone": "010-1234-5678"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `bookerName` | String | N | 예약자 이름 |
| `bookerPhone` | String | N | 예약자 연락처 |
| `visitorName` | String | N | 방문자 이름 |
| `visitorPhone` | String | N | 방문자 연락처 |

**Response Body**

```json
{
  "reservationId": 1
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

---

### 8. 단건 예약 조회

특정 예약의 상세 정보를 조회합니다.

```
GET /programs/reservations/{reservationId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

**Response Body**

```json
{
  "id": 1,
  "name": "도자기 체험",
  "tags": [
    { "tagId": 1, "tagName": "체험" }
  ],
  "date": "2026-03-01",
  "durationTime": "2시간",
  "headCount": 2,
  "priceAmount": 0
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 예약 ID |
| `name` | String | 프로그램명 |
| `tags[]` | Array | 태그 목록 |
| `date` | String | 예약 날짜 (`yyyy-MM-dd`) |
| `durationTime` | String | 소요시간 텍스트 |
| `headCount` | int | 예약 인원 |
| `priceAmount` | int | 총 이용료 (단가 × 인원) |

---

### 9. 다건 예약 조회

사용자의 확정된 예약 목록을 조회합니다.

```
GET /programs/reservations
```

**Response Body**

```json
{
  "responses": [
    {
      "id": 1,
      "name": "도자기 체험",
      "tags": [
        { "tagId": 1, "tagName": "체험" }
      ],
      "date": "2026-03-01",
      "durationTime": "2시간",
      "headCount": 2,
      "priceAmount": 0
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `responses[]` | Array | 예약 목록 |
| `responses[].id` | Long | 예약 ID |
| `responses[].name` | String | 프로그램명 |
| `responses[].tags[]` | Array | 태그 목록 |
| `responses[].date` | String | 예약 날짜 (`yyyy-MM-dd`) |
| `responses[].durationTime` | String | 소요시간 텍스트 |
| `responses[].headCount` | int | 예약 인원 |
| `responses[].priceAmount` | int | 총 이용료 |

---

### 10. 예약 취소

확정된 예약을 취소합니다.

**취소 정책:**
- `CONFIRMED` 상태의 예약만 취소 가능
- 슬롯 시작일 **전날 18:00**까지 취소 가능

```
POST /programs/reservations/{reservationId}/cancel
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

**Response Body**

```json
{
  "reservationId": 1
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `reservationId` | Long | 예약 ID |

**에러 응답**

| 상황 | 에러 코드 |
|---|---|
| 취소 불가 상태 | `INVALID_INPUT_VALUE` |
| 취소 가능 시간 초과 | `CANCEL_NOT_ALLOWED` |

---

## 관리자 API

> **Base URL**: `/admin`
>
> 모든 엔드포인트는 관리자 인증이 필요합니다.

---

---

## Enum 정의

### ProgramSlotType

슬롯(회차)의 유형을 나타냅니다.

| 값 | 설명 |
|---|---|
| `DATE` | 날짜 단위 슬롯 (하루 1개, 정원 무제한) |
| `TIME` | 시간 단위 슬롯 (패턴 기반, 정원 제한) |

### ProgramReservationStatus

예약 상태를 나타냅니다.

| 값 | 설명 |
|---|---|
| `HOLD` | 임시 예약 (확정 대기) |
| `CONFIRMED` | 예약 확정 |
| `EXPIRED` | HOLD 만료 |
| `CANCELED` | 예약 취소 |

### ReservationSearchField

예약 검색 대상 필드입니다.

| 값 | 설명 |
|---|---|
| `PROGRAM_NAME` | 프로그램명 |
| `BOOKER_NAME` | 예약자 이름 |
| `BOOKER_PHONE` | 예약자 연락처 |
| `VISITOR_NAME` | 방문자 이름 |
| `VISITOR_PHONE` | 방문자 연락처 |

### PricingType

프로그램 이용료 유형입니다.

| 값 | 설명 |
|---|---|
| `FREE` | 무료 |
| `PAID` | 유료 |

### PersonLimit

프로그램 인원 제한 유형입니다.

| 값 | 설명 |
|---|---|
| `UNLIMITED` | 인원 무제한 |
| `LIMITED` | 인원 제한 |

### BlockType

프로그램 상세 정보 블록 유형입니다.

| 값 | 설명 | 사용 필드 |
|---|---|---|
| `SUMMARY` | 프로그램 요약 안내 | `summaryLabel`, `summaryValue` |
| `DESCRIPTION` | 프로그램 설명 | `descriptionOneLine`, `descriptionDetail`, `descriptionImageUrl` |
| `CAUTION` | 주의사항 | `cautionContent` |

---

## 프로그램 관리 API

### 1. 프로그램 생성

새 프로그램을 생성합니다. 생성 시 기본으로 **미노출(isActive=false)** 상태입니다.

```
POST /admin/programs?festivalId={festivalId}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `festivalId` | Long | Y | 축제 ID |

**Request Body**

```json
{
  "name": "도자기 체험"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | Y | 프로그램명 (공백 불가) |


---

### 2. 프로그램 리스트 조회

축제에 속한 프로그램 목록을 조회합니다. 프로그램명으로 검색할 수 있습니다.

```
GET /admin/programs?festivalId={festivalId}&name={name}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `festivalId` | Long | Y | 축제 ID |
| `name` | String | N | 프로그램명 검색어 (부분 일치) |

**Response Body**

```json
{
  "responses": [
    {
      "id": 1,
      "name": "도자기 체험",
      "isActive": false
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `responses[].id` | Long | 프로그램 ID |
| `responses[].name` | String | 프로그램명 |
| `responses[].isActive` | boolean | 노출 여부 |

---

### 3. 프로그램 상세 조회

프로그램의 전체 정보를 조회합니다. 태그, 상세 블록, 축제 공통 템플릿을 모두 포함합니다.

```
GET /admin/programs/{programId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "id": 1,
  "name": "도자기 체험",
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "pricingType": "FREE",
  "priceAmount": 0,
  "durationTime": "2시간",
  "availableAge": "만 8세 이상",
  "personLimit": "UNLIMITED",
  "maxPersonCount": 0,
  "isActive": true,
  "activeStartAt": "2026-02-01T10:00:00",
  "activeEndAt": "2026-02-28T23:59:00",
  "tags": [
    {
      "tagId": 1,
      "tagName": "체험",
      "sortOrder": 0
    }
  ],
  "blocks": [
    {
      "id": 1,
      "type": "SUMMARY",
      "sortOrder": 0,
      "summaryLabel": "소요시간",
      "summaryValue": "약 2시간",
      "descriptionOneLine": null,
      "descriptionDetail": null,
      "descriptionImageUrl": null,
      "cautionContent": null
    },
    {
      "id": 2,
      "type": "DESCRIPTION",
      "sortOrder": 1,
      "summaryLabel": null,
      "summaryValue": null,
      "descriptionOneLine": "직접 도자기를 만들어보세요",
      "descriptionDetail": "전문 강사와 함께하는 도자기 체험 프로그램입니다.",
      "descriptionImageUrl": "https://example.com/desc.jpg",
      "cautionContent": null
    },
    {
      "id": 3,
      "type": "CAUTION",
      "sortOrder": 2,
      "summaryLabel": null,
      "summaryValue": null,
      "descriptionOneLine": null,
      "descriptionDetail": null,
      "descriptionImageUrl": null,
      "cautionContent": "체험 시작 10분 전까지 도착해주세요."
    }
  ],
  "templates": [
    {
      "id": 1,
      "sortOrder": 0,
      "title": "환불 규정",
      "content": "체험 3일 전 100% 환불 가능"
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 프로그램 ID |
| `name` | String | 프로그램명 |
| `thumbnailUrl` | String | 썸네일 이미지 URL |
| `pricingType` | String | `FREE` / `PAID` |
| `priceAmount` | int | 이용료 (원) |
| `durationTime` | String | 소요시간 텍스트 (예: "2시간") |
| `availableAge` | String | 이용 가능 연령 텍스트 (예: "만 8세 이상") |
| `personLimit` | String | `UNLIMITED` / `LIMITED` |
| `maxPersonCount` | int | 최대 인원수 (LIMITED일 때 사용) |
| `isActive` | boolean | 노출 여부 |
| `activeStartAt` | String | 노출 시작 일시 (ISO 8601) |
| `activeEndAt` | String | 노출 종료 일시 (ISO 8601) |
| `tags[]` | Array | 태그 목록 |
| `tags[].tagId` | Long | 태그 ID |
| `tags[].tagName` | String | 태그명 |
| `tags[].sortOrder` | int | 정렬 순서 |
| `blocks[]` | Array | 상세 정보 블록 목록 |
| `blocks[].id` | Long | 블록 ID |
| `blocks[].type` | String | `SUMMARY` / `DESCRIPTION` / `CAUTION` |
| `blocks[].sortOrder` | int | 정렬 순서 |
| `blocks[].summaryLabel` | String | 요약 항목명 (SUMMARY 타입) |
| `blocks[].summaryValue` | String | 요약 정보 (SUMMARY 타입) |
| `blocks[].descriptionOneLine` | String | 한줄 설명 (DESCRIPTION 타입) |
| `blocks[].descriptionDetail` | String | 상세 설명 (DESCRIPTION 타입) |
| `blocks[].descriptionImageUrl` | String | 설명 이미지 URL (DESCRIPTION 타입) |
| `blocks[].cautionContent` | String | 주의사항 내용 (CAUTION 타입) |
| `templates[]` | Array | 축제 공통 템플릿 목록 |
| `templates[].id` | Long | 템플릿 ID |
| `templates[].sortOrder` | int | 정렬 순서 |
| `templates[].title` | String | 템플릿 제목 |
| `templates[].content` | String | 템플릿 내용 |

---

### 4. 프로그램 상세 정보 수정

프로그램의 상세 정보를 저장합니다. **태그와 블록은 전체 교체** 방식으로 동작합니다. (기존 항목 삭제 후 새로 생성)

```
PATCH /admin/programs/{programId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Request Body**

```json
{
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "pricingType": "FREE",
  "priceAmount": 0,
  "durationTime": "2시간",
  "availableAge": "만 8세 이상",
  "personLimit": "UNLIMITED",
  "maxPersonCount": 0,
  "tags": [
    { "tagId": 1 },
    { "tagId": 3 }
  ],
  "blocks": [
    {
      "type": "SUMMARY",
      "summaryLabel": "소요시간",
      "summaryValue": "약 2시간"
    },
    {
      "type": "DESCRIPTION",
      "descriptionOneLine": "직접 도자기를 만들어보세요",
      "descriptionDetail": "전문 강사와 함께하는 도자기 체험 프로그램입니다.",
      "descriptionImageUrl": "https://example.com/desc.jpg"
    },
    {
      "type": "CAUTION",
      "cautionContent": "체험 시작 10분 전까지 도착해주세요."
    }
  ]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `thumbnailUrl` | String | N | 썸네일 이미지 URL |
| `pricingType` | String | N | `FREE` / `PAID` |
| `priceAmount` | int | N | 이용료 (원) |
| `durationTime` | String | N | 소요시간 텍스트 |
| `availableAge` | String | N | 이용 가능 연령 텍스트 |
| `personLimit` | String | N | `UNLIMITED` / `LIMITED` |
| `maxPersonCount` | int | N | 최대 인원수 |
| `tags[]` | Array | N | 태그 목록 (전체 교체) |
| `tags[].tagId` | Long | Y | 태그 ID |
| `blocks[]` | Array | N | 블록 목록 (전체 교체, 배열 순서가 sortOrder) |
| `blocks[].type` | String | Y | `SUMMARY` / `DESCRIPTION` / `CAUTION` |
| `blocks[].summaryLabel` | String | N | 요약 항목명 (SUMMARY 타입에서 사용) |
| `blocks[].summaryValue` | String | N | 요약 정보 (SUMMARY 타입에서 사용) |
| `blocks[].descriptionOneLine` | String | N | 한줄 설명 (DESCRIPTION 타입에서 사용) |
| `blocks[].descriptionDetail` | String | N | 상세 설명 (DESCRIPTION 타입에서 사용) |
| `blocks[].descriptionImageUrl` | String | N | 이미지 URL (DESCRIPTION 타입에서 사용) |
| `blocks[].cautionContent` | String | N | 주의사항 (CAUTION 타입에서 사용) |

**Response**: `200 OK` (body 없음)

---

### 5. 프로그램 이름 변경

```
PATCH /admin/programs/{programId}/name
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Request Body**

```json
{
  "name": "새 프로그램명"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | Y | 새 프로그램명 (공백 불가) |

**Response**: `200 OK` (body 없음)

---

### 6. 프로그램 삭제

프로그램과 관련된 모든 데이터(태그, 블록 등)를 삭제합니다.

```
DELETE /admin/programs/{programId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response**: `200 OK` (body 없음)

---

### 7. 프로그램 노출/미노출 토글

프로그램의 `isActive` 상태를 반전시킵니다.

```
PATCH /admin/programs/{programId}/active
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response**: `200 OK` (body 없음)

---

### 8. 프로그램 노출 일정 조회

```
GET /admin/programs/{programId}/active-info
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "programId": 1,
  "active": true,
  "startDate": "2026-02-01",
  "startTime": "10:00",
  "endDate": "2026-02-28",
  "endTime": "23:59"
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |
| `active` | boolean | 현재 노출 여부 |
| `startDate` | String | 노출 시작 날짜 (`yyyy-MM-dd`) |
| `startTime` | String | 노출 시작 시간 (`HH:mm`) |
| `endDate` | String | 노출 종료 날짜 (`yyyy-MM-dd`) |
| `endTime` | String | 노출 종료 시간 (`HH:mm`) |

---

### 9. 프로그램 노출 일정 설정

프로그램이 사용자에게 노출되는 시작/종료 일시를 설정합니다. 종료 일시는 시작 일시보다 이후여야 합니다.

```
POST /admin/programs/{programId}/active-info
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Request Body**

```json
{
  "activeStartDate": "2026-02-01",
  "activeStartTime": "10:00",
  "activeEndDate": "2026-02-28",
  "activeEndTime": "23:59"
}
```

| 필드 | 타입 | 필수 | 형식 | 설명 |
|---|---|---|---|---|
| `activeStartDate` | String | Y | `yyyy-MM-dd` | 노출 시작 날짜 |
| `activeStartTime` | String | Y | `HH:mm` | 노출 시작 시간 |
| `activeEndDate` | String | Y | `yyyy-MM-dd` | 노출 종료 날짜 |
| `activeEndTime` | String | Y | `HH:mm` | 노출 종료 시간 |

**Response**: `200 OK` (body 없음)

---

### 10. 예약 오픈/마감 일정 조회

```
GET /admin/programs/{programId}/booking
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "programId": 1,
  "openDate": "2026-02-01",
  "openTime": "10:00",
  "closeDate": "2026-02-28",
  "closeTime": "10:00"
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |
| `openDate` | String | 예약 오픈 날짜 (`yyyy-MM-dd`) |
| `openTime` | String | 예약 오픈 시간 (`HH:mm`) |
| `closeDate` | String | 예약 마감 날짜 (`yyyy-MM-dd`) |
| `closeTime` | String | 예약 마감 시간 (`HH:mm`) |

---

### 11. 예약 오픈/마감 일정 설정

예약 접수 기간을 설정합니다. 마감 일시는 오픈 일시보다 이후여야 합니다.

```
POST /admin/programs/{programId}/booking
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Request Body**

```json
{
  "bookingOpenDate": "2026-02-01",
  "bookingOpenTime": "10:00",
  "bookingCloseDate": "2026-02-28",
  "bookingCloseTime": "10:00"
}
```

| 필드 | 타입 | 필수 | 형식 | 설명 |
|---|---|---|---|---|
| `bookingOpenDate` | String | Y | `yyyy-MM-dd` | 예약 오픈 날짜 |
| `bookingOpenTime` | String | Y | `HH:mm` | 예약 오픈 시간 |
| `bookingCloseDate` | String | Y | `yyyy-MM-dd` | 예약 마감 날짜 |
| `bookingCloseTime` | String | Y | `HH:mm` | 예약 마감 시간 |

**Response**: `200 OK` (body 없음)

---

### 12. 공통 템플릿 목록 조회

축제에 등록된 공통 템플릿을 `sortOrder` 순으로 조회합니다. 행사 대표 썸네일도 함께 반환합니다.

```
GET /admin/programs/templates?festivalId={festivalId}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `festivalId` | Long | Y | 축제 ID |

**Response Body**

```json
{
  "thumbnail": "https://example.com/festival-thumb.jpg",
  "templates": [
    {
      "id": 1,
      "sortOrder": 0,
      "title": "환불 규정",
      "content": "체험 3일 전 100% 환불 가능"
    },
    {
      "id": 2,
      "sortOrder": 1,
      "title": "준비물 안내",
      "content": "편한 복장으로 오세요."
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `thumbnail` | String | 행사 대표 썸네일 URL (없으면 `null`) |
| `templates[]` | Array | 공통 템플릿 목록 |
| `templates[].id` | Long | 템플릿 ID |
| `templates[].sortOrder` | int | 정렬 순서 |
| `templates[].title` | String | 템플릿 제목 |
| `templates[].content` | String | 템플릿 내용 |

---

### 13. 공통 템플릿 전체 저장

기존 템플릿을 모두 삭제하고 요청 배열 순서대로 새로 저장합니다. 배열의 인덱스가 `sortOrder`가 됩니다.

```
PUT /admin/programs/templates?festivalId={festivalId}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `festivalId` | Long | Y | 축제 ID |

**Request Body**

```json
{
  "templates": [
    {
      "title": "환불 규정",
      "content": "체험 3일 전 100% 환불 가능"
    },
    {
      "title": "준비물 안내",
      "content": "편한 복장으로 오세요."
    }
  ]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `templates` | Array | Y | 템플릿 목록 |
| `templates[].title` | String | Y | 템플릿 제목 (공백 불가) |
| `templates[].content` | String | Y | 템플릿 내용 (공백 불가) |

**Response**: `200 OK` (body 없음)

---

### 14. 공통 템플릿 삭제

```
DELETE /admin/programs/templates/{templateId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |

**Response**: `200 OK` (body 없음)

---

### 15. 행사 대표 썸네일 저장

축제의 행사 대표 썸네일 이미지를 저장합니다.

```
POST /admin/programs/{festivalId}/thumbnail
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `festivalId` | Long | 축제 ID |

**Request Body**

```json
{
  "thumbnail": "https://example.com/festival-thumb.jpg"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `thumbnail` | String | Y | 썸네일 이미지 URL |

**Response**: `200 OK` (body 없음)

---

### 17. 태그 목록 조회

드롭다운 선택용 태그 목록을 조회합니다.

```
GET /admin/programs/tags
```

**Response Body**

```json
{
  "tags": [
    {
      "id": 1,
      "name": "체험"
    },
    {
      "id": 2,
      "name": "공연"
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `tags[]` | Array | 태그 목록 |
| `tags[].id` | Long | 태그 ID |
| `tags[].name` | String | 태그명 |

---

### 18. 태그 상세 조회

태그 ID로 태그 상세 정보를 조회합니다.

```
GET /admin/programs/tags/{tagId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `tagId` | Long | 태그 ID |

**Response Body**

```json
{
  "name": "체험",
  "bgColorHex": "#FFEEEE",
  "mainColorHex": "#FF5555",
  "iconUrl": "https://example.com/icon.png"
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `name` | String | 태그명 |
| `bgColorHex` | String | 배경색 HEX 코드 |
| `mainColorHex` | String | 메인색 HEX 코드 |
| `iconUrl` | String | 아이콘 이미지 URL |

---

## 예약 회차 관리 API

### 1. 예약 목록 조회 (대시보드)

예약 관리 대시보드용 예약 목록을 페이징 조회합니다. 상태 필터와 검색 기능을 지원합니다.

- 기본 페이지 크기: 8건
- 기본 정렬: `createdAt` 내림차순
- 전화번호 검색 시 하이픈(`-`) 유무 모두 검색됩니다.

```
GET /admin/reservations?searchField={searchField}&keyword={keyword}&status={status}&page={page}&size={size}&sort={sort}
```

**Query Parameters**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| `searchField` | String | N | - | 검색 대상: `PROGRAM_NAME`, `BOOKER_NAME`, `BOOKER_PHONE`, `VISITOR_NAME`, `VISITOR_PHONE` |
| `keyword` | String | N | - | 검색어 |
| `status` | String | N | - | 상태 필터: `CONFIRMED`, `CANCELED` (HOLD, EXPIRED는 조회되지 않음) |
| `page` | int | N | `0` | 페이지 번호 (0-based) |
| `size` | int | N | `8` | 페이지 크기 |
| `sort` | String | N | `createdAt,desc` | 정렬 기준 |

**Response Body**

```json
{
  "adminReservations": [
    {
      "id": 1,
      "programName": "도자기 체험",
      "bookerName": "홍길동",
      "bookerPhone": "010-1234-5678",
      "visitorName": "홍길동",
      "visitorPhone": "010-1234-5678",
      "slotDate": "2026-03-15",
      "slotStartTime": "14:00",
      "durationMinutes": 120,
      "peopleCount": 2,
      "fee": 0,
      "status": "APPROVED",
      "past": false
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 8,
    "totalElements": 25,
    "totalPages": 4,
    "first": true,
    "last": false
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `adminReservations[]` | Array | 예약 목록 |
| `adminReservations[].id` | Long | 예약 ID |
| `adminReservations[].programName` | String | 프로그램명 |
| `adminReservations[].bookerName` | String | 예약자 이름 |
| `adminReservations[].bookerPhone` | String | 예약자 연락처 |
| `adminReservations[].visitorName` | String | 방문자 이름 (없으면 예약자 정보로 대체) |
| `adminReservations[].visitorPhone` | String | 방문자 연락처 (없으면 예약자 정보로 대체) |
| `adminReservations[].slotDate` | String | 예약 날짜 (`yyyy-MM-dd`) |
| `adminReservations[].slotStartTime` | String | 시작 시간 (`HH:mm`) |
| `adminReservations[].durationMinutes` | int | 소요 시간 (분) |
| `adminReservations[].peopleCount` | int | 예약 인원 |
| `adminReservations[].fee` | int | 이용료 (현재 0 고정) |
| `adminReservations[].status` | String | 예약 상태 (`CONFIRMED`, `CANCELED`) |
| `adminReservations[].past` | boolean | 지난 예약 여부 (슬롯 일시가 현재 시간 이전이면 `true`) |
| `pageInfo` | Object | 페이징 정보 |
| `pageInfo.page` | int | 현재 페이지 (0-based) |
| `pageInfo.size` | int | 페이지 크기 |
| `pageInfo.totalElements` | long | 전체 예약 수 |
| `pageInfo.totalPages` | int | 전체 페이지 수 |
| `pageInfo.first` | boolean | 첫 페이지 여부 |
| `pageInfo.last` | boolean | 마지막 페이지 여부 |

---

### 2. 선택 예약 취소

관리자가 선택한 예약들을 일괄 취소합니다.

**취소 정책:**
- `CONFIRMED` 상태의 예약만 취소 가능
- `CANCELED` 상태는 멱등하게 처리 (이미 취소된 경우 성공으로 간주)
- `HOLD`, `EXPIRED` 상태는 취소 불가 (예외 발생 시 전체 롤백)
- 취소 시 슬롯 정원이 복구됩니다

```
POST /admin/reservations/cancel
```

**Request Body**

```json
{
  "reservationIds": [1, 2, 3]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `reservationIds` | Array | Y | 취소할 예약 ID 목록 |

**Response Body**

```json
{
  "canceledIds": [1, 2, 3]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `canceledIds` | Array | 취소 처리된 예약 ID 목록 |

**에러 응답**

| 상황 | 에러 코드 |
|---|---|
| 예약을 찾을 수 없음 | `ENTITY_NOT_FOUND` |
| 취소 불가 상태 (HOLD, EXPIRED) | `INVALID_INPUT_VALUE` |

---

### 3. 예약 회차 캘린더 조회

관리자 예약 회차 관리 화면에서 사용하는 날짜별 슬롯 목록을 조회합니다.

- DATE 타입 슬롯: `startTime`, `capacity`가 `null`로 내려옵니다 (정원 무제한).
- TIME 타입 슬롯: 패턴의 `capacity`를 사용합니다.

```
GET /admin/reservations/programs/{programId}/slots/calendar
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
{
  "rangeStartDate": "2026-03-01",
  "rangeEndDate": "2026-03-31",
  "dates": ["2026-03-01", "2026-03-02", "2026-03-03"],
  "days": [
    {
      "date": "2026-03-01",
      "slots": [
        {
          "slotId": 1,
          "slotType": "TIME",
          "slotDate": "2026-03-01",
          "startTime": "10:00",
          "durationMinutes": 60,
          "capacity": 20
        },
        {
          "slotId": 2,
          "slotType": "TIME",
          "slotDate": "2026-03-01",
          "startTime": "14:00",
          "durationMinutes": 60,
          "capacity": 20
        }
      ]
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `rangeStartDate` | String | 전체 범위 시작일 (`yyyy-MM-dd`) |
| `rangeEndDate` | String | 전체 범위 종료일 (`yyyy-MM-dd`) |
| `dates` | Array | 슬롯이 존재하는 날짜 목록 |
| `days[]` | Array | 날짜별 슬롯 그룹 |
| `days[].date` | String | 날짜 (`yyyy-MM-dd`) |
| `days[].slots[]` | Array | 해당 날짜의 슬롯 목록 |
| `days[].slots[].slotId` | Long | 슬롯 ID |
| `days[].slots[].slotType` | String | `DATE` / `TIME` |
| `days[].slots[].slotDate` | String | 슬롯 날짜 (`yyyy-MM-dd`) |
| `days[].slots[].startTime` | String \| null | 시작 시간 (`HH:mm`). DATE 타입이면 `null` |
| `days[].slots[].durationMinutes` | Integer \| null | 소요 시간 (분) |
| `days[].slots[].capacity` | Integer \| null | 정원. DATE 타입이면 `null` (무제한) |

---

### 4. 기간 카드(스케줄 템플릿) 목록 조회

특정 프로그램에 등록된 스케줄 템플릿 카드 리스트를 조회합니다.

```
GET /admin/reservations/programs/{programId}/schedule-templates
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Response Body**

```json
[
  {
    "templateId": 1,
    "slotType": "TIME",
    "startDate": "2026-03-01",
    "endDate": "2026-03-31",
    "durationMinutes": null,
    "patternCount": 2,
    "patterns": [
      {
        "patternId": 1,
        "startTime": "10:00",
        "durationMinutes": 60,
        "capacity": 20
      },
      {
        "patternId": 2,
        "startTime": "14:00",
        "durationMinutes": 60,
        "capacity": 15
      }
    ]
  },
  {
    "templateId": 2,
    "slotType": "DATE",
    "startDate": "2026-04-01",
    "endDate": "2026-04-15",
    "durationMinutes": 120,
    "patternCount": 0,
    "patterns": []
  }
]
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |
| `slotType` | String | `DATE` / `TIME` |
| `startDate` | String | 시작일 (`yyyy-MM-dd`) |
| `endDate` | String | 종료일 (`yyyy-MM-dd`) |
| `durationMinutes` | Integer \| null | 소요 시간 (분). DATE 타입에서 사용, TIME 타입이면 `null` |
| `patternCount` | int | 패턴(회차) 수 |
| `patterns[]` | Array | 패턴 목록 (TIME 타입에서만 존재) |
| `patterns[].patternId` | Long | 패턴 ID |
| `patterns[].startTime` | String | 시작 시간 (`HH:mm`) |
| `patterns[].durationMinutes` | Integer | 소요 시간 (분) |
| `patterns[].capacity` | Integer | 정원 |

---

### 5. 스케줄 템플릿 상세 조회 (편집용)

편집 모달에서 사용할 템플릿 설정값 전체를 조회합니다. `hasReservation`이 `true`이면 수정 제약이 있습니다 (아래 수정 API 참고).

```
GET /admin/reservations/schedule-templates/{templateId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |

**Response Body**

```json
{
  "templateId": 1,
  "slotType": "TIME",
  "startDate": "2026-03-01",
  "endDate": "2026-03-31",
  "durationMinutes": null,
  "hasReservation": true,
  "patterns": [
    {
      "patternId": 1,
      "startTime": "10:00",
      "durationMinutes": 60,
      "capacity": 20
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |
| `slotType` | String | `DATE` / `TIME` |
| `startDate` | String | 시작일 (`yyyy-MM-dd`) |
| `endDate` | String | 종료일 (`yyyy-MM-dd`) |
| `durationMinutes` | Integer \| null | 소요 시간 (분). DATE 타입에서 사용 |
| `hasReservation` | boolean | 예약 이력 존재 여부. `true`이면 수정 제약 적용 |
| `patterns[]` | Array | 패턴 목록 |
| `patterns[].patternId` | Long | 패턴 ID |
| `patterns[].startTime` | String | 시작 시간 (`HH:mm`) |
| `patterns[].durationMinutes` | Integer | 소요 시간 (분) |
| `patterns[].capacity` | Integer | 정원 |

---

### 6. 스케줄 템플릿 생성

프로그램에 새 기간 카드(스케줄 템플릿)를 추가합니다.

**슬롯 타입별 규칙:**
- **DATE 타입**: `durationMinutes` 필수, `patterns`는 비워야 합니다. 기간 내 매일 1개의 DATE 슬롯이 생성됩니다.
- **TIME 타입**: `patterns` 필수, `durationMinutes`는 비워야 합니다. 패턴별로 기간 내 매일 TIME 슬롯이 생성됩니다.

```
POST /admin/reservations/programs/{programId}/schedule-templates
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `programId` | Long | 프로그램 ID |

**Request Body — DATE 타입 예시**

```json
{
  "slotType": "DATE",
  "startDate": "2026-04-01",
  "endDate": "2026-04-15",
  "durationMinutes": 120,
  "patterns": null
}
```

**Request Body — TIME 타입 예시**

```json
{
  "slotType": "TIME",
  "startDate": "2026-03-01",
  "endDate": "2026-03-31",
  "durationMinutes": null,
  "patterns": [
    {
      "startTime": "10:00",
      "durationMinutes": 60,
      "capacity": 20
    },
    {
      "startTime": "14:00",
      "durationMinutes": 60,
      "capacity": 15
    }
  ]
}
```

| 필드 | 타입 | 필수 | 형식 | 설명 |
|---|---|---|---|---|
| `slotType` | String | Y | - | `DATE` / `TIME` |
| `startDate` | String | Y | `yyyy-MM-dd` | 기간 시작일 |
| `endDate` | String | Y | `yyyy-MM-dd` | 기간 종료일 |
| `durationMinutes` | Integer | DATE: Y / TIME: N | - | 소요 시간 (분). DATE 타입에서만 사용 |
| `patterns` | Array | DATE: N / TIME: Y | - | 회차 패턴 목록. TIME 타입에서만 사용 |
| `patterns[].startTime` | String | Y | `HH:mm` | 회차 시작 시간 |
| `patterns[].durationMinutes` | Integer | Y | - | 회차 소요 시간 (분) |
| `patterns[].capacity` | Integer | Y | - | 회차 정원 |

**Response**: `200 OK` (body 없음)

---

### 7. 스케줄 템플릿 수정

모달에서 기간/회차/정원 변경 사항을 저장합니다.

**수정 정책:**
- **예약 없음**: 전체 교체 (기존 슬롯/패턴 삭제 후 새로 생성) → `"SUCCESS"` 응답
- **예약 존재 (HOLD/CONFIRMED)**: 제한적 수정만 허용
  - 날짜 범위, 슬롯 타입, 시작 시간 등은 변경 불가
  - **정원(capacity)만 변경 가능** (단, 새 정원 >= 현재 예약 인원)
  - 모든 패턴 성공 시 `"SUCCESS"`, 일부 실패 시 `"PARTIAL_SUCCESS"` 응답

```
PUT /admin/reservations/schedule-templates/{templateId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |

**Request Body** (ScheduleTemplateCreateRequest와 동일한 구조)

```json
{
  "slotType": "TIME",
  "startDate": "2026-03-01",
  "endDate": "2026-03-31",
  "durationMinutes": null,
  "patterns": [
    {
      "startTime": "10:00",
      "durationMinutes": 60,
      "capacity": 25
    }
  ]
}
```

**Response Body — 성공**

```json
{
  "templateId": 1,
  "result": "SUCCESS",
  "updated": null,
  "rejected": null
}
```

**Response Body — 부분 성공 (PARTIAL_SUCCESS)**

```json
{
  "templateId": 1,
  "result": "PARTIAL_SUCCESS",
  "updated": [
    {
      "patternId": 1,
      "startTime": "10:00",
      "appliedCapacity": 25,
      "updatedSlotCount": 31
    }
  ],
  "rejected": [
    {
      "patternId": 2,
      "startTime": "14:00",
      "reason": "BOOKED_EXCEEDS_NEW_CAPACITY",
      "message": "새 정원이 기존 예약 수보다 적습니다",
      "failedSlots": [
        {
          "slotDate": "2026-03-15",
          "booked": 18,
          "requestedCapacity": 15
        }
      ]
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |
| `result` | String | `"SUCCESS"` 또는 `"PARTIAL_SUCCESS"` |
| `updated` | Array \| null | 성공한 패턴 목록 (SUCCESS 시 `null`) |
| `updated[].patternId` | Long | 패턴 ID |
| `updated[].startTime` | String | 시작 시간 (`HH:mm`) |
| `updated[].appliedCapacity` | Integer | 적용된 정원 |
| `updated[].updatedSlotCount` | Integer | 업데이트된 슬롯 수 |
| `rejected` | Array \| null | 실패한 패턴 목록 (SUCCESS 시 `null`) |
| `rejected[].patternId` | Long | 패턴 ID |
| `rejected[].startTime` | String | 시작 시간 (`HH:mm`) |
| `rejected[].reason` | String | 실패 사유 코드 (예: `FIELD_CHANGE_NOT_ALLOWED`, `BOOKED_EXCEEDS_NEW_CAPACITY`) |
| `rejected[].message` | String | 실패 사유 메시지 |
| `rejected[].failedSlots` | Array \| null | 실패한 슬롯 상세 |
| `rejected[].failedSlots[].slotDate` | String | 슬롯 날짜 (`yyyy-MM-dd`) |
| `rejected[].failedSlots[].booked` | Integer | 현재 예약 인원 |
| `rejected[].failedSlots[].requestedCapacity` | Integer | 요청한 정원 |

---

### 8. 스케줄 템플릿 삭제

예약 이력이 없는 경우에만 삭제 가능합니다. 예약 이력이 존재하면 `409 Conflict`를 반환합니다.

삭제 시 템플릿에 속한 패턴과 슬롯도 함께 삭제됩니다.

```
DELETE /admin/reservations/schedule-templates/{templateId}
```

**Path Parameters**

| 이름 | 타입 | 설명 |
|---|---|---|
| `templateId` | Long | 템플릿 ID |

**Response**
- 성공: `200 OK` (body 없음)
- 예약 이력 존재: `409 Conflict`

---

## 참고: 슬롯 타입별 동작 비교

| 항목 | DATE 타입 | TIME 타입 |
|---|---|---|
| 슬롯 생성 | 기간 내 매일 1개 | 기간 내 매일 패턴 수만큼 |
| 정원 | 무제한 (`capacity` = `null`) | 패턴별 정원 설정 |
| 패턴 | 사용하지 않음 | 필수 (시작 시간, 소요 시간, 정원) |
| `durationMinutes` | 템플릿 레벨에서 설정 | 패턴 레벨에서 개별 설정 |
| 캘린더 응답 | `startTime`=`null`, `capacity`=`null` | `startTime`, `capacity` 모두 표시 |
