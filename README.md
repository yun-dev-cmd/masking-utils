# masking-utils

[![CI](https://github.com/yun-dev-cmd/masking-utils/actions/workflows/ci.yml/badge.svg)](https://github.com/yun-dev-cmd/masking-utils/actions/workflows/ci.yml)
[![JitPack](https://jitpack.io/v/yun-dev-cmd/masking-utils.svg)](https://jitpack.io/#yun-dev-cmd/masking-utils)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[English](README_ENG.md)

한국 서비스에서 자주 쓰는 개인정보를 **같은 규칙으로** 가리는 의존성 없는 Java 라이브러리입니다.

화면마다, 엑셀마다 마스킹을 따로 구현하면 어느 화면은 전화번호 가운데를, 어느 화면은 끝자리를 가리는 식으로 기준이 흩어집니다. 마스킹 규칙을 한곳에 두고 화면·엑셀·로그가 모두 같은 함수를 부르도록 만들기 위한 모듈입니다.

- **Java 8 이상** (전자정부 표준프레임워크 환경 포함), 외부 의존성 없음
- `null`·빈 문자열은 그대로 통과, 형식을 모르는 값은 예외 없이 더 많이 가림
- 종류를 값으로 다루는 `MaskType` 으로 설정 파일·컬럼 정의에서 바로 사용

## 규칙

| 메서드 | 입력 | 결과 |
| --- | --- | --- |
| `name` | 홍길동 / 남궁민수 / 이순 / John Smith | 홍\*동 / 남\*\*수 / 이\* / J\*\*\* S\*\*\*\* |
| `phone` | 010-1234-5678 / 01012345678 / 02-123-4567 | 010-\*\*\*\*-5678 / 010\*\*\*\*5678 / 02-\*\*\*-4567 |
| `birthDate` | 1968-04-17 / 19680417 / 680417 | 1968-\*\*-\*\* / 1968\*\*\*\* / 68\*\*\*\* |
| `residentNumber` | 680417-1234567 | 680417-1\*\*\*\*\*\* |
| `address` | 서울특별시 중구 세종대로 110 | 서울특별시 중구 세종대로 \*\*\* |
| `address` | 경기도 안양시 만안구 안양동 123-4 101동 202호 | 경기도 안양시 만안구 안양동 \*\*\*\*\* \*\*\*\* \*\*\*\* |
| `email` | gildong@example.com | gi\*\*\*\*\*@example.com |
| `account` | 123-456-789012 | \*\*\*-\*\*\*-\*\*9012 |
| `cardNumber` | 1234-5678-9012-3456 | 1234-\*\*\*\*-\*\*\*\*-3456 |
| `keepEdges` | ("abcdefg", 2, 2) | ab\*\*\*fg |

주소는 도로명(…로, …길) 또는 동·리·읍·면까지 보여주고 그 뒤 번지·건물·동호수를 가립니다.

## 설치

[JitPack](https://jitpack.io/#yun-dev-cmd/masking-utils) 으로 받습니다.

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.yun-dev-cmd</groupId>
  <artifactId>masking-utils</artifactId>
  <version>v0.1.0</version>
</dependency>
```

Gradle: `implementation 'com.github.yun-dev-cmd:masking-utils:v0.1.0'`

## 사용법

```java
import io.github.yundevcmd.masking.Masking;
import io.github.yundevcmd.masking.MaskType;

Masking.phone("010-1234-5678");          // 010-****-5678
Masking.address("서울특별시 중구 세종대로 110"); // 서울특별시 중구 세종대로 ***

// 설정에 저장된 종류로
MaskType type = MaskType.valueOf(column.getMaskType()); // "PHONE"
String shown = type.mask(value);
```

엑셀 출력에는 [excel-audit-export](https://github.com/yun-dev-cmd/excel-audit-export) 와 함께 씁니다.

```java
Column.of("연락처", Owner::getPhone).masked(Masking::phone)
```

## 개발

```bash
mvn test
```

JDK 11 이상으로 빌드하며, 결과물은 Java 8 바이트코드입니다.

## 라이선스

[MIT](LICENSE)
