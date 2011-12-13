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

import hudson.ProxyConfiguration;
import hudson.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

    /**
     * Sends cmd to Karotz using ReST.
     *
     * @param url Karotz webAPI URL
     * @return response
     * @throws KarotzException Network or karotz trouble.
     */
    public static String doRequest(String url) throws KarotzException {
        if (url == null) {
            throw new KarotzException("url is null");
        }

        String result;
        try {
            URLConnection connection = ProxyConfiguration.open(new URL(url));
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            result = IOUtils.toString(inputStream);
            LOGGER.log(Level.INFO, "result is {0}", result);
        } catch (IOException e) {
            throw new KarotzException(e);
        }

        return result;
    }

    /**
     * Parses response from karotz.
     *
     * @param response response from karotz
     * @param tagName
     * @return tag value
     * @throws KarotzException illega response
     */
    public static String parseResponse(String response, String tagName) throws KarotzException {
        if (response == null || tagName == null) {
            throw new IllegalArgumentException("params should not be null.");
        }

        String value;
        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(new InputSource(new StringReader(response)));
            Element elt = (Element) document.getElementsByTagName(tagName).item(0);
            if (elt == null) {
                return null;
            }
            value = elt.getTextContent();
        } catch (SAXException e) {
            throw new KarotzException(e);
        } catch (ParserConfigurationException e) {
            throw new KarotzException(e);
        } catch (IOException e) {
            throw new KarotzException(e);
        }

        return value;
    }

    private static final Logger LOGGER = Logger.getLogger(KarotzUtil.class.getName());
}
