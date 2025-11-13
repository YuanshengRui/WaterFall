package tech.waterfall.register.support.column;

import tech.waterfall.register.model.EnumBase;

public enum AppServerColumn implements EnumBase {
    hostName,
    propertyFile,
    appName,
    status("appServerSatus"),
    lastModifiedDate;

    private String value;

    private AppServerColumn() {
    }

    private AppServerColumn(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
