package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.common.dto.UserDTO;

import java.util.List;

public interface UserService {

    BasicDTO addUser(String username, String email, String password);

    List<User> getUsers();

    List<UserDTO> getUserRoles(User user);
}
