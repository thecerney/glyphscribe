package kr.cerney.hobby.glyphscribe.core.model;

/**
 * 바인딩 전 동적 쿼리 상태 (예: ? 포함된 SQL)
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record AssemblyState(String assembledSql) {}