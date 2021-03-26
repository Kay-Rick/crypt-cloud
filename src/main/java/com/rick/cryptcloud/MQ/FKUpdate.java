package com.rick.cryptcloud.MQ;

public class FKUpdate {
    
    private String rolename;

    private String filename;

    private String operation;

    private Integer tag;

    private Integer versionRole;

    private Integer versionFile;

    private FKUpdateInfo updateInfo;

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getTag() {
        return this.tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public FKUpdateInfo getUpdateInfo() {
        return this.updateInfo;
    }

    public void setUpdateInfo(FKUpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getRolename() {
        return this.rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getVersionRole() {
        return this.versionRole;
    }

    public void setVersionRole(Integer versionRole) {
        this.versionRole = versionRole;
    }

    public Integer getVersionFile() {
        return this.versionFile;
    }

    public void setVersionFile(Integer versionFile) {
        this.versionFile = versionFile;
    }

}
