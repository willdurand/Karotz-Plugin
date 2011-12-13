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

import hudson.model.AbstractBuild;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * KarotzBuildActionHandler.
 *
 * @author Seiji Sogabe
 */
public class KarotzBuildActionHandler implements KarotzHandler {

    private final KarotzClient client;

    /**
     * Constructor.
     *
     * @param build build
     * @param client {@link KarotzClient}
     */
    public KarotzBuildActionHandler(KarotzClient client) {
        if (client == null) {
            throw new IllegalArgumentException("client should not be null.");
        }
        this.client = client;
    }

    /**
     * Prepare the text to speak (tts) by replacing variables with their values.
     *
     * @param textToSpeak text to speak
     * @return the text to speak by replaced vliables
     */
    private String prepareTTS(String textToSpeak, AbstractBuild<?, ?> build) {
        String projectName = build.getProject().getName();
        return StringUtils.replace(textToSpeak, "${projectName}", projectName);
    }

    @Override
    public void onStart(AbstractBuild<?, ?> build) {
        String tts = prepareTTS("The project ${projectName} has started", build);
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build failure.
     */
    @Override
    public void onFailure(AbstractBuild<?, ?> build) {
        String tts = prepareTTS("The project ${projectName} has failed", build);
        try {
            client.light("FF0000");
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build unstable.
     */
    @Override
    public void onUnstable(AbstractBuild<?, ?> build) {
        String tts = prepareTTS("The project ${projectName} is unstable", build);
        try {
            client.light("FFFF00");
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build recover.
     */
    @Override
    public void onRecover(AbstractBuild<?, ?> build) {
        String tts = prepareTTS("The project ${projectName} is back to stable", build);
        try {
            client.light("0000FF");
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build success.
     */
    @Override
    public void onSuccess(AbstractBuild<?, ?> build) {
        String tts = prepareTTS("The project ${projectName} is ok", build);
        try {
            client.light("0000FF");
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(KarotzBuildActionHandler.class.getName());
}
