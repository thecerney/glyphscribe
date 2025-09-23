package kr.cerney.hobby.glyphscribe.core.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 실행 전의 원본 SQL과 주석(comment)을 저장하는 레지스트리
 * <p>
 * key: core에서 생성된 고유 식별자 (예: UUID)
 * value: raw SQL 및 주석을 담은 SqlSource 객체
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class RawSqlRegistry {
    private static final Map<String, SqlSource> STORE = new ConcurrentHashMap<>();

    private RawSqlRegistry() {
    }

    /**
     * 원본 SQL 및 주석 등록
     */
    public static void put(String key, String rawSql, String comment) {
        if (key != null) {
            STORE.put(key, new SqlSource(rawSql, comment));
        }
    }

    /**
     * 원본 SQL 조회
     */
    public static Optional<String> getRawSql(String key) {
        return Optional.ofNullable(STORE.get(key)).map(SqlSource::rawSql);
    }

    /**
     * 주석(comment) 조회
     */
    public static Optional<String> getComment(String key) {
        return Optional.ofNullable(STORE.get(key)).map(SqlSource::comment);
    }

    /**
     * 등록 여부 확인
     */
    public static boolean contains(String key) {
        return STORE.containsKey(key);
    }

    /**
     * 모든 저장소 초기화 (테스트 등)
     */
    public static void clear() {
        STORE.clear();
    }

    /**
     * 내부 저장된 SQL 및 주석 구조체
     */
    public record SqlSource(
            String rawSql,
            String comment
    ) {}
}
