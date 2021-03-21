package com.rick.cryptcloud.common;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class ElgamalUtils {

    public static final String KEY_ALGORITHM = "ElGamal";

    private static final int KEY_SIZE = 256;

    private static final int ENCRYPT_BLOCK_SIZE = 32;

    private static final int DECRYPT_BLOCK_SIZE = 88;

    private static final String PUBLIC_KEY = "ElGamalPublicKey";

    private static final String PRIVATE_KEY = "ElGamalPrivateKey";

    /**
     * 初始化公私钥对
     * @return
     */
    public static Map<String, Object> initKey() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance(KEY_ALGORITHM);
            apg.init(KEY_SIZE);
            AlgorithmParameters params = apg.generateParameters();
            DHParameterSpec elParams = params.getParameterSpec(DHParameterSpec.class);
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            kpg.initialize(elParams, new SecureRandom());
            KeyPair keyPair = kpg.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 使用公钥加密
     * @param data
     * @param key
     * @return
     */
    public static String encryptByPublicKey(String data, String key) {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = null;
        String cipherText = "";
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (data.length() <= ENCRYPT_BLOCK_SIZE) {
            try {
                return Base64.encodeBase64String(cipher.doFinal(data.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int blockNum = data.length() / ENCRYPT_BLOCK_SIZE + 1;
        String[] plainText = new String[blockNum];
        for (int i = 0; i < blockNum; i++) {
            if (i * ENCRYPT_BLOCK_SIZE + ENCRYPT_BLOCK_SIZE > data.length()) {
                plainText[i] = data.substring(i * ENCRYPT_BLOCK_SIZE);
            }
            else {
                plainText[i] = data.substring(i * ENCRYPT_BLOCK_SIZE, (i + 1) * ENCRYPT_BLOCK_SIZE);
            }
            try {
                cipherText += Base64.encodeBase64String(cipher.doFinal(plainText[i].getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cipherText;
    }


    /**
     * 使用私钥解密
     * @param data
     * @param key
     * @return
     */
    public static String decryptByPrivateKey(String data, String key) {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = null;
        String plainText = "";
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (data.length() <= DECRYPT_BLOCK_SIZE) {
            try {
                return new String(cipher.doFinal(Base64.decodeBase64(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int blockNum = data.length() / DECRYPT_BLOCK_SIZE;
        String[] cipherText = new String[blockNum];
        for (int i = 0; i < blockNum; i++) {
            if (i * DECRYPT_BLOCK_SIZE + DECRYPT_BLOCK_SIZE > data.length()) {
                cipherText[i] = data.substring(i * DECRYPT_BLOCK_SIZE);
            }
            else {
                cipherText[i] = data.substring(i * DECRYPT_BLOCK_SIZE, (i + 1) * DECRYPT_BLOCK_SIZE);
            }
            try {
                plainText += new String(cipher.doFinal(Base64.decodeBase64(cipherText[i])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plainText;
    }


    /**
     * 获得私钥
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获得公钥
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

}
