package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DTO.BasicDTO;

public interface RoleService {

    BasicDTO addRole(String rolename);

    BasicDTO assignUser(String username, String rolename);

    BasicDTO uploadFile(String rolename, String filename);
}
