package org.jenkinsci.plugins.karotz;

import hudson.ProxyConfiguration;
import hudson.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * KarotzClient class.
 *
 * @author William Durand <william.durand1@gmail.com>
 */
public class KarotzClient {

    /**
     * Base URL for the START method (auth)
     */
    private static final String KAROTZ_URL_START = "http://api.karotz.com/api/karotz/start";

    /**
     * Base URL for the Interactive mode method
     */
    private static final String KAROTZ_URL_INTERACTIVE_MODE = "http://api.karotz.com/api/karotz/interactivemode";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(KarotzClient.class.getName());

    /**
     * Interactive Id
     */
    private static String interactiveId;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API Secret
     */
    private String secretKey;

    /**
     * Install Id
     */
    private String installId;

    /**
     * Default constructor
     *
     * @param apiKey application APIKey
     * @param secretKey application SecretKey
     * @param installId application Install ID
     */
    public KarotzClient(String apiKey, String secretKey, String installId) {
        this.installId = installId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getInteractiveId() {
        return interactiveId;
    }

    public boolean isInteractive() {
        return interactiveId != null;
    }

    public synchronized void startInteractiveMode() throws KarotzException {
        if (isInteractive()) {
            return;
        }
        Random random = new Random();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("apikey", apiKey);
        parameters.put("installid", installId);
        parameters.put("once", String.valueOf(random.nextInt(99999999)));
        // See: http://stackoverflow.com/questions/732034/getting-unixtime-in-java
        parameters.put("timestamp", String.valueOf((int) (System.currentTimeMillis() / 1000L)));
        String url = getSignedUrl(parameters, secretKey);

        String result = doRequest(url);
        LOGGER.log(Level.INFO, "Got: {0}", result);

        interactiveId = parseResponse(result, "interactiveId");
        if (interactiveId == null) {
            String code = parseResponse(result, "code");
            throw new KarotzException("[code] " + code);
        }
    }

    public synchronized void stopInteractiveMode() throws KarotzException {
        if (!isInteractive()) {
            return;
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action", "stop");
        parameters.put("interactiveid", interactiveId);

        String url = KAROTZ_URL_INTERACTIVE_MODE + '?' + KarotzUtil.buildQuery(parameters);

        String result = doRequest(url);
        String code = parseResponse(result, "code");
        if (!"OK".equalsIgnoreCase(code) && !"NOT_CONNECTED".equalsIgnoreCase(code)) {
            throw new KarotzException("[code] " + code);
        }

        interactiveId = null;
    }

    /**
     * Sends cmd to Karotz using ReST.
     *
     * @param url Karotz webAPI URL
     * @return response
     * @throws KarotzException Network or karotz trouble.
     */
    public String doRequest(String url) throws KarotzException {
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
    public String parseResponse(String response, String tagName) throws KarotzException {
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

    private String getSignedUrl(Map<String, String> params, String secretKey) throws KarotzException {
        String q = KarotzUtil.buildQuery(params);
        String signedQuery = KarotzUtil.doHmacSha1(secretKey, q);
        LOGGER.log(Level.INFO, "singedQuery: [{0}]", signedQuery);
        return String.format("%s?%s&signature=%s", KAROTZ_URL_START, q, Util.rawEncode(signedQuery));
    }
}
