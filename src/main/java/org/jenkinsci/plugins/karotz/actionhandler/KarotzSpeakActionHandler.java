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
package org.jenkinsci.plugins.karotz.actionhandler;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.karotz.KarotzException;
import org.jenkinsci.plugins.karotz.action.LedColor;
import org.jenkinsci.plugins.karotz.action.LedFadeAction;
import org.jenkinsci.plugins.karotz.action.LedLightAction;
import org.jenkinsci.plugins.karotz.action.LedOffAction;
import org.jenkinsci.plugins.karotz.action.SpeakAction;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * KarotzSpeakActionHandler.
 *
 * @author Seiji Sogabe
 */
public class KarotzSpeakActionHandler extends KarotzActionHandler {

    private static final String START_TEXT = "The build ${BUILD_NUMBER} of project ${JOB_NAME} has started";

    private static final String SUCCESS_TEXT = "Success of build ${BUILD_NUMBER} in project ${JOB_NAME}";

    private static final String FAILURE_TEXT = "Failure of build ${BUILD_NUMBER} in project ${JOB_NAME}";

    private static final String UNSTABLE_TEXT = "Project ${JOB_NAME} is unstable at build ${BUILD_NUMBER}";

    private static final String RECOVER_TEXT = "Project ${JOB_NAME} recovered at build ${BUILD_NUMBER}";

    /**
     * the language of the text to say
     */
    private String lang;

    /**
     * Text to speak when the build has started.
     */
    private String startText;

    /**
     * Text to speak for the build is successful.
     */
    private String successText;

    /**
     * Text to speak when the build has unfortunatelly failed.
     */
    private String failureText;

    /**
     * Text to speak when the build is unstable.
     */
    private String unstableText;

    /**
     * Text to speak when the build has recover success.
     */
    private String recoverText;

    /**
     * Gets the language of the text to say.
     *
     * @return language
     */
    public String getLang() {
        return lang;
    }

    /**
     * Gets the text to speak when the build has started.
     *
     * @return the text
     */
    public String getStartText() {
        return startText;
    }

    /**
     * Gets the text to speak when the build is successfull.
     *
     * @return the text
     */
    public String getSuccessText() {
        return successText;
    }

    /**
     * Gets the text to speak when the build is failed.
     *
     * @return the text
     */
    public String getFailureText() {
        return failureText;
    }

    /**
     * Gets the text to speak when the build is unstable.
     *
     * @return the text
     */
    public String getUnstableText() {
        return unstableText;
    }

    /**
     * Gets the text to speak when the build has recover success.
     *
     * @return the text
     */
    public String getRecoverText() {
        return recoverText;
    }

    @DataBoundConstructor
    public KarotzSpeakActionHandler(String lang, String startText, String successText,
            String failureText, String unstableText, String recoverText) {
        this.lang = lang;
        this.startText = Util.fixEmptyAndTrim(startText) != null ? startText : START_TEXT;
        this.successText = Util.fixEmptyAndTrim(successText) != null ? successText : SUCCESS_TEXT;
        this.failureText = Util.fixEmptyAndTrim(failureText) != null ? failureText : FAILURE_TEXT;
        this.unstableText = Util.fixEmptyAndTrim(unstableText) != null ? unstableText : UNSTABLE_TEXT;
        this.recoverText = Util.fixEmptyAndTrim(recoverText) != null ? recoverText : RECOVER_TEXT;
    }

    /**
     * Triggered on build start.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onStart(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        new LedFadeAction(LedColor.GREEN, 3000).execute(build, listener);
        new SpeakAction(getStartText(), getLang()).execute(build, listener);
    }

    /**
     * Triggered on build failure.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onFailure(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        for (int i = 5; i > 0; i--) {
            new LedOffAction().execute(build, listener);
            new LedLightAction(LedColor.RED).execute(build, listener);
        }
        new SpeakAction(getFailureText(), getLang()).execute(build, listener);
    }

    /**
     * Triggered on build unstable.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onUnstable(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        new LedLightAction(LedColor.YELLOW).execute(build, listener);
        new SpeakAction(getUnstableText(), getLang()).execute(build, listener);
    }

    /**
     * Triggered on build recover.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onRecover(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        new LedLightAction(LedColor.BLUE).execute(build, listener);
        new SpeakAction(getRecoverText(), getLang()).execute(build, listener);
    }

    /**
     * Triggered on build success.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onSuccess(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        for (int i = 5; i > 0; i--) {
            new LedOffAction().execute(build, listener);
            new LedLightAction(LedColor.BLUE).execute(build, listener);
        }
        new SpeakAction(getSuccessText(), getLang()).execute(build, listener);
    }

    @Extension
    public static class DescriptorImpl extends KarotzActionHandlerDescriptor {

        @Override
        public String getDisplayName() {
            return "Customizable Speak ActionHandler";
        }

        public ListBoxModel doFillLangItems() {
            ListBoxModel model = new ListBoxModel();
            model.add("English", "EN");
            model.add("French", "FR");
            model.add("Deutch", "DE");
            model.add("Spanish", "ES");
            return model;
        }
    }
}
