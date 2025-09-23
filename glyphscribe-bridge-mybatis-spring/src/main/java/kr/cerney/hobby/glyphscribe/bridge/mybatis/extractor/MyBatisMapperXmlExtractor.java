package kr.cerney.hobby.glyphscribe.bridge.mybatis.extractor;

import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.registry.SqlTemplateRegistries;
import kr.cerney.hobby.glyphscribe.core.registry.SqlTemplateRegistry;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * MyBatis Mapper XML에서 SQL ID, 원본 SQL, 주석을 추출하여 RawSqlRegistry에 등록
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class MyBatisMapperXmlExtractor {
    private static final Logger               LOGGER         = LoggerFactory.getLogger(MyBatisMapperXmlExtractor.class);
    private static final Set<String>          SUPPORTED_TAGS = Set.of("select", "insert", "update", "delete", "merge into");
    private static final SqlTemplateRegistry  REGISTRY       = SqlTemplateRegistries.getGlobal();
    private final        List<CommentPattern> commentPatterns;

    public MyBatisMapperXmlExtractor(List<CommentPattern> commentPatterns) {
        this.commentPatterns = commentPatterns;
    }

    /**
     * Configuration 내부의 모든 XML Mapper를 탐색하여 SQL 원문 및 주석 추출
     */
    public void extractFromConfiguration(Configuration configuration) {
        for (Object obj : configuration.getMappedStatements()) {
            if (!(obj instanceof MappedStatement ms)) {
                continue;
            }

            String resource = ms.getResource();
            String sqlId    = ms.getId();

            if (REGISTRY.contains(sqlId)) {
                continue;
            }
            if (resource != null && resource.contains(".xml")) {
                extractSqlAndRegister(resource, sqlId);
            }
        }
    }

    private void extractSqlAndRegister(String resourcePath, String sqlId) {
        try {
            URL resourceUrl = MyBatisMapperXmlExtractor.class.getClassLoader().getResource(resolveClasspath(resourcePath));
            if (resourceUrl == null) {
                return;
            }

            try (InputStream is = resourceUrl.openStream()) {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader  = factory.createXMLStreamReader(is, StandardCharsets.UTF_8.name());

                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        String tag = reader.getLocalName();
                        if (isSqlTag(tag)) {
                            String idAttr = reader.getAttributeValue(null, "id");
                            if (idAttr != null && sqlId.endsWith(idAttr)) {
                                String rawSql  = reader.getElementText().trim();
                                String comment = extractComment(rawSql);
                                REGISTRY.put(sqlId, rawSql, comment);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse SQL from mapper XML: {} (reason: {})", resourcePath, e.getMessage(), e);
        }
    }

    private boolean isSqlTag(String tag) {
        return SUPPORTED_TAGS.contains(tag.toLowerCase());
    }

    /**
     * 설정된 패턴 기반으로 주석 추출 (첫 등장만 반환)
     */
    private String extractComment(String sql) {
        for (CommentPattern pattern : commentPatterns) {
            int start = sql.indexOf(pattern.start());
            if (start != -1) {
                int end = sql.indexOf(pattern.end(), start + pattern.start().length());
                if (end != -1) {
                    return sql.substring(start, end + pattern.end().length()).trim();
                }
            }
        }
        return null;
    }

    /**
     * resource 경로에서 classpath: prefix 제거
     */
    private String resolveClasspath(String resource) {
        int idx = resource.indexOf("classpath:");
        if (idx != -1) {
            return resource.substring(idx + "classpath:".length())
                    .replace("file [", "")
                    .replace("]", "")
                    .trim();
        }
        return resource;
    }
}
