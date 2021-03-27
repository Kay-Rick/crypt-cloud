package com.rick.cryptcloud.service;

import com.rick.cryptcloud.common.dto.BasicDTO;

public interface UserService {

    BasicDTO addUser(String username, String email, String password);
}
