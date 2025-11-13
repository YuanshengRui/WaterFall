package tech.waterfall.register.model;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class AppServer implements Serializable {
    private static final long serialVersionUID = 4254941934844575478L;
    @Id
    protected long id;
    protected String hostName;
    protected String propertyFile;
    protected String appName;
    protected Date createdDate;
    @Field("appServerSatus")
    protected Status status;
    protected Date lastModifiedDate;

    public long getId() {
        return this.id;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getPropertyFile() {
        return this.propertyFile;
    }

    public String getAppName() {
        return this.appName;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Status getStatus() {
        return this.status;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AppServer)) {
            return false;
        } else {
            AppServer other = (AppServer)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getId() != other.getId()) {
                return false;
            } else {
                Object this$hostName = this.getHostName();
                Object other$hostName = other.getHostName();
                if (this$hostName == null) {
                    if (other$hostName != null) {
                        return false;
                    }
                } else if (!this$hostName.equals(other$hostName)) {
                    return false;
                }

                Object this$propertyFile = this.getPropertyFile();
                Object other$propertyFile = other.getPropertyFile();
                if (this$propertyFile == null) {
                    if (other$propertyFile != null) {
                        return false;
                    }
                } else if (!this$propertyFile.equals(other$propertyFile)) {
                    return false;
                }

                Object this$appName = this.getAppName();
                Object other$appName = other.getAppName();
                if (this$appName == null) {
                    if (other$appName != null) {
                        return false;
                    }
                } else if (!this$appName.equals(other$appName)) {
                    return false;
                }

                Object this$createdDate = this.getCreatedDate();
                Object other$createdDate = other.getCreatedDate();
                if (this$createdDate == null) {
                    if (other$createdDate != null) {
                        return false;
                    }
                } else if (!this$createdDate.equals(other$createdDate)) {
                    return false;
                }

                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                if (this$status == null) {
                    if (other$status != null) {
                        return false;
                    }
                } else if (!this$status.equals(other$status)) {
                    return false;
                }

                Object this$lastModifiedDate = this.getLastModifiedDate();
                Object other$lastModifiedDate = other.getLastModifiedDate();
                if (this$lastModifiedDate == null) {
                    if (other$lastModifiedDate != null) {
                        return false;
                    }
                } else if (!this$lastModifiedDate.equals(other$lastModifiedDate)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof AppServer;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        Object $hostName = this.getHostName();
        result = result * 59 + ($hostName == null ? 43 : $hostName.hashCode());
        Object $propertyFile = this.getPropertyFile();
        result = result * 59 + ($propertyFile == null ? 43 : $propertyFile.hashCode());
        Object $appName = this.getAppName();
        result = result * 59 + ($appName == null ? 43 : $appName.hashCode());
        Object $createdDate = this.getCreatedDate();
        result = result * 59 + ($createdDate == null ? 43 : $createdDate.hashCode());
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $lastModifiedDate = this.getLastModifiedDate();
        result = result * 59 + ($lastModifiedDate == null ? 43 : $lastModifiedDate.hashCode());
        return result;
    }

    public String toString() {
        return "AppServer(id=" + this.getId() + ", hostName=" + this.getHostName() + ", propertyFile=" + this.getPropertyFile() + ", appName=" + this.getAppName() + ", createdDate=" + this.getCreatedDate() + ", status=" + this.getStatus() + ", lastModifiedDate=" + this.getLastModifiedDate() + ")";
    }

    public static enum Status {
        Free,
        Used;
    }
}
