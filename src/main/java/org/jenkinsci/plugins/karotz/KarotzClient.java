package org.jenkinsci.plugins.karotz;

import hudson.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final String KAROTZ_URL_INTERACTIVE_MODE = "http://api.karotz.com/api/karotz/interactivemode";

    /**
     * Base URL for the TTS function
     */
    private static final String KAROTZ_URL_TTS = "http://api.karotz.com/api/karotz/tts";

    /**
     * Base URL for the LED function
     */
    private static final String KAROTZ_URL_LED = "http://api.karotz.com/api/karotz/led";

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

    private boolean isInteractive() {
        return interactiveId != null;
    }

    /**
     * Speak API.
     *
     * @param textToSpeak
     * @param language
     */
    public void speak(String textToSpeak, String language) throws KarotzException {
        if (!isInteractive()) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "speak");
        params.put("lang", language);
        params.put("text", textToSpeak);
        params.put("interactiveid", interactiveId);
        String url = KAROTZ_URL_TTS + '?' + KarotzUtil.buildQuery(params);

        String result = KarotzUtil.doRequest(url);
        String code = KarotzUtil.parseResponse(result, "code");
        if ("OK".equalsIgnoreCase(code)) {
            throw new KarotzException("failed to speak code: " + code);
        }
    }

    public void pulse(String color, int period, int pulse) throws KarotzException {
        if (!isInteractive()) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "pulse");
        params.put("color", color);
        params.put("period", String.valueOf(period));
        params.put("pulse", String.valueOf(pulse));
        params.put("interactiveid", interactiveId);
        String url = KAROTZ_URL_LED + '?' + KarotzUtil.buildQuery(params);

        String result = KarotzUtil.doRequest(url);
        String code = KarotzUtil.parseResponse(result, "code");
        if ("OK".equalsIgnoreCase(code)) {
            throw new KarotzException("failed to speak code: " + code);
        }
    }

    public void light(String color) throws KarotzException {
        if (!isInteractive()) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "light");
        params.put("color", color);
        params.put("interactiveid", interactiveId);
        String url = KAROTZ_URL_LED + '?' + KarotzUtil.buildQuery(params);

        String result = KarotzUtil.doRequest(url);
        String code = KarotzUtil.parseResponse(result, "code");
        if ("OK".equalsIgnoreCase(code)) {
            throw new KarotzException("failed to speak code: " + code);
        }
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

        String result = KarotzUtil.doRequest(url);
        LOGGER.log(Level.INFO, "Got: {0}", result);

        interactiveId = KarotzUtil.parseResponse(result, "interactiveId");
        if (interactiveId == null) {
            String code = KarotzUtil.parseResponse(result, "code");
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

        String result = KarotzUtil.doRequest(url);
        String code = KarotzUtil.parseResponse(result, "code");
        if (!"OK".equalsIgnoreCase(code) && !"NOT_CONNECTED".equalsIgnoreCase(code)) {
            throw new KarotzException("[code] " + code);
        }

        interactiveId = null;
    }

    private String getSignedUrl(Map<String, String> params, String secretKey) throws KarotzException {
        String q = KarotzUtil.buildQuery(params);
        String signedQuery = KarotzUtil.doHmacSha1(secretKey, q);
        LOGGER.log(Level.INFO, "singedQuery: [{0}]", signedQuery);
        return String.format("%s?%s&signature=%s", KAROTZ_URL_START, q, Util.rawEncode(signedQuery));
    }
}
