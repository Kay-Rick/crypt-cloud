package com.rick.cryptcloud.MQ;

public class RKUpdateInfo {

    private Integer versionRole;

    private String cryptoRolekey;

    private String cryptoRolesign;

    private String signature;

    public Integer getVersionRole() {
        return this.versionRole;
    }

    public void setVersionRole(Integer versionRole) {
        this.versionRole = versionRole;
    }

    public String getCryptoRolekey() {
        return this.cryptoRolekey;
    }

    public void setCryptoRolekey(String cryptoRolekey) {
        this.cryptoRolekey = cryptoRolekey;
    }

    public String getCryptoRolesign() {
        return this.cryptoRolesign;
    }

    public void setCryptoRolesign(String cryptoRolesign) {
        this.cryptoRolesign = cryptoRolesign;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
