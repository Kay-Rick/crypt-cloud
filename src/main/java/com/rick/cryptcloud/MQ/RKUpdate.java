package com.rick.cryptcloud.MQ;

public class RKUpdate {
    
    private String username;

    private String rolename;

    private Integer versionRole;

    private RKUpdateInfo updateInfo;

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


    public RKUpdateInfo getUpdateInfo() {
        return this.updateInfo;
    }

    public void setUpdateInfo(RKUpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }


}
