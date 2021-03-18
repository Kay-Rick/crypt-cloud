package com.rick.cryptcloud.DO;

public class FK {
    private Integer id;

    private String rolename;

    private Integer versionRole;

    private Integer versionFile;

    private String operation;

    private String tag;

    private String cipherFk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Integer getVersionRole() {
        return versionRole;
    }

    public void setVersionRole(Integer versionRole) {
        this.versionRole = versionRole;
    }

    public Integer getVersionFile() {
        return versionFile;
    }

    public void setVersionFile(Integer versionFile) {
        this.versionFile = versionFile;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCipherFk() {
        return cipherFk;
    }

    public void setCipherFk(String cipherFk) {
        this.cipherFk = cipherFk;
    }
}