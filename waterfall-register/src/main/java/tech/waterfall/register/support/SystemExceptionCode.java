package tech.waterfall.register.support;

public enum SystemExceptionCode {
    SYSTEM_ERROR(-1, "System error."),
    DATA_ACCESS_ERROR(-2, "Data access error."),
    GENERATE_PRIMARY_KEY_ERROR(-3, "Generate primary key error."),
    GERNATE_CRITERIA_FOR_QUERY_ERROR(-4, "Generate criterid for query error."),
    CROSS_MODULE_INVOKE_ERROR(-5, "Please use business module implement."),
    SERVICE_ERROR(-6, "Error occurred when accessiong services"),
    INIT_DATABASE_ERROR(-7, "Error occurred when init database"),
    DATA_SIZE_OUT_RANGE(3108, "Error occurred when query page from elasticsearch");

    private int code;
    private String message;

    private SystemExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
