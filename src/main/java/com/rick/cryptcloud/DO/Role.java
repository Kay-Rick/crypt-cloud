package com.rick.cryptcloud.DO;

public class Role {
    private Integer id;

    private String rolename;

    private String publicKey;

    private String privateKey;

    private String signPublic;

    private String signPrivate;

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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSignPublic() {
        return signPublic;
    }

    public void setSignPublic(String signPublic) {
        this.signPublic = signPublic;
    }

    public String getSignPrivate() {
        return signPrivate;
    }

    public void setSignPrivate(String signPrivate) {
        this.signPrivate = signPrivate;
    }
}