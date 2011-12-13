package org.jenkinsci.plugins.karotz.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Led Light Action.
 *
 * @author Seiji Sogabe
 */
public class LedLightAction extends KarotzAction {

    private String color;

    public LedLightAction(String color) {
        this.color = color;
    }

    public String getBaseUrl() {
        return "http://api.karotz.com/api/karotz/led";
    }

    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "light");
        params.put("color", color);
        return params;
    }
}
