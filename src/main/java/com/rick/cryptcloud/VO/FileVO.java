package com.rick.cryptcloud.VO;

import java.util.List;

public class FileVO {

    private Integer id;

    private String filename;

    private String cipherFK;

    private Long[] cipherFKList;

    private List<String> roleNameList;

    private String fileType;

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

    public String getCipherFK() {
        return cipherFK;
    }

    public void setCipherFK(String cipherFK) {
        this.cipherFK = cipherFK;
    }

    public Long[] getCipherFKList() {
        return cipherFKList;
    }

    public void setCipherFKList(Long[] cipherFKList) {
        this.cipherFKList = cipherFKList;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
