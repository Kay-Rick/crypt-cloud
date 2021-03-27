package com.rick.cryptcloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.common.utils.AESUtils;
import com.rick.cryptcloud.common.utils.DSAUtils;
import com.rick.cryptcloud.common.utils.ElgamalUtils;
import com.rick.cryptcloud.common.utils.RotationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class UtilsTest {

    private final String baseLocation = "/Users/rick/Desktop/Server/Crypt-Cloud/";

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();


    @Test
    public void test1() {
        CipherFK cipherFK = new CipherFK();
        cipherFK.setK0("123");
        cipherFK.setkT("456");
        cipherFK.setT(1);
        cipherFK.setRpk("148");
        String obj = Base64.encodeBase64String(SerializationUtils.serialize(cipherFK));
        System.out.println(obj);
        CipherFK recover = SerializationUtils.deserialize(Base64.decodeBase64(obj));
        System.out.println(recover.getK0());
        System.out.println(recover.getkT());
        System.out.println(recover.getT());
        System.out.println(recover.getRpk());
    }

    @Test
    public void test2() {
        String content = "Hello Rick";
        try {
            FileUtils.writeStringToFile(new File(baseLocation + "download/download.txt"), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        String downloadFileContent = "";
        try {
            downloadFileContent = FileUtils.readFileToString(new File(baseLocation + "download/download.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(downloadFileContent);
    }

    @Test
    public void test4() {
        String key1 = AESUtils.generateAESKey();
        String key2 = AESUtils.generateAESKey();
        System.out.println(key1);
        System.out.println(key2);
        String text = "Hello Rick";
        String cipher1 = AESUtils.encryptAES(text, key1);
        System.out.println(cipher1);
        String cipher2 = AESUtils.encryptAES(cipher1, key2);
        System.out.println(cipher2);
        System.out.println(AESUtils.decryptAES(AESUtils.decryptAES(cipher2, key2), key1));
    }

/* 
    @Test
    public void test5() {
        String command = "cd /home/rick/WorkSpace/C++/src/ && make && ./test";
        CommandUtils.executeLinuxCmd(command);
    }
 */
/* 
    @Test
    public void test6() {
        String command  = "cd /home/rick/WorkSpace/C++/src/gen_key && make && ./genkey";
        CommandUtils.executeLinuxCmd(command);
        String elgamal_key_pub = "";
        try {
            elgamal_key_pub =  FileUtils.readFileToString(new File("/home/rick/ntl/elgamal_key_pub.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(elgamal_key_pub);
    }
 */

    @Test
    public void test7() {
        Map<String, Object> key1 = ElgamalUtils.initKey();
        String text = "Hello Rick";
        String cipher = ElgamalUtils.encryptByPublicKey(text, ElgamalUtils.getPublicKey(key1));
        System.out.println(ElgamalUtils.decryptByPrivateKey(cipher, ElgamalUtils.getPrivateKey(key1)));
        Map<String, Object> key2 = ElgamalUtils.initKey();
        System.out.println(ElgamalUtils.getPublicKey(key1));
        System.out.println(ElgamalUtils.getPublicKey(key2));

        System.out.println();
        System.out.println();
        System.out.println(ElgamalUtils.getPrivateKey(key1));
        System.out.println(ElgamalUtils.getPrivateKey(key2));
    }


    @Test
    public void test8() {
        String text = "Hello Rick";
        Map<String, Object> key = DSAUtils.initKey();
        Map<String, String> signatureData = DSAUtils.signatureData(text, DSAUtils.getPrivateKey(key));
        if (DSAUtils.checkSignature(signatureData, DSAUtils.getPublicKey(key))) {
            System.out.println("check signature success");
        } else {
            System.out.println("check signature failed");
        }

    }

    @Test
    public void test9() {
        long p = RotationUtils.genPrime();
        long q = RotationUtils.genPrime();
        long N = RotationUtils.genN(p, q);
        long rpk = RotationUtils.getRpk(RotationUtils.genfi(p, q));
        System.out.println("rpk:" + rpk);
        long rsk = RotationUtils.getRsk(rpk, RotationUtils.genfi(p, q));
        System.out.println("rpk:" + rsk);
        long cur = 148;
        for (int i = 0; i < 9; i++) {
            System.out.println(cur);
            cur = RotationUtils.BDri(rsk, cur, N);
        }
        System.out.println(cur);
        System.out.println();

        long[] result = RotationUtils.FDri(rpk, cur, 10, N);
        for (long item : result) {
            System.out.println(item);
        }
    }

    @Test
    public void test10() {
        Map<String, Object> keyMap1 = ElgamalUtils.initKey();
        Map<String, Object> keyMap2 = ElgamalUtils.initKey();
        System.out.println(ElgamalUtils.getPrivateKey(keyMap1));
        System.out.println();
        String info = ElgamalUtils.encryptByPublicKey(ElgamalUtils.getPrivateKey(keyMap1), ElgamalUtils.getPublicKey(keyMap2));
        System.out.println();
        System.out.println(info);
        System.out.println(ElgamalUtils.decryptByPrivateKey(info, ElgamalUtils.getPrivateKey(keyMap2)));
    }

    @Test
    public void test11() {
        String text = "fsdfsdfsdfsad.txt";
        System.out.println(text.substring(0, text.lastIndexOf(".")));
    }

    @Test
    public void test12() {
        String pv = "MHkCAQAwUAYGKw4HAgEBMEYCIQD4iMDIkbm8WLrXyKDHNcEZEuBdWRqFb729GEgPjw+AMwIhAJYjnFXlwQ3xSpHNFYHRs98FkH0xSTC/pgQMJCH0TqGQBCICIEVV5Lh46Br9g+hhnSdiSYSOos0ZmLo/eXUQiA8gbyzu";
        String pb = "MHcwUAYGKw4HAgEBMEYCIQD4iMDIkbm8WLrXyKDHNcEZEuBdWRqFb729GEgPjw+AMwIhAJYjnFXlwQ3xSpHNFYHRs98FkH0xSTC/pgQMJCH0TqGQAyMAAiBkTjmbrqN/pfn0hhVF8nneNHQdXtUIByUTcapUZhY79Q==";
        String text = "MHgCAQAwTwYGKw4HAgEBMEUCIQCm9Vlo1zvdLZH2XR9xyZlsekPDRxIPeeTqRlZI8Z3+LwIgbMNMiTLL3rT9NVLBPhAi4bq2JGsccX81uLlHQ1v6l8kEIgIgbmywUzmDYaKz9cyTDeiGK7Z8JvZeEn/e9ifnpKTrV4s=";
        System.out.println(pv);
        System.out.println(pb);
        String cipher = ElgamalUtils.encryptByPublicKey(text, pb);
        System.out.println(cipher);
        System.out.println(ElgamalUtils.decryptByPrivateKey(cipher, pv));
    }


    @Test
    public void ratationTest() {
        String text = "Hello Rick";
        String k1 = AESUtils.generateAESKey();

        CipherFK cipherFK = new CipherFK();
        cipherFK.setK0(k1);
        cipherFK.setkT(k1);
        cipherFK.setT(1);
        long p = RotationUtils.genPrime();
        long q = RotationUtils.genPrime();
        long rpk = RotationUtils.getRpk(RotationUtils.genfi(p, q));
        long rsk = RotationUtils.getRsk(rpk, RotationUtils.genfi(p, q));
        Map<String, Long> rotationKey = new HashMap<>();
        rotationKey.put("RPK", rpk);
        rotationKey.put("RSK", rsk);
        rotationKey.put("N", p * q);
        cipherFK.setRpk(String.valueOf(rotationKey.get("RPK")));
        cipherFK.setRsk(String.valueOf(rotationKey.get("RSK")));
        cipherFK.setN(rotationKey.get("N").intValue());
        
        System.out.println(GSON.toJson(cipherFK));
        String cipher1 = AESUtils.encryptAES(text, cipherFK.getK0());
        System.out.println();
        System.out.println();
        System.out.println(cipher1);

        Long next = RotationUtils.BDri(rsk, Long.parseLong(cipherFK.getkT()), cipherFK.getN());
        System.out.println(next);
        cipherFK.setkT(String.valueOf(next));
        // 安全模式：添加加密层，使密钥列表多一个密钥
        if (cipherFK.getT() < 20) {
            cipherFK.setT(cipherFK.getT() + 1);
        }
        System.out.println(GSON.toJson(cipherFK));
        String cipher2 = AESUtils.encryptAES(cipher1, String.valueOf(next));

        long[] keylist = RotationUtils.FDri(rpk, Long.parseLong(cipherFK.getkT()), 2, cipherFK.getN());
        for (long item : keylist) {
            System.out.println(item);
        }
        String plainText = "";
        for (int i = 1; i >= 0; i--) {
            try {
                plainText = AESUtils.decryptAES(cipher2, String.valueOf(keylist[i]));
                cipher2 = plainText;
            } catch (Exception e) {
            }
        }
        System.out.println(plainText);
    }
}
