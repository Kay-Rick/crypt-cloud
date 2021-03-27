package com.rick.cryptcloud.common.utils;

import org.apache.commons.codec.binary.Base64;


import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class DSAUtils {

    private static final String ALGORITHM = "DSA";

    private static final String PUBLIC_KEY = "DSAPublicKey";

    private static final String PRIVATE_KEY = "DSAPrivateKey";

    private static final String SIGNATURE = "DSASignature";

    private static final String TEXT = "DSAText";

    /**
     * 初始化公私钥对
     * 
     * @return
     */
    public static Map<String, Object> initKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得私钥
     * 
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        PrivateKey key = (PrivateKey) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获得公钥
     * 
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        PublicKey key = (PublicKey)keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取签名
     * 
     * @param info
     * @return
     */
    public static String getSignature(Map<String, String> info) {
        return info.get(SIGNATURE);
    }

    /**
     * 使用私钥签名
     * 
     * @param info
     * @param privateKey
     * @return
     */
    public static Map<String, String> signatureData(String info, String privateKey) {
        try {
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initSign(byteArrayToPrivateKey(Base64.decodeBase64(privateKey)));
            signature.update(info.getBytes());
            Map<String, String> textMap = new HashMap<>();
            textMap.put(TEXT, info);
            textMap.put(SIGNATURE, Base64.encodeBase64String(signature.sign()));
            return textMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用公钥校验签名
     * 
     * @param info
     * @param publicKey
     * @return
     */
    public static boolean checkSignature(Map<String, String> info, String publicKey) {
        try {
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(byteArrayToPublicKey(Base64.decodeBase64(publicKey)));
            signature.update(info.get(TEXT).getBytes());
            if (signature.verify(Base64.decodeBase64(info.get(SIGNATURE)))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static PublicKey byteArrayToPublicKey(byte[] bytes) {
        PublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    private static PrivateKey byteArrayToPrivateKey(byte[] bytes) {
        PrivateKey privateKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }
}
