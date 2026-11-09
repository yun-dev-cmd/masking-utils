package io.github.yundevcmd.masking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 개인정보 마스킹 함수 모음.
 *
 * <p>모든 메서드는 {@code null} 이나 공백 문자열을 받으면 그대로 돌려주고, 형식을 알 수 없는 값은
 * 예외를 던지지 않고 보수적으로(더 많이) 가린다. 화면, 엑셀, 로그 어디에서 호출해도 출력이 끊기지 않게 하기 위해서다.
 *
 * <pre>{@code
 * Masking.name("홍길동");              // 홍*동
 * Masking.phone("010-1234-5678");     // 010-****-5678
 * Masking.birthDate("1968-04-17");    // 1968-**-**
 * Masking.residentNumber("680417-1234567"); // 680417-1******
 * Masking.address("서울특별시 중구 세종대로 110"); // 서울특별시 중구 세종대로 ***
 * }</pre>
 */
public final class Masking {

    /** 가림 문자 */
    public static final char MASK = '*';

    private static final Pattern NON_DIGIT = Pattern.compile("\\D");
    private static final Pattern PHONE_GROUPED = Pattern.compile("^(\\d{2,4})([-.\\s])(\\d{3,4})([-.\\s])(\\d{4})$");
    private static final Pattern RRN = Pattern.compile("^(\\d{6})[-\\s]?(\\d)(\\d{6})$");
    private static final Pattern DATE_SEPARATED = Pattern.compile("^(\\d{4})([-./])(\\d{1,2})([-./])(\\d{1,2})$");
    private static final Pattern EMAIL = Pattern.compile("^([^@\\s]+)@([^@\\s]+)$");
    /** 주소에서 이 단위로 끝나는 마지막 토큰까지 보여주고, 그 뒤(번지·동호수)를 가린다 */
    private static final Pattern ADDRESS_UNIT = Pattern.compile(".+(로|길|동|리|가|읍|면)(\\d+가)?$");

    private Masking() {
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(Math.max(n, 0));
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /** 숫자만 가리고 구분자(-, 공백 등)는 남긴다. */
    private static String maskEachDigit(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            sb.append(Character.isDigit(c) ? MASK : c);
        }
        return sb.toString();
    }

