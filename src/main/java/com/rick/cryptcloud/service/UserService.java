package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DTO.BasicDTO;

import java.util.List;

public interface UserService {

    List<User> queryAll();

    BasicDTO addUser(String username, String email, String password);
}
