package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.core.config.FormatterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot 설정 바인딩용 래퍼
 */
@ConfigurationProperties(prefix = "glyphscribe.format")
public class FormatterConfigProperties extends FormatterConfig {
}