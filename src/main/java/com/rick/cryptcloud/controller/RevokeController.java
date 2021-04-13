package com.rick.cryptcloud.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.common.Enum.ResultEnum;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.service.RevokeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("revoke")
public class RevokeController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    
    @Autowired
    private RevokeService revokeService;

    @RequestMapping(value = "role",method = RequestMethod.POST)
    public ResultVO<String> userRevoke(String username, String rolename) {
        log.info("撤销用户：{}的：{}权限", username, rolename);
        BasicDTO revokeDTO = revokeService.revokeUserRole(username, rolename);
        log.info("撤销用户：{}的{}权限出参：{}", username, rolename, GSON.toJson(revokeDTO));
        if (DTOEnum.FAILED.getCode() == revokeDTO.getCode()) {
            return new ResultVO<>(ResultEnum.FAILED, "撤销权限失败");
        }
        return new ResultVO<>(ResultEnum.SUCCESS, "用户权限撤销成功");
    }
}
