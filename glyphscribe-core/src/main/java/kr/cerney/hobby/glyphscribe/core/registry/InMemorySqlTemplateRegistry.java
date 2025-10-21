package kr.cerney.hobby.glyphscribe.core.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap 기반 인메모리 구현.
 * - thread-safe put/get
 * - bridge-id 충돌 시 마지막 값으로 덮어씀
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class InMemorySqlTemplateRegistry implements SqlTemplateRegistry {
    private final Map<String, SqlTemplate> store = new ConcurrentHashMap<>();

    @Override
    public void put(String bridgeId, String rawSql, String comment) {
        if (bridgeId == null) return;
        store.put(bridgeId, new SqlTemplate(rawSql, comment));
    }

    @Override
    public Optional<SqlTemplate> get(String bridgeId) {
        if (bridgeId == null) return Optional.empty();
        return Optional.ofNullable(store.get(bridgeId));
    }

    @Override
    public boolean contains(String bridgeId) {
        return bridgeId != null && store.containsKey(bridgeId);
    }

    @Override
    public void clear() {
        store.clear();
    }
}