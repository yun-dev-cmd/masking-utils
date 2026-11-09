package io.github.yundevcmd.masking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class MaskingTest {

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "홍길동, 홍*동",
            "남궁민수, 남**수",
            "이순, 이*",
            "김, 김",
            "'John Smith', 'J*** S****'",
            "'  홍길동  ', 홍*동",
    })
    void name(String input, String expected) {
        assertEquals(expected, Masking.name(input));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "010-1234-5678, 010-****-5678",
            "010-123-4567, 010-***-4567",
            "02-123-4567, 02-***-4567",
            "031.234.5678, 031.***.5678",
            "01012345678, 010****5678",
            "0212345678, 02****5678",
            "+82 10 1234 5678, ************5678",
    })
    void phone(String input, String expected) {
        assertEquals(expected, Masking.phone(input));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "1968-04-17, 1968-**-**",
            "1968.4.7, 1968.*.*",
            "19680417, 1968****",
            "680417, 68****",
    })
    void birthDate(String input, String expected) {
        assertEquals(expected, Masking.birthDate(input));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "680417-1234567, 680417-1******",
            "6804171234567, 6804171******",
            "680417-12, ******-**",
    })
    void residentNumber(String input, String expected) {
        assertEquals(expected, Masking.residentNumber(input));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "'서울특별시 중구 세종대로 110', '서울특별시 중구 세종대로 ***'",
            "'경기도 안양시 만안구 안양동 123-4 101동 202호', '경기도 안양시 만안구 안양동 ***** **** ****'",
            "'전라남도 무안군 삼향읍 남악리 1000', '전라남도 무안군 삼향읍 남악리 ****'",
            "'서울 종로구 종로1가 1', '서울 종로구 종로1가 *'",
            "'Some Unknown Place 42', 'Some Unknown Place **'",
            "'세종', '세*'",
            "'세종시청', '세종**'",
    })
    void address(String input, String expected) {
        assertEquals(expected, Masking.address(input));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "gildong@example.com, gi*****@example.com",
            "abc@example.com, a**@example.com",
            "not-an-email, n***********",
    })
    void email(String input, String expected) {
        assertEquals(expected, Masking.email(input));
    }

    @Test
    void accountAndCard() {
        assertEquals("***-***-**9012", Masking.account("123-456-789012"));
        assertEquals("1234-****-****-3456", Masking.cardNumber("1234-5678-9012-3456"));
        assertEquals("1234********3456", Masking.cardNumber("1234567890123456"));
    }

    @Test
    void keepEdges() {
        assertEquals("ab***fg", Masking.keepEdges("abcdefg", 2, 2));
        assertEquals("a*", Masking.keepEdges("ab", 2, 2));
        assertEquals("😀*", Masking.keepEdges("😀😀", 1, 0));
    }

    @ParameterizedTest
    @EnumSource(MaskType.class)
    void nullAndBlankArePassedThrough(MaskType type) {
        assertNull(type.mask(null));
        assertEquals("", type.mask(""));
        assertEquals("  ", type.mask("  "));
    }

    @Test
    void applyByType() {
        assertEquals("010-****-5678", Masking.apply(MaskType.PHONE, "010-1234-5678"));
        assertEquals("010-1234-5678", Masking.apply(MaskType.NONE, "010-1234-5678"));
        assertEquals("010-1234-5678", Masking.apply(null, "010-1234-5678"));
    }
}
