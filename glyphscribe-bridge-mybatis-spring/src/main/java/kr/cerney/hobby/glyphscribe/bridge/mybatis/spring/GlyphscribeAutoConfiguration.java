package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.BindingParameterAwareIdMapper;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.MyBatisBridgeInitializer;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.MyBatisSqlLogInterceptor;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.ShortIdIndex;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.constants.BridgeProperties;
import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.api.IdMapper;
import kr.cerney.hobby.glyphscribe.core.api.LogPrinter;
import kr.cerney.hobby.glyphscribe.core.archive.LogArchive;
import kr.cerney.hobby.glyphscribe.core.config.FormatterConfig;
import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.DefaultFormatter;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Spring 환경에서 MyBatis Bridge 자동 초기화 구성 클래스
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
@AutoConfiguration
@EnableConfigurationProperties({FormatterConfigProperties.class, BridgeProperties.class})
public class GlyphscribeAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlyphscribeAutoConfiguration.class);

    @Bean
    @ConfigurationPropertiesBinding
    @ConditionalOnMissingBean
    public Converter<String, CommentPattern> commentPatternConverter() {
        return new CommentPatternConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortIdIndex shortIdIndex() {
        return new ShortIdIndex();
    }

    /**
     * 단일 싱글톤 IdMapper 빈 제공
     * (절대 new 다중 생성 금지)
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(IdMapper.class)
    public IdMapper idMapper(ShortIdIndex shortIdIndex) {
        return new BindingParameterAwareIdMapper(shortIdIndex);
    }

    @Bean
    @ConditionalOnMissingBean(Formatter.class)
    public Formatter glyphscribeFormatter(FormatterConfig config) {
        return new DefaultFormatter(config);
    }

    @Bean(name = "logPrinter")
    @ConditionalOnMissingBean(name = "logPrinter")
    public LogPrinter glyphscribeLogPrinter(IdMapper idMapper, Formatter formatter, BridgeProperties bridgeProps) {
        return new GlyphscribeLogPrinter(idMapper, formatter, bridgeProps.getLogLevel());
    }

    @Bean
    public MyBatisBridgeAutoInitializer myBatisBridgeAutoInitializer(List<SqlSessionFactory> factories, ShortIdIndex shortIdIndex, Environment env) {
        return new MyBatisBridgeAutoInitializer(factories, shortIdIndex, env);
    }

    /**
     * 초기화: XML 스캔(PreArchive 적재) + Logger에 IdMapper 주입 보장
     */
    @Bean
    public SmartInitializingSingleton glyphscribeInitializer(List<SqlSessionFactory> factories, IdMapper idMapper, Formatter formatter, FormatterConfig formatterConfig, BridgeProperties bridgeProps) {
        return () -> {
            // Archive ↔ IdMapper 동기화
            LogArchive.getInstance().setEvictionListener(idMapper::evictByCoreKey);

            // 주석 형태 설정 적용 (없으면 기본)
            List<CommentPattern> patterns = bridgeProps.getCommentPatterns();

            // 인터셉터 주입 (중복 방지)
            for (SqlSessionFactory factory : factories) {
                Configuration cfg = factory.getConfiguration();
                if (bridgeProps.isInjectInterceptor()) {
                    boolean already = cfg.getInterceptors()
                            .stream()
                            .anyMatch(it -> it instanceof MyBatisSqlLogInterceptor);

                    LOGGER.debug("[GlyphscribeAutoConfiguration] MyBatisSqlLogInterceptor already present: {}", already);

                    if (!already) {
                        cfg.addInterceptor(new MyBatisSqlLogInterceptor(LogArchive.getInstance(), idMapper, formatter, formatterConfig, bridgeProps));
                        LOGGER.debug("[GlyphscribeAutoConfiguration] MyBatisSqlLogInterceptor added, cfg={}", cfg.getInterceptors());
                    } else {
                        LOGGER.debug("[GlyphscribeAutoConfiguration] MyBatisSqlLogInterceptor already present, skip");
                    }
                }

                MyBatisBridgeInitializer.init(cfg, patterns);
            }
        };
    }
}
