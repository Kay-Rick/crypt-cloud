package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.common.dto.BasicDTO;

import java.util.List;

public interface RoleService {

    BasicDTO addRole(String rolename);

    BasicDTO assignUser(String username, String rolename);

    BasicDTO uploadFile(String rolename, String filename, String content);

    List<Role> getAllRoles();

    List<RoleFile> getAllRoleFiles(String rolename);
}
