package fml.plus.auth.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;

@Slf4j
public class RSAUtils {
    private RSAUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Charset DEF_CHARSET = StandardCharsets.UTF_8;
    private static final SignAlgorithm SIGN_ALGORITHM = SignAlgorithm.SHA256withRSA;

    /**
     * RSA使用公钥加密
     */
    public static String ecrypt(String plainText, String publicKey) {
        try {
            var rsa = new RSA(null, publicKey);
            var encryVal = rsa.encrypt(plainText.getBytes(DEF_CHARSET), KeyType.PublicKey);
            return Base64.encode(encryVal);
        } catch (Exception e) {
            log.debug("RSA加密失败:" + e.getMessage());
            return null;
        }
    }

    /**
     * RSA使用私钥解密
     */
    public static String decrypt(String encryptedString, String privateKey) {
        try {
            var rsa = new RSA(privateKey, null);
            var dencryVal = rsa.decrypt(Base64.decode(encryptedString), KeyType.PrivateKey);
            return new String(dencryVal, DEF_CHARSET);
        } catch (Exception e) {
            log.debug("RSA解密失败:" + e.getMessage());
            return null;
        }
    }

    /**
     * 生成RSA公私钥对
     */
    public static KeyPair generateKeyPair(int keySize) {
        return KeyUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), keySize);
    }

    /**
     * 获得公钥/私钥内容
     */
    public static String getEncodedKey(Key key) {
        return Base64.encode(key.getEncoded());
    }

    /**
     * SHA256 使用私钥签名
     */
    public static String sign(String content, String privateKey) {
        try {
            var sign = SecureUtil.sign(SIGN_ALGORITHM, privateKey, null);
            byte[] signed = sign.sign(StrUtil.bytes(content, DEF_CHARSET));
            return Base64.encode(signed);
        } catch (Exception e) {
            log.debug("签名失败: "+e.getMessage());
            return null;
        }
    }

    /**
     * SHA256 公钥验证签名
     */
    public static boolean verify(String content, String signStr, String publicKey) {
        if(!Base64.isBase64(signStr) ||!Base64.isBase64(content)) return false;
        try {
            var decodeSign = Base64.decode(signStr);
            var sign = SecureUtil.sign(SIGN_ALGORITHM, null, publicKey);
            return sign.verify(StrUtil.bytes(content, DEF_CHARSET), decodeSign);
        } catch (Exception e) {
            log.debug("校验签名失败:"+ e.getMessage());
            return false;
        }
    }

}
