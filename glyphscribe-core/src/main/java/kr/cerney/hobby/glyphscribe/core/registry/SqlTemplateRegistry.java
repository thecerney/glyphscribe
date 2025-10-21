package kr.cerney.hobby.glyphscribe.core.registry;

import java.util.Optional;

/**
 * 실행 전(스캔 시점) 수집한 SQL 원문/주석을 bridge-id로 보관하는 레지스트리.
 * 실행 시점에는 여기서 읽어 실행 로그 조립에 활용한다.
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public interface SqlTemplateRegistry {

    /**
     * 템플릿 등록 (bridge-id 기준)
     */
    void put(String bridgeId, String rawSql, String comment);

    /**
     * 템플릿 조회
     */
    Optional<SqlTemplate> get(String bridgeId);

    /**
     * 보유 여부
     */
    boolean contains(String bridgeId);

    /**
     * 전체 비우기 (주로 테스트/리로드용)
     */
    void clear();

    /**
     * 저장되는 템플릿 구조
     */
    record SqlTemplate(
            String rawSql,
            String comment
    ) {}
}