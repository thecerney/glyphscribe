package kr.cerney.hobby.glyphscribe.core.config;

/**
 * 로그 출력 포맷 설정
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class FormatterConfig {
    private boolean enableAutoLogging    = true;
    private boolean showStartTime        = true;
    private boolean showElapsedTime      = true;
    private boolean showSqlId            = true;
    private boolean showRawSql           = false;
    private boolean showAssembledSql     = false;
    private boolean showExecutedSql      = true;
    private boolean showParams           = false;
    private boolean showComment          = true;
    private boolean showSeparator        = true;
    private boolean insertCommentIntoSql = false;
    private String  timestampFormat      = "yyyy-MM-dd HH:mm:ss.SSS";
    private String  separator            = "==========================================================================";

    public boolean isEnableAutoLogging() {
        return enableAutoLogging;
    }

    public void setEnableAutoLogging(boolean enableAutoLogging) {
        this.enableAutoLogging = enableAutoLogging;
    }

    public boolean isShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(boolean showStartTime) {
        this.showStartTime = showStartTime;
    }

    public boolean isShowElapsedTime() {
        return showElapsedTime;
    }

    public void setShowElapsedTime(boolean showElapsedTime) {
        this.showElapsedTime = showElapsedTime;
    }

    public boolean isShowSqlId() {
        return showSqlId;
    }

    public void setShowSqlId(boolean showSqlId) {
        this.showSqlId = showSqlId;
    }

    public boolean isShowRawSql() {
        return showRawSql;
    }

    public void setShowRawSql(boolean showRawSql) {
        this.showRawSql = showRawSql;
    }

    public boolean isShowAssembledSql() {
        return showAssembledSql;
    }

    public void setShowAssembledSql(boolean showAssembledSql) {
        this.showAssembledSql = showAssembledSql;
    }

    public boolean isShowExecutedSql() {
        return showExecutedSql;
    }

    public void setShowExecutedSql(boolean showExecutedSql) {
        this.showExecutedSql = showExecutedSql;
    }

    public boolean isShowParams() {
        return showParams;
    }

    public void setShowParams(boolean showParams) {
        this.showParams = showParams;
    }

    public boolean isShowComment() {
        return showComment;
    }

    public void setShowComment(boolean showComment) {
        this.showComment = showComment;
    }

    public boolean isShowSeparator() {
        return showSeparator;
    }

    public void setShowSeparator(boolean showSeparator) {
        this.showSeparator = showSeparator;
    }

    public boolean isInsertCommentIntoSql() {
        return insertCommentIntoSql;
    }

    public void setInsertCommentIntoSql(boolean insertCommentIntoSql) {
        this.insertCommentIntoSql = insertCommentIntoSql;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
