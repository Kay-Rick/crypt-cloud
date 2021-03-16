package com.rick.cryptcloud.common;

import org.apache.commons.codec.binary.Base64;

import java.security.*;
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
     * @param keyMap
     * @return
     */
    public static PrivateKey getPrivateKey(Map<String, Object> keyMap) {
        return (PrivateKey) keyMap.get(PRIVATE_KEY);
    }

    /**
     * 获得公钥
     * @param keyMap
     * @return
     */
    public static PublicKey getPublicKey(Map<String, Object> keyMap) {
        return (PublicKey) keyMap.get(PUBLIC_KEY);
    }

    /**
     * 获取签名
     * @param info
     * @return
     */
    public static String getSignature(Map<String, String> info) {
        return info.get(SIGNATURE);
    }

    /**
     * 使用私钥签名
     * @param info
     * @param privateKey
     * @return
     */
    public static Map<String, String> SignatureData (String info, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initSign(privateKey);
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
     * @param info
     * @param publicKey
     * @return
     */
    public static boolean checkSignature(Map<String, String> info, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(publicKey);
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
}
