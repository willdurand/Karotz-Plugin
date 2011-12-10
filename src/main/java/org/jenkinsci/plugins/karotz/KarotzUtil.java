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
 *
 * @author sogabe
 */
public final class KarotzUtil {

    private KarotzUtil() {
        // do not use
    }

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

    public static String parseXML(String xml, String tagName) throws KarotzException {
        if (xml == null || tagName == null) {
            throw new IllegalArgumentException("params should not be null.");
        }

        String value;
        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(new InputSource(new StringReader(xml)));
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
