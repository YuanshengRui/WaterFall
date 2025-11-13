package tech.waterfall.register.support.column;

import tech.waterfall.register.model.ColumnBase;

public enum BaseInfoColumn implements ColumnBase {
    ID("id"),
    id("_id"),
    accountId("accountId"),
    status("status"),
    createdId("createdId"),
    createdName("createdName"),
    lastModifiedId("lastModifiedId"),
    lastModifiedName("lastModifiedName"),
    createdDate("createdDate"),
    lastModifiedDate("lastModifiedDate"),
    organizationId("organizationId"),
    createdProxyName("createdProxyName"),
    lastModifiedProxyName("lastModifiedProxyName"),
    resourceBundleId("resourceBundleId"),
    contactOrgId("contactOrgId"),
    contactId("contactId"),
    shortId("shortId"),
    taskStatus("taskStatus"),
    notificationId("notificationId"),
    type("type"),
    processCount("processCount");

    private final String columnName;

    private BaseInfoColumn(String name) {
        this.columnName = name;
    }

    public String value() {
        return this.columnName;
    }
}
