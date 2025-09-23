package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.MyBatisBridgeInitializer;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.ShortIdIndex;
import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring 환경에서 MyBatis Bridge 자동 초기화 구성 클래스
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class MyBatisBridgeAutoInitializer implements SmartInitializingSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisBridgeAutoInitializer.class);

    private final List<SqlSessionFactory> factories;
    private final ShortIdIndex            shortIdIndex;
    private final Environment             env;

    public MyBatisBridgeAutoInitializer(List<SqlSessionFactory> factories, ShortIdIndex shortIdIndex, Environment env) {
        LOGGER.info("MyBatisBridgeAutoInitializer 생성자 호출 (factories: {})", factories.size());
        this.factories = factories;
        this.shortIdIndex = shortIdIndex;
        this.env = env;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            // 1) 코멘트 패턴 로딩 (공식 Resolver 사용)
            List<CommentPattern> patterns = BridgeSettingsResolver.resolveCommentPatterns(env);

            // 2) short id 인덱스 초기화 + 팩토리별 구성/스캔
            shortIdIndex.clear();

            for (SqlSessionFactory factory : factories) {
                Configuration cfg = factory.getConfiguration();

                // 1) 숏 ID 인덱스는 이름 기반으로 구축 (Ambiguity 영향 없음)
                cfg.getMappedStatementNames().forEach(shortIdIndex::add);

                // 인터셉터 사후 주입 + XML 스캔 → 템플릿 적재
                MyBatisBridgeInitializer.init(cfg, patterns);
            }

            LOGGER.info("MyBatisBridge initialized: {} SqlSessionFactory(ies) processed.", factories.size());
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize MyBatisBridge: {}", e.getMessage(), e);
        }
    }

    private List<CommentPattern> loadPatternsFromConfig() {
        String[]             raw      = env.getProperty("glyphscribe.comment-patterns", String[].class);
        List<CommentPattern> patterns = new ArrayList<>();
        LOGGER.info("MyBatisBridge loadPatternsFromConfig 호출");

        if (raw != null) {
            for (String item : raw) {
                String[] parts = item.trim().split("\\s+", 2);
                if (parts.length == 2) {
                    patterns.add(new CommentPattern(parts[0], parts[1]));
                }
            }
        }

        if (patterns.isEmpty()) {
            patterns.addAll(SqlCommentDefaults.defaults());
        }

        return patterns;
    }
}
