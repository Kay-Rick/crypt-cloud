package com.rick.cryptcloud.DO;

import java.io.Serializable;

public class RK implements Serializable {

    private static final long serialVersionUID = 8267757693L;

    private Integer id;

    private Integer versionRole;

    private String username;

    private String rolename;

    private String cryptoRolekey;

    private String cryptoRolesign;

    private String signature;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersionRole() {
        return versionRole;
    }

    public void setVersionRole(Integer versionRole) {
        this.versionRole = versionRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getCryptoRolekey() {
        return cryptoRolekey;
    }

    public void setCryptoRolekey(String cryptoRolekey) {
        this.cryptoRolekey = cryptoRolekey;
    }

    public String getCryptoRolesign() {
        return cryptoRolesign;
    }

    public void setCryptoRolesign(String cryptoRolesign) {
        this.cryptoRolesign = cryptoRolesign;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}