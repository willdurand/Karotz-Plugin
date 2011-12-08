package org.jenkinsci.plugins.karotz;

import hudson.Util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author sogabe
 */
public class KarotzUtil {

    private KarotzUtil() {
        // do not use
    }

    public static String doHmacSha1(String secretKey, String data)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes("ASCII"), "HmacSHA1");

        mac.init(secret);
        byte[] digest = mac.doFinal(data.getBytes("UTF-8"));

        return new String(Base64.encodeBase64(digest), "ASCII");
    }

    public static String buildQuery(Map<String, String> params) {
        if (params == null) {
            return "";
        }
        
        SortedMap<String, String> sortedParams = new TreeMap<String, String>(params);
        Iterator<Map.Entry<String, String>> iter = sortedParams.entrySet().iterator();

        StringBuilder buffer = new StringBuilder();

        Map.Entry<String, String> next = iter.next();
        buffer.append(next.getKey()).append("=").append(Util.rawEncode(next.getValue()));
        while (iter.hasNext()) {
            next = iter.next();
            buffer.append("&").append(next.getKey()).append("=").append(Util.rawEncode(next.getValue()));
        }

        return buffer.toString();
    }
}
