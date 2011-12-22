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
package org.jenkinsci.plugins.karotz.eventhandler;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.karotz.KarotzException;
import org.jenkinsci.plugins.karotz.action.LedColor;
import org.jenkinsci.plugins.karotz.action.LedFadeAction;
import org.jenkinsci.plugins.karotz.action.LedLightAction;
import org.jenkinsci.plugins.karotz.action.LedOffAction;
import org.jenkinsci.plugins.karotz.action.SpeakAction;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * KarotzDefaultEventHandler.
 *
 * @author Seiji Sogabe
 */
public class KarotzDefaultEventHandler extends KarotzEventHandler {

    @DataBoundConstructor
    public KarotzDefaultEventHandler() {
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
        String tts = "The build ${BUILD_NUMBER} of project ${JOB_NAME} has started";
        new SpeakAction(tts).execute(build, listener);
    }

    /**
     * Triggered on build failure.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onFailure(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        String tts = "Failure of build ${BUILD_NUMBER} in project ${JOB_NAME}";
        for (int i = 5; i > 0; i--) {
            new LedOffAction().execute(build, listener);
            new LedLightAction(LedColor.RED).execute(build, listener);
        }
        new SpeakAction(tts).execute(build, listener);
    }

    /**
     * Triggered on build unstable.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onUnstable(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        String tts = "Project ${JOB_NAME} is unstable at build ${BUILD_NUMBER}";
        new LedLightAction(LedColor.YELLOW).execute(build, listener);
        new SpeakAction(tts).execute(build, listener);
    }

    /**
     * Triggered on build recover.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onRecover(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        String tts = "Project ${JOB_NAME} recovered at build ${BUILD_NUMBER}";
        new LedLightAction(LedColor.BLUE).execute(build, listener);
        new SpeakAction(tts).execute(build, listener);
    }

    /**
     * Triggered on build success.
     *
     * @param build The build in progress
     * @param listener build listener
     */
    @Override
    public void onSuccess(AbstractBuild<?, ?> build, BuildListener listener) throws KarotzException {
        String tts = "Success of build ${BUILD_NUMBER} in project ${JOB_NAMe}";
        for (int i = 5; i > 0; i--) {
            new LedOffAction().execute(build, listener);
            new LedLightAction(LedColor.BLUE).execute(build, listener);
        }
        new SpeakAction(tts).execute(build, listener);
    }

    @Extension
    public static class DescriptorImpl extends KarotzEventHandlerDescriptor {

        @Override
        public String getDisplayName() {
            return "Default EventHandler";
        }
    }
}
