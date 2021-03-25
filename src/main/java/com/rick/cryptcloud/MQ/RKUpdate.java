package com.rick.cryptcloud.MQ;

import java.io.Serializable;
import java.util.Map;

public class RKUpdate implements Serializable {
    
    private String username;

    private String rolename;

    private Integer versionRole;

    private Map<String, Object> updateInfo;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return this.rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Integer getVersionRole() {
        return this.versionRole;
    }

    public void setVersionRole(Integer versionRole) {
        this.versionRole = versionRole;
    }

    public Map<String,Object> getUpdateInfo() {
        return this.updateInfo;
    }

    public void setUpdateInfo(Map<String,Object> updateInfo) {
        this.updateInfo = updateInfo;
    }

}
