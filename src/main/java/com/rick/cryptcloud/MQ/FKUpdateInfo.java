package com.rick.cryptcloud.MQ;

import com.rick.cryptcloud.DO.CipherFK;

public class FKUpdateInfo {

    private Integer versionRole;

    private Integer versionFile;

    private CipherFK cipherFk;

    private String signature;

    private String cipherFKText;

    public String getCipherFKText() {
        return this.cipherFKText;
    }

    public void setCipherFKText(String cipherFKText) {
        this.cipherFKText = cipherFKText;
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

    public CipherFK getCipherFk() {
        return this.cipherFk;
    }

    public void setCipherFk(CipherFK cipherFk) {
        this.cipherFk = cipherFk;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
