package kr.cerney.hobby.glyphscribe.core.registry;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class SqlTemplateRegistries {
    public static final String DEFAULT = "default";

    private static final ConcurrentHashMap<String, SqlTemplateRegistry> REGISTRIES = new ConcurrentHashMap<>();

    static {
        REGISTRIES.put(DEFAULT, new InMemorySqlTemplateRegistry());
    }

    private SqlTemplateRegistries() {
    }

    /**
     * 기본 레지스트리 반환
     */
    public static SqlTemplateRegistry getGlobal() {
        return get(DEFAULT);
    }

    /**
     * 네임스페이스별 레지스트리 반환 (없으면 생성)
     */
    public static SqlTemplateRegistry get(String namespace) {
        String ns = (namespace == null || namespace.isBlank()) ? DEFAULT : namespace;
        return REGISTRIES.computeIfAbsent(ns, k -> new InMemorySqlTemplateRegistry());
    }

    /**
     * 네임스페이스에 구현 교체 (파일/Redis/DB 등등...)
     */
    public static void set(String namespace, SqlTemplateRegistry registry) {
        String ns = (namespace == null || namespace.isBlank()) ? DEFAULT : namespace;
        Objects.requireNonNull(registry, "SqlTemplateRegistry must not be null");
        REGISTRIES.put(ns, registry);
    }

    /**
     * 특정 네임스페이스 제거
     */
    public static void remove(String namespace) {
        String ns = (namespace == null || namespace.isBlank()) ? DEFAULT : namespace;
        REGISTRIES.remove(ns);
    }

    /**
     * 전부 초기화
     */
    public static void clearAll() {
        REGISTRIES.values().forEach(SqlTemplateRegistry::clear);
        REGISTRIES.clear();
        REGISTRIES.put(DEFAULT, new InMemorySqlTemplateRegistry());
    }
}