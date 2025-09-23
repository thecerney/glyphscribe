package kr.cerney.hobby.glyphscribe.core.util;

import java.util.UUID;

/**
 * 로그 실행 단위에 대한 고유 키를 생성하는 유틸리티 클래스
 * - UUID 기반 무작위 키
 * - 해시 기반 deterministic 키
 * - 복수 파라미터 조합 키
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class LogKeyGenerator {
    private static final String PREFIX = "glyph-";

    private LogKeyGenerator() {
        // 유틸 클래스, 인스턴스 생성 방지
    }

    /**
     * 무작위 UUID 기반 키 생성
     */
    public static String generate() {
        return PREFIX + UUID.randomUUID();
    }

    /**
     * bridgeId 기반 해시 키 생성
     * - 동일 bridgeId → 동일 key (예측 가능)
     */
    public static String fromBridgeId(String bridgeId) {
        if (bridgeId == null || bridgeId.isBlank()) {
            throw new IllegalArgumentException("bridgeId cannot be null or blank");
        }
        return PREFIX + Integer.toHexString(bridgeId.hashCode());
    }

    /**
     * 여러 문자열 조합 기반 해시 키 생성
     * - 예: bridgeId + timestamp 등
     */
    public static String from(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part == null ? "null" : part).append("::");
        }
        return PREFIX + Integer.toHexString(sb.toString().hashCode());
    }
}
