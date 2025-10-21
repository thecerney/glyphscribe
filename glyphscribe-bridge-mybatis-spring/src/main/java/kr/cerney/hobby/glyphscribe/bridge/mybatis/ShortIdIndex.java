package kr.cerney.hobby.glyphscribe.bridge.mybatis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis xml 파일에 기록하는 id (NOT full package ID) 참조를 위한 클래스
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class ShortIdIndex {
    private final Map<String, Set<String>> index = new ConcurrentHashMap<>();

    public void add(String fullId) {
        String shortId = fullId.substring(fullId.lastIndexOf('.') + 1);
        index.computeIfAbsent(shortId, k -> ConcurrentHashMap.newKeySet()).add(fullId);
    }

    /**
     * id가 full이면 그대로, 짧으면 후보 fullId들을 돌려줌
     */
    public Set<String> resolve(String id) {
        if (id == null || id.isBlank()) {
            return Set.of();
        }
        if (id.indexOf('.') >= 0) {
            return Set.of(id);
        }
        return index.getOrDefault(id, Set.of());
    }

    public void clear() {
        index.clear();
    }
}