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
package org.jenkinsci.plugins.karotz.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Led Light Action.
 *
 * @author Seiji Sogabe
 */
public class LedFadeAction extends KarotzAction {

    public static String RED = "FF0000";

    public static String BLUE = "0000FF";

    public static String YELLOW = "FFFF00";

    public static String GREEN = "00FF00";

    private String color;

    private long period;

    public LedFadeAction(String color, long period) {
        this.color = color;
        this.period = period;
    }

    public String getBaseUrl() {
        return "http://api.karotz.com/api/karotz/led";
    }

    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "fade");
        params.put("color", color);
        params.put("period", String.valueOf(period));
        return params;
    }
}
