package kr.cerney.hobby.glyphscribe.core.model;

import java.util.List;

/**
 * 실행 후 생성된 키나 결과 정보
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record ResultInfo(Object result) {
    @SuppressWarnings("unchecked")
    public List<Object> asList() {
        return (result instanceof List<?> l) ? (List<Object>) l : List.of(result);
    }

    public Integer asUpdateCountOrNull() {
        return (result instanceof Number n) ? n.intValue() : null;
    }

    public boolean isEmpty() {
        return result == null || (result instanceof List<?> l && l.isEmpty());
    }
}
