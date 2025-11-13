package tech.waterfall.register.model;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Status status;
    protected long createdId;
    protected String createdName;
    protected Date createdDate;
    protected String createdProxyName;
    protected long lastModifiedId;
    protected String lastModifiedName;
    protected Date lastModifiedDate;
    protected String lastModifiedProxyName;
    protected long resourceBundleId;
    protected long accountId;
    protected long organizationId;
    protected String shortId;

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getCreatedId() {
        return this.createdId;
    }

    public void setCreatedId(long createdId) {
        this.createdId = createdId;
    }

    public String getCreatedName() {
        return this.createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedProxyName() {
        return this.createdProxyName;
    }

    public void setCreatedProxyName(String createdProxyName) {
        this.createdProxyName = createdProxyName;
    }

    public long getLastModifiedId() {
        return this.lastModifiedId;
    }

    public void setLastModifiedId(long lastModifiedId) {
        this.lastModifiedId = lastModifiedId;
    }

    public String getLastModifiedName() {
        return this.lastModifiedName;
    }

    public void setLastModifiedName(String lastModifiedName) {
        this.lastModifiedName = lastModifiedName;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedProxyName() {
        return this.lastModifiedProxyName;
    }

    public void setLastModifiedProxyName(String lastModifiedProxyName) {
        this.lastModifiedProxyName = lastModifiedProxyName;
    }

    public long getResourceBundleId() {
        return this.resourceBundleId;
    }

    public void setResourceBundleId(long resourceBundleId) {
        this.resourceBundleId = resourceBundleId;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getShortId() {
        return this.shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public void bindBaseInfo(long createdId, String createdName) {
        this.bindBaseInfo(createdId, createdName, new Date());
    }

    public void bindBaseInfo(long createdId, String createdName, Date date) {
        this.setCreatedId(createdId);
        this.setCreatedName(createdName);
        this.setLastModifiedId(createdId);
        this.setLastModifiedName(createdName);
        this.setStatus(Status.A);
        this.setCreatedDate(date);
        this.setLastModifiedDate(date);
    }
}