    /**
     * 앞 {@code head} 글자와 뒤 {@code tail} 글자만 남기고 가린다. 남길 글자가 전체보다 많으면 마지막 한 글자만 가린다.
     */
    public static String keepEdges(String value, int head, int tail) {
        if (blank(value)) {
            return value;
        }
        int[] cps = value.codePoints().toArray();
        int len = cps.length;
        if (head + tail >= len) {
            head = Math.max(0, Math.min(head, len - 1));
            tail = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i < head || i >= len - tail) {
                sb.appendCodePoint(cps[i]);
            } else {
                sb.append(MASK);
            }
        }
        return sb.toString();
    }

    /**
     * 이름. 한글 이름은 첫 글자와 끝 글자만 남긴다 (두 글자면 끝 글자를 가린다).
     * 공백으로 나뉜 영문 이름은 단어마다 첫 글자만 남긴다.
     * <p>홍길동 → 홍*동, 남궁민수 → 남**수, 이순 → 이*, John Smith → J*** S****
     */
    public static String name(String value) {
        if (blank(value)) {
            return value;
        }
        String trimmed = value.trim();
        if (trimmed.contains(" ")) {
            StringBuilder sb = new StringBuilder();
            for (String word : trimmed.split("\\s+")) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(keepEdges(word, 1, 0));
            }
            return sb.toString();
        }
        int len = trimmed.codePointCount(0, trimmed.length());
        if (len == 1) {
            return trimmed; // 한 글자는 가려도 의미가 없어 그대로 둔다
        }
        return len <= 2 ? keepEdges(trimmed, 1, 0) : keepEdges(trimmed, 1, 1);
    }

    /**
     * 전화번호. 가운데 자리를 가린다. 구분자가 없으면 길이로 자리를 나눈다.
     * <p>010-1234-5678 → 010-****-5678, 01012345678 → 010****5678, 02-123-4567 → 02-***-4567
     */
    public static String phone(String value) {
        if (blank(value)) {
            return value;
        }
        String v = value.trim();
        Matcher m = PHONE_GROUPED.matcher(v);
        if (m.matches()) {
            return m.group(1) + m.group(2) + repeat(MASK, m.group(3).length()) + m.group(4) + m.group(5);
        }
        String digits = NON_DIGIT.matcher(v).replaceAll("");
        if (digits.equals(v) && digits.length() >= 9 && digits.length() <= 11) {
            int head = digits.startsWith("02") ? 2 : 3;
            int middle = digits.length() - head - 4;
            return digits.substring(0, head) + repeat(MASK, middle) + digits.substring(head + middle);
        }
        return keepEdges(v, 0, 4);
    }

    /**
     * 생년월일. 연도만 남긴다.
     * <p>1968-04-17 → 1968-**-**, 19680417 → 1968****, 680417 → 68****
     */
    public static String birthDate(String value) {
        if (blank(value)) {
            return value;
        }
        String v = value.trim();
        Matcher m = DATE_SEPARATED.matcher(v);
        if (m.matches()) {
            return m.group(1) + m.group(2) + repeat(MASK, m.group(3).length()) + m.group(4) + repeat(MASK, m.group(5).length());
        }
        if (v.matches("\\d{8}")) {
            return v.substring(0, 4) + repeat(MASK, 4);
        }
        if (v.matches("\\d{6}")) {
            return v.substring(0, 2) + repeat(MASK, 4);
        }
        return maskEachDigit(v);
    }

    /**
     * 주민등록번호·외국인등록번호. 생년월일과 성별 자리만 남긴다.
     * <p>680417-1234567 → 680417-1******
     */
    public static String residentNumber(String value) {
        if (blank(value)) {
            return value;
        }
        String v = value.trim();
        Matcher m = RRN.matcher(v);
        if (m.matches()) {
            String sep = v.length() == 14 ? String.valueOf(v.charAt(6)) : "";
            return m.group(1) + sep + m.group(2) + repeat(MASK, 6);
        }
        return maskEachDigit(v);
    }

    /**
     * 주소. 도로명(…로/…길) 또는 동·리까지 보여주고 그 뒤 번지·건물·동호수를 가린다.
     * 단위를 찾지 못하면 마지막 단어를 가린다.
     * <p>서울특별시 중구 세종대로 110 → 서울특별시 중구 세종대로 ***
     * <p>경기도 안양시 만안구 안양동 123-4 101동 202호 → 경기도 안양시 만안구 안양동 ***** **** ****
     */
    public static String address(String value) {
        if (blank(value)) {
            return value;
        }
        String[] tokens = value.trim().split("\\s+");
        if (tokens.length == 1) {
            return keepEdges(tokens[0], 2, 0);
        }
        int lastUnit = -1;
        for (int i = 0; i < tokens.length; i++) {
            if (ADDRESS_UNIT.matcher(tokens[i]).matches() && !tokens[i].matches("\\d.*")) {
                lastUnit = i;
            }
        }
        int maskFrom = lastUnit >= 0 ? lastUnit + 1 : tokens.length - 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(i >= maskFrom ? repeat(MASK, tokens[i].codePointCount(0, tokens[i].length())) : tokens[i]);
        }
        return sb.toString();
    }

    /**
     * 이메일. 아이디 앞 두 글자만 남긴다 (세 글자 이하면 첫 글자만).
     * <p>gildong@example.com → gi*****@example.com
     */
    public static String email(String value) {
        if (blank(value)) {
            return value;
        }
        Matcher m = EMAIL.matcher(value.trim());
        if (!m.matches()) {
            return keepEdges(value.trim(), 1, 0);
        }
        String id = m.group(1);
        int keep = id.length() <= 3 ? 1 : 2;
        return keepEdges(id, keep, 0) + "@" + m.group(2);
    }

    /**
     * 계좌번호. 끝 네 자리 숫자만 남기고, 구분자는 유지한다.
     * <p>123-456-789012 → ***-***-**9012
     */
    public static String account(String value) {
        if (blank(value)) {
            return value;
        }
        return keepLastDigits(value.trim(), 4);
    }

    /**
     * 카드번호. 앞 네 자리와 끝 네 자리 숫자만 남긴다.
     * <p>1234-5678-9012-3456 → 1234-****-****-3456
     */
    public static String cardNumber(String value) {
        if (blank(value)) {
            return value;
        }
        String v = value.trim();
        int totalDigits = NON_DIGIT.matcher(v).replaceAll("").length();
        StringBuilder sb = new StringBuilder(v.length());
        int seen = 0;
        for (char c : v.toCharArray()) {
            if (Character.isDigit(c)) {
                seen++;
                sb.append(seen <= 4 || seen > totalDigits - 4 ? c : MASK);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String keepLastDigits(String v, int keep) {
        int totalDigits = NON_DIGIT.matcher(v).replaceAll("").length();
        StringBuilder sb = new StringBuilder(v.length());
        int seen = 0;
        for (char c : v.toCharArray()) {
            if (Character.isDigit(c)) {
                seen++;
                sb.append(seen > totalDigits - keep ? c : MASK);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** 종류를 값으로 받아 마스킹한다. 설정 파일이나 컬럼 정의에서 종류를 고를 때 쓴다. */
    public static String apply(MaskType type, String value) {
        if (type == null) {
            return value;
        }
        return type.mask(value);
    }
}
