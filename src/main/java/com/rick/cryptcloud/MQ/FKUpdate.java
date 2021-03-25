package com.rick.cryptcloud.MQ;

import java.io.Serializable;
import java.util.Map;


public class FKUpdate implements Serializable {
    
    private String rolename;

    private String filename;

    private Integer versionRole;

    private Integer versionFile;

    private Map<String, Object> updateInfo;


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

    public Map<String,Object> getUpdateInfo() {
        return this.updateInfo;
    }

    public void setUpdateInfo(Map<String,Object> updateInfo) {
        this.updateInfo = updateInfo;
    }

}
