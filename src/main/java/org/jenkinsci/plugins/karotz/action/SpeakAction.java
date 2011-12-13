package org.jenkinsci.plugins.karotz.action;

import java.util.HashMap;
import java.util.Map;

/**
 * SpeakAction.
 *
 * @author Seiji Sogabe
 */
public class SpeakAction extends KarotzAction {

    private String textToSpeak;

    private String language;

    public SpeakAction(String textToSpeak, String language) {
        this.textToSpeak = textToSpeak;
        this.language = language;
    }

    public String getBaseUrl() {
        return "http://api.karotz.com/api/karotz/tts";
    }

    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "speak");
        params.put("lang", language);
        params.put("text", textToSpeak);
        return params;
    }
}
