package com.rick.cryptcloud.DO;

public class User {
    private Integer id;

    private String username;

    private String mail;

    private String password;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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