package voxxrin2.utils;

import com.google.common.base.Charsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

public class HMACSHA1 {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static byte[] generate(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(Charsets.UTF_8), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(data.getBytes(Charsets.UTF_8));
    }
}
