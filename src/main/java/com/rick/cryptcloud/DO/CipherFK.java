package com.rick.cryptcloud.DO;

import java.io.Serializable;

public class CipherFK implements Serializable{
    private Integer id;

    private String k0;

    private String kT;

    private String rpk;

    private Integer t;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getK0() {
        return k0;
    }

    public void setK0(String k0) {
        this.k0 = k0;
    }

    public String getkT() {
        return kT;
    }

    public void setkT(String kT) {
        this.kT = kT;
    }

    public String getRpk() {
        return rpk;
    }

    public void setRpk(String rpk) {
        this.rpk = rpk;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }
}