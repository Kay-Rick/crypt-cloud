package com.rick.cryptcloud.controller;

import com.google.gson.Gson;
import com.rick.cryptcloud.service.RevokeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("revoke")
public class RevokeController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();
    
    @Autowired
    private RevokeService revokeService;

    @RequestMapping("role")
    public void userRevoke(String username, String rolename) {
        revokeService.revokeUserRole(username, rolename);
    }
}
