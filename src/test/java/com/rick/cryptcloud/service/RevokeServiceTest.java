package com.rick.cryptcloud.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.MQ.RKUpdate;
import com.rick.cryptcloud.MQ.RKUpdateInfo;
import com.rick.cryptcloud.dao.RKMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RevokeServiceTest {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private RKMapper rkMapper;
    
    @Test
    public void test1() {
        RKUpdate rkUpdate = new RKUpdate();
        RK rk = rkMapper.selectByPrimaryKey(24);
        RKUpdateInfo rkUpdateInfo = new RKUpdateInfo();
        rkUpdateInfo.setCryptoRolekey(rk.getCryptoRolekey());
        rkUpdateInfo.setCryptoRolesign(rk.getCryptoRolesign());
        rkUpdateInfo.setSignature(rk.getSignature());
        rkUpdateInfo.setVersionRole(rk.getVersionRole());
        rkUpdate.setRolename(rk.getRolename());
        rkUpdate.setUsername(rk.getUsername());
        rkUpdate.setVersionRole(rk.getVersionRole());
        rkUpdate.setUpdateInfo(rkUpdateInfo);
        String serializeBean = GSON.toJson(rkUpdate);
        System.out.println(serializeBean);
        RKUpdate recover = GSON.fromJson(serializeBean, RKUpdate.class);
        System.out.println(GSON.toJson(recover.getUpdateInfo()));
        RKUpdateInfo rkuUpdateInfo = recover.getUpdateInfo();
        System.out.println(rkuUpdateInfo.getVersionRole());
    }
}
