package com.rick.cryptcloud.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.VO.RoleVO;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.common.Enum.ResultEnum;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.service.RoleService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("role")
public class RoleController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String RW = "读写";

    private final static String R = "读";
    
    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public ResultVO<List<RoleVO>> getAllRoles() {
        List<RoleVO> result = new ArrayList<>();
        List<Role> roleList = roleService.getAllRoles();
        if (null == roleList|| roleList.isEmpty()) {
            return new ResultVO<>(ResultEnum.FAILED, null);
        }
        else {
            for (Role role : roleList) {
                RoleVO item = new RoleVO();
                item.setId(role.getId());
                item.setRolename(role.getRolename());
                List<RoleFile> roleFileList = roleService.getAllRoleFiles(role.getRolename());
                if (null != roleFileList && !roleFileList.isEmpty()) {
                    for (RoleFile roleFile : roleFileList) {
                        item.setFilename(roleFile.getFilename());
                        if (StringUtils.equals("rw", roleFile.getOperation())) {
                            item.setOperation(RW);
                        }
                        else {
                            item.setOperation(R);
                        }
                        result.add(item);
                    }
                }
                else {
                    result.add(item);
                }

            }
            return new ResultVO<>(ResultEnum.SUCCESS, result);
        }
    }
    
    /**
     * 添加角色
     * @param rolename
     * @return
     */
    @RequestMapping("add")
    public ResultVO<String> addRole(String rolename) {
        log.info("开始添加角色：{}", rolename);
        BasicDTO roleDTO = roleService.addRole(rolename);
        if (roleDTO.getCode() == ResultEnum.FAILED.getCode()) {
            log.info("添加角色：{}失败：{}", rolename, roleDTO.getMessage());
            return new ResultVO<>(ResultEnum.ERROR, roleDTO.getMessage());
        }
        log.info("添加角色：{}成功", rolename);
        return new ResultVO<>(ResultEnum.SUCCESS, roleDTO.getMessage());
    }

    /**
     * 用户授予角色权限
     * @param rolename
     * @param username
     * @return
     */
    @RequestMapping(value = "assign", method = RequestMethod.POST)
    public ResultVO<String> addUser(String rolename, String username) {
        log.info("开始为用户：{}授角色：{}权限", username, rolename);
        BasicDTO roleDTO = roleService.assignUser(username, rolename);
        if (roleDTO.getCode() == ResultEnum.FAILED.getCode()) {
            log.info("为用户：{}授权失败：{}", username, roleDTO.getMessage());
            return new ResultVO<>(ResultEnum.ERROR, "为用户授权失败");
        }
        log.info("为用户：{}授权成功", username);
        return new ResultVO<>(ResultEnum.SUCCESS, "为用户授权成功");
    }

    /**
     * 为role角色上传文件
     * @param rolename
     * @param file
     * @return
     */
    @RequestMapping("upload")
    public ResultVO<String> addFile(String rolename, @RequestPart("file") MultipartFile file) {
        log.info("rolename={},filename={}", rolename, file.getOriginalFilename());
        if (!file.isEmpty()) {
            try {
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                String filename = file.getOriginalFilename();
                roleService.uploadFile(rolename, filename, fileContent);
            } catch (Exception e) {
                log.error("{}上传失败：{}", file.getOriginalFilename(), e.getMessage());
                return new ResultVO<>(ResultEnum.FAILED, "文件上传失败");
            }
            log.info("{}上传成功", file.getOriginalFilename());
        }
        return new ResultVO<>(ResultEnum.SUCCESS, "文件上传成功");
    }
}
