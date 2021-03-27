package com.rick.cryptcloud.service;

import com.rick.cryptcloud.common.dto.BasicDTO;

public interface RevokeService {
    
    BasicDTO revokeUserRole(String username, String rolename);

    void revokeRoleFile(String rolename, String filename);
}
