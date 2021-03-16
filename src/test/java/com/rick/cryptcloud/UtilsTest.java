package com.rick.cryptcloud;

import com.rick.cryptcloud.common.*;
import com.rick.cryptcloud.domain.User;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class UtilsTest {

    private final String baseLocation = "D:\\Server\\Crypt-Cloud\\";

    @Test
    public void test1() {
        User user = new User();
        user.setAge(20);
        user.setId(1005);
        user.setName("Rick");
        SerializationUtils<User> serializationUtils = new SerializationUtils<>(baseLocation + "user.txt");
        serializationUtils.saveObjToFile(user);
        User recover =  serializationUtils.getObjToFile();
        System.out.println(recover);
    }

    @Test
    public void test2() {
        String content = "Hello Rick";
        try {
            FileUtils.writeStringToFile(new File(baseLocation + "download.txt"), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        String downloadFileContent = "";
        try {
            downloadFileContent = FileUtils.readFileToString(new File(baseLocation + "download.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(downloadFileContent);
    }

    @Test
    public void test4() {
        String key1 = "123";
        String key2 = "456";
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
        Map<String, String> signatureData = DSAUtils.SignatureData(text, DSAUtils.getPrivateKey(key));
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
}
