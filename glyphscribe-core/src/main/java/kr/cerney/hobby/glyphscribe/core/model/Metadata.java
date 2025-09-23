package kr.cerney.hobby.glyphscribe.core.model;

/**
 * XML, Mapper 등에서 추출된 원본 SQL 및 주석 정보
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record Metadata(
        String bridgeId,
        String rawSql,
        String comment
) {}
