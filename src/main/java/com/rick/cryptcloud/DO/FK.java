package com.rick.cryptcloud.DO;

import java.io.Serializable;

public class FK implements Serializable{
    private Integer id;

    private String rolename;

    private String filename;

    private Integer versionRole;

    private Integer versionFile;

    private String operation;

    private Integer tag;

    private String cipherFk;

    private String signature;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getCipherFk() {
        return cipherFk;
    }

    public void setCipherFk(String cipherFk) {
        this.cipherFk = cipherFk;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}