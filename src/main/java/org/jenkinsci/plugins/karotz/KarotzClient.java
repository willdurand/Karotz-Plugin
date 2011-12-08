package org.jenkinsci.plugins.karotz;

import hudson.ProxyConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * KarotzClient class.
 *
 * @author William Durand <william.durand1@gmail.com>
 */
public class KarotzClient {

    /**
     * Base URL for the START method (auth)
     */
    private final static String KAROTZ_URL_START = "http://api.karotz.com/api/karotz/start";

    /**
     * Base URL for the TTS function
     */
    private final static String KAROTZ_URL_TTS = "http://api.karotz.com/api/karotz/tts";

    /**
     * Base URL for the LED function
     */
    private final static String KAROTZ_URL_LED = "http://api.karotz.com/api/karotz/led";

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
     */
    public KarotzClient(String apiKey, String secretKey, String installId) {
        this.installId = installId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    /**
     * @return String
     */
    public String getInstallId() {
        return installId;
    }

    private boolean isInteractive() {
        return interactiveId != null;
    }

    /**
     * @param textToSpeak
     * @param language
     * @return
     */
    public boolean speak(String textToSpeak, String language) {
        if (!isInteractive()) {
            return false;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "speak");
        params.put("lang", language);
        params.put("text", textToSpeak);
        params.put("interactiveid", interactiveId);
        String url = KAROTZ_URL_TTS + '?' + KarotzUtil.buildQuery(params);
        return doRequest(url);
    }

    /**
     *
     * @param color
     * @return
     */
    public boolean colorize(String color) {
        if (!isInteractive()) {
            return false;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "pulse");
        params.put("color", color);
        params.put("period", "3000");
        params.put("interactiveid", interactiveId);
        String url = KAROTZ_URL_LED + '?' + KarotzUtil.buildQuery(params);
        return doRequest(url);
    }

    /**
     * @see http://dev.karotz.com/api/signed.html
     */
    public synchronized void startSession() {
        if (interactiveId != null) {
            return;
        }
        Random random = new Random();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("apikey", apiKey);
        parameters.put("installid", getInstallId());
        parameters.put("once", String.valueOf(random.nextInt(99999999)));
        // See: http://stackoverflow.com/questions/732034/getting-unixtime-in-java
        parameters.put("timestamp", String.valueOf((int) (System.currentTimeMillis() / 1000L)));

        String url = null;
        try {
            url = getSignedUrl(parameters, secretKey);
            LOGGER.log(Level.INFO, "URL: {0}", url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception catched: {0}", e.getMessage());
            interactiveId = null;
        }

        URLConnection cnx;
        InputStream inputStream;

        String result = null;

        try {
            cnx = ProxyConfiguration.open(new URL(url));
            cnx.connect();
            inputStream = cnx.getInputStream();
            result = IOUtils.toString(inputStream);
            LOGGER.log(Level.INFO, "Got: {0}", result);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception catched: {0}", e.toString());
            interactiveId = null;
        }

        interactiveId = parseXML(result);
    }

    protected String parseXML(String xml) {
        String value = null;

        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(new InputSource(new StringReader(xml)));
            Element elt = (Element) document.getElementsByTagName("interactiveId").item(0);

            value = elt.getTextContent();

            LOGGER.log(Level.INFO, "InteractiveId: {0}", value);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse: {0}", xml);
        }

        return value;
    }

    /**
     * @param String url The URL to call.
     * @return boolean
     * <code>false</code> if the request has failed,
     * <code>true</code> otherwise.
     */
    protected boolean doRequest(String url) {
        URLConnection connection;

        try {
            connection = ProxyConfiguration.open(new URL(url));
        } catch (Exception e) {
            interactiveId = null;
            LOGGER.severe(e.getMessage());
            return false;
        }

        try {
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream);
            LOGGER.log(Level.INFO, "Connect to {0}", url);
            LOGGER.log(Level.INFO, "result is {0}", result);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            interactiveId = null;
            return false;
        }

        return true;
    }

    private String getSignedUrl(Map<String, String> params, String secretKey) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String q = KarotzUtil.buildQuery(params);
        String signedQuery = KarotzUtil.doHmacSha1(secretKey, q);
        LOGGER.log(Level.INFO, "singedQuery: [{0}]", signedQuery);
        return String.format("%s?%s&signature=%s", KAROTZ_URL_START, q, URLEncoder.encode(signedQuery, "UTF-8"));
    }
}
