package kr.cerney.hobby.glyphscribe.core.model;

/**
 * SQL 실행 로그의 전체 컨텍스트 정보
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record LogContext(
        String key,
        Metadata metadata,
        AssemblyState assembly,
        ExecutionSnapshot execution,
        ResultInfo result
) {}
