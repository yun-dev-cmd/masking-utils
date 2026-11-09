package io.github.yundevcmd.masking;

/**
 * 마스킹 종류. 엑셀 컬럼 정의나 설정 파일처럼 "어떤 값을 어떻게 가릴지"를 데이터로 들고 다닐 때 쓴다.
 *
 * <pre>{@code
 * MaskType type = MaskType.valueOf("PHONE");
 * type.mask("010-1234-5678"); // 010-****-5678
 * }</pre>
 */
public enum MaskType {
    NAME {
        @Override
        public String mask(String value) {
            return Masking.name(value);
        }
    },
    PHONE {
        @Override
        public String mask(String value) {
            return Masking.phone(value);
        }
    },
    BIRTH_DATE {
        @Override
        public String mask(String value) {
            return Masking.birthDate(value);
        }
    },
    RESIDENT_NUMBER {
        @Override
        public String mask(String value) {
            return Masking.residentNumber(value);
        }
    },
    ADDRESS {
        @Override
        public String mask(String value) {
            return Masking.address(value);
        }
    },
    EMAIL {
        @Override
        public String mask(String value) {
            return Masking.email(value);
        }
    },
    ACCOUNT {
        @Override
        public String mask(String value) {
            return Masking.account(value);
        }
    },
    CARD_NUMBER {
        @Override
        public String mask(String value) {
            return Masking.cardNumber(value);
        }
    },
    /** 가리지 않음. 설정에서 "마스킹 없음"을 명시할 때 쓴다. */
    NONE {
        @Override
        public String mask(String value) {
            return value;
        }
    };

    public abstract String mask(String value);
}
