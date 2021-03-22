package com.rick.cryptcloud.service;

public interface RevokeService {
    
    void revokeUserRole(String username, String rolename);

    void revokeRoleFile(String rolename, String filename);
}
