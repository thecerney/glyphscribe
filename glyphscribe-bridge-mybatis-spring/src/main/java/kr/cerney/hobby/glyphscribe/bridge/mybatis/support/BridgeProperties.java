package kr.cerney.hobby.glyphscribe.bridge.mybatis.support;

import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "glyphscribe.bridge")
public class BridgeProperties {
    /**
     * 스프링이 바인딩할 대상 (없거나 빈이면 core 기본값 사용)
     */
    private List<CommentPattern> commentPatterns;
    private boolean injectInterceptor = true;

    public List<CommentPattern> getCommentPatterns() {
        return SqlCommentDefaults.orDefaults(commentPatterns);
    }

    public void setCommentPatterns(List<CommentPattern> commentPatterns) {
        this.commentPatterns = commentPatterns;
    }

    public boolean isInjectInterceptor() {
        return injectInterceptor;
    }

    public void setInjectInterceptor(boolean injectInterceptor) {
        this.injectInterceptor = injectInterceptor;
    }
}