# masking-utils

[![CI](https://github.com/yun-dev-cmd/masking-utils/actions/workflows/ci.yml/badge.svg)](https://github.com/yun-dev-cmd/masking-utils/actions/workflows/ci.yml)
[![JitPack](https://jitpack.io/v/yun-dev-cmd/masking-utils.svg)](https://jitpack.io/#yun-dev-cmd/masking-utils)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[한국어](README.md)

A dependency-free Java library that masks personal data commonly found in Korean services **with one consistent set of rules**.

When each screen and each Excel export implements its own masking, rules drift: one page hides the middle of a phone number, another hides the end. This module keeps the rules in one place so screens, exports and logs all call the same function.

- **Java 8+**, no dependencies
- `null` and blank strings pass through; unknown formats are masked more, never thrown on
- `MaskType` lets you pick the rule from configuration or column metadata

## Rules

| Method | Input | Output |
| --- | --- | --- |
| `name` | 홍길동 / 남궁민수 / John Smith | 홍\*동 / 남\*\*수 / J\*\*\* S\*\*\*\* |
| `phone` | 010-1234-5678 / 01012345678 | 010-\*\*\*\*-5678 / 010\*\*\*\*5678 |
| `birthDate` | 1968-04-17 / 19680417 | 1968-\*\*-\*\* / 1968\*\*\*\* |
| `residentNumber` | 680417-1234567 | 680417-1\*\*\*\*\*\* |
| `address` | 서울특별시 중구 세종대로 110 | 서울특별시 중구 세종대로 \*\*\* |
| `email` | gildong@example.com | gi\*\*\*\*\*@example.com |
| `account` | 123-456-789012 | \*\*\*-\*\*\*-\*\*9012 |
| `cardNumber` | 1234-5678-9012-3456 | 1234-\*\*\*\*-\*\*\*\*-3456 |
| `keepEdges` | ("abcdefg", 2, 2) | ab\*\*\*fg |

Addresses keep everything up to the road name (…로, …길) or the dong/ri/eup/myeon, and mask the building number and unit after it.

## Install

Via [JitPack](https://jitpack.io/#yun-dev-cmd/masking-utils):

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

## Usage

```java
Masking.phone("010-1234-5678");          // 010-****-5678
MaskType.valueOf("PHONE").mask(value);   // rule chosen by configuration
```

Pairs with [excel-audit-export](https://github.com/yun-dev-cmd/excel-audit-export):

```java
Column.of("Phone", Owner::getPhone).masked(Masking::phone)
```

## Development

```bash
mvn test
```

Build with JDK 11+; the output is Java 8 bytecode.

## License

[MIT](LICENSE)
