package com.rick.cryptcloud.service.user;

import com.rick.cryptcloud.domain.User;

import java.util.List;

public interface UserService {
    void insertUser(User user);
    List<User> queryAll();
}