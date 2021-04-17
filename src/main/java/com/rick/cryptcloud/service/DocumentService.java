package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DO.Document;
import com.rick.cryptcloud.DO.RoleFile;

import java.util.List;

public interface DocumentService {

    List<Document> getAllFiles();

    List<RoleFile> getRoleFiles(String filename);

    Long[] getCipherList(String k0);
}
