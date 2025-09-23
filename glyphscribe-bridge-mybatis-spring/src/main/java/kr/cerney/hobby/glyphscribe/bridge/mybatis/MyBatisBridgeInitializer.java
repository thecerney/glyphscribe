package kr.cerney.hobby.glyphscribe.bridge.mybatis;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.extractor.MyBatisMapperXmlExtractor;
import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * MyBatis 브릿지 초기화 진입점
 * - Mapper XML에서 SQL 원문 및 주석을 추출하여 RawSqlRegistry에 등록
 * - bridge 사용자가 명시적으로 init 메서드를 호출해야 함
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class MyBatisBridgeInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisBridgeInitializer.class);

    private MyBatisBridgeInitializer() {}

    /**
     * 주석 패턴을 명시하지 않을 경우 기본 패턴 사용
     */
    public static void init(Configuration configuration) {
        init(configuration, SqlCommentDefaults.defaults());
    }

    /**
     * 주석 패턴을 외부에서 지정할 수 있도록 확장된 초기화 메서드
     */
    public static void init(Configuration configuration, List<CommentPattern> commentPatterns) {
        LOGGER.info("[MyBatisBridgeInitializer] init 시작함");
        MyBatisMapperXmlExtractor extractor = new MyBatisMapperXmlExtractor(SqlCommentDefaults.defaults());
        extractor.extractFromConfiguration(configuration);
    }
}
