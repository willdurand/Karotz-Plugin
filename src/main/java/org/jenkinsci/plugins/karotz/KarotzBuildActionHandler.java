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
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.karotz.action.LedFadeAction;
import org.jenkinsci.plugins.karotz.action.LedLightAction;
import org.jenkinsci.plugins.karotz.action.LedOffAction;
import org.jenkinsci.plugins.karotz.action.SpeakAction;

/**
 * KarotzBuildActionHandler.
 *
 * @author Seiji Sogabe
 */
public class KarotzBuildActionHandler implements KarotzHandler {

    /**
     * Prepare the text to speak (tts) by replacing variables with their values.
     *
     * @param textToSpeak text to speak
     * @param build The build in progress
     * @return the text to speak by replaced vliables
     */
    private String prepareTTS(String textToSpeak, AbstractBuild<?, ?> build) {
        String projectName = build.getProject().getName();
        return StringUtils.replace(textToSpeak, "${projectName}", projectName);
    }

    /**
     * Triggered on build start.
     *
     * @param build The build in progress
     */
    @Override
    public void onStart(AbstractBuild<?, ?> build) throws KarotzException {
        String tts = prepareTTS("The project ${projectName} has started", build);
        new LedFadeAction(LedFadeAction.BLUE, 3000).execute();
        new SpeakAction(tts).execute();
    }

    /**
     * Triggered on build failure.
     *
     * @param build The build in progress
     */
    @Override
    public void onFailure(AbstractBuild<?, ?> build) throws KarotzException {
        String tts = prepareTTS("The project ${projectName} has failed", build);
        for (int i = 3; i > 0; i--) {
            new LedOffAction().execute();
            new LedLightAction(LedLightAction.RED).execute();
        }
        new SpeakAction(tts).execute();
    }

    /**
     * Triggered on build unstable.
     *
     * @param build The build in progress
     */
    @Override
    public void onUnstable(AbstractBuild<?, ?> build) throws KarotzException {
        String tts = prepareTTS("The project ${projectName} is unstable", build);
        new LedLightAction(LedLightAction.YELLOW).execute();
        new SpeakAction(tts).execute();
    }

    /**
     * Triggered on build recover.
     *
     * @param build The build in progress
     */
    @Override
    public void onRecover(AbstractBuild<?, ?> build) throws KarotzException {
        String tts = prepareTTS("The project ${projectName} is back to stable", build);
        new LedLightAction(LedLightAction.GREEN).execute();
        new SpeakAction(tts).execute();
    }

    /**
     * Triggered on build success.
     *
     * @param build The build in progress
     */
    @Override
    public void onSuccess(AbstractBuild<?, ?> build) throws KarotzException {
        String tts = prepareTTS("The project ${projectName} is ok", build);
        for (int i = 3; i > 0; i--) {
            new LedOffAction().execute();
            new LedLightAction(LedLightAction.GREEN).execute();
        }
        new SpeakAction(tts).execute();
    }

}
