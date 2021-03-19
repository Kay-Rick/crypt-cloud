package com.rick.cryptcloud.DO;

public class F {
    private Integer id;

    private String filename;

    private String signature;

    private String cryptoFile;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCryptoFile() {
        return cryptoFile;
    }

    public void setCryptoFile(String cryptoFile) {
        this.cryptoFile = cryptoFile;
    }
}