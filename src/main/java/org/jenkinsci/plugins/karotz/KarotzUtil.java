/*
 * The MIT License
 *
 * Copyright (c) 2011, Seiji Sogabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.karotz;

import hudson.Util;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Utilitiy methods for Karotz.
 *
 * @author Seiji Sogabe
 */
public final class KarotzUtil {

    /**
     * COnstructor.
     */
    private KarotzUtil() {
        // do not use
    }

    /**
     * Creates HmacSha1.
     *
     * @param secretKey SecretKey
     * @param data target data
     * @return HmacSha1
     * @throws KarotzException Illegal encoding.
     */
    public static String doHmacSha1(String secretKey, String data) throws KarotzException {
        String hmacSha1;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes("ASCII"), "HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(data.getBytes("UTF-8"));
            hmacSha1 = new String(Base64.encodeBase64(digest), "ASCII");
        } catch (IllegalStateException e) {
            throw new KarotzException(e);
        } catch (InvalidKeyException e) {
            throw new KarotzException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new KarotzException(e);
        } catch (UnsupportedEncodingException e) {
            throw new KarotzException(e);
        }

        return hmacSha1;
    }

    /**
     * Builds query string.
     *
     * @param params key and value pairs.
     * @return  query string.
     */
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

    private static final Logger LOGGER = Logger.getLogger(KarotzUtil.class.getName());
}
