package fr.willdurand.jenkins.karotz;

import hudson.ProxyConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.codec.binary.Base64;
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
    private static String interactiveId = null;

    /**
     * API Key
     */
    private String APIKey;
    
    /**
     * API Secret
     */
    private String secretKey;

    
    /**
     * Install Id
     */
    private String installId = null;

    /**
     * Default constructor
     */
    public KarotzClient(String APIKey, String secretKey, String installId) {
        this.installId = installId;
        this.APIKey = APIKey;
        this.secretKey = secretKey;
    }

    /**
     * @return String
     */
    public String getInstallId() {
        return installId;
    }

    /**
     * @return String
     */
    public String getInteractiveId() {
        if (null == KarotzClient.interactiveId) {
            this.requestInteractiveId();
        }
        return KarotzClient.interactiveId;
    }

    /**
     * @param textToSpeak
     * @param language
     * @return
     */
    public boolean speak(String textToSpeak, String language) {
        textToSpeak = textToSpeak.replace(" ", "+");
        String url = KAROTZ_URL_TTS + "?action=speak&lang=" + language + "&text="
                + textToSpeak + "&interactiveid=" + this.getInteractiveId();

        return this.doRequest(url);
    }

    /**
     *
     * @param color
     * @return
     */
    public boolean colorize(String color) {
        String url = KAROTZ_URL_LED + "?action=pulse&color=" + color
                + "&period=3000&pulse=500&interactiveid=" + this.getInteractiveId();

        return this.doRequest(url);
    }

    /**
     * @see http://dev.karotz.com/api/signed.html
     */
    protected void requestInteractiveId() {
        Random random = new Random();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("apikey", APIKey);
        parameters.put("installid", getInstallId());
        parameters.put("once", String.valueOf(random.nextInt(99999999)));
        // See: http://stackoverflow.com/questions/732034/getting-unixtime-in-java
        parameters.put("timestamp", String.valueOf((int) (System.currentTimeMillis() / 1000L)));

        String url = null;
        try {
            url = this.getSignedUrl(parameters, secretKey);
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

        KarotzClient.interactiveId = this.parseXML(result);
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
     * @param String url	The URL to call.
     * @return boolean
     * <code>false</code> if the request has failed,
     * <code>true</code> otherwise.
     */
    protected boolean doRequest(String url) {
        URLConnection connection;

        try {
            connection = ProxyConfiguration.open(new URL(url));
        } catch (Exception e) {
            KarotzClient.interactiveId = null;
            LOGGER.severe(e.getMessage());
            return false;
        }

        try {
            connection.connect();
            connection.getInputStream();
            LOGGER.log(Level.INFO, "Connect to {0}", url);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            KarotzClient.interactiveId = null;
            return false;
        }

        return true;
    }

    /**
     */
    protected String hmacSha1(String data, String secretKey)
            throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");

        mac.init(secret);
        byte[] digest = mac.doFinal(data.getBytes());

        return URLEncoder.encode(Base64.encodeBase64String(digest), "UTF-8").replace("%0D%0A", "");
    }

    /**
     */
    protected String getSignedUrl(Map<String, String> params, String secretKey)
            throws Exception {
        SortedMap<String, String> sortedParams = new TreeMap<String, String>(params);
        Iterator<Map.Entry<String, String>> iter = sortedParams.entrySet().iterator();

        Map.Entry<String, String> next = iter.next();
        StringBuilder buffer = new StringBuilder(next.getKey()).append("=").append(next.getValue());

        while (iter.hasNext()) {
            next = iter.next();
            buffer.append("&").append(next.getKey()).append("=").append(next.getValue());
        }

        String signedQuery = this.hmacSha1(buffer.toString(), secretKey);
        return KAROTZ_URL_START + "?" + buffer.toString() + "&signature=" + signedQuery;
    }
}
