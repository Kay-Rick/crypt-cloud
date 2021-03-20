package com.rick.cryptcloud;

import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.common.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class UtilsTest {

    private final String baseLocation = "/Users/rick/Desktop/Server/Crypt-Cloud/";


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

        long[]  result = RotationUtils.FDri(rpk, cur, 10, N);
        for (long item : result) {
            System.out.println(item);
        }
    }

    @Test
    public void test10() {
        Map<String, Object> keyMap1 =  ElgamalUtils.initKey();
        Map<String, Object> keyMap2 = ElgamalUtils.initKey();
        System.out.println(ElgamalUtils.getPrivateKey(keyMap1));
        System.out.println();
        String info = ElgamalUtils.encryptByPublicKey(ElgamalUtils.getPrivateKey(keyMap1), ElgamalUtils.getPublicKey(keyMap2));
        System.out.println();
        System.out.println(info);
        System.out.println(ElgamalUtils.decryptByPrivateKey(info, ElgamalUtils.getPrivateKey(keyMap2)));
    }
}
