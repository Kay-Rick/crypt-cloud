package com.rick.cryptcloud.DO;

public class RK {
    private Integer id;

    private String username;

    private String rolename;

    private String cryptoRolekey;

    private String cryptoRolesign;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}