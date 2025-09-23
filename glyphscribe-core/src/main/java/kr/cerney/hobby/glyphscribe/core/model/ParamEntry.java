package kr.cerney.hobby.glyphscribe.core.model;

/**
 * 실행 파라미터 하나 (값 + 타입)
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record ParamEntry(
        Object value,
        String type
) {}
