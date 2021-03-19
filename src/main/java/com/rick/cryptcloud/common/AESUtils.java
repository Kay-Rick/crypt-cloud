package com.rick.cryptcloud.common;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESUtils {

    private static final Random random = new Random();

    private static final String ALGORITHM = "AES";

    private static final String ALGORITHM_MODE = "AES/ECB/PKCS5Padding";

    private static final int N = 1000;

    public static String generateAESKey() {
        int key = random.nextInt(N);
        return String.valueOf(key);
    }

    /**
     * 加密
     * @param input
     * @param key
     * @return
     */
    public static String encryptAES(String input, String key){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE);
            byte[] keys = new byte[16];
            System.arraycopy(key.getBytes(), 0, keys, 0, key.getBytes().length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.encodeBase64String(cipher.doFinal(input.getBytes())) ;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * @param encryptDES
     * @param key
     * @return
     */
    public static String decryptAES(String encryptDES, String key){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE);
            byte[] keys = new byte[16];
            System.arraycopy(key.getBytes(), 0, keys, 0, key.getBytes().length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String (cipher.doFinal(Base64.decodeBase64(encryptDES)));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
