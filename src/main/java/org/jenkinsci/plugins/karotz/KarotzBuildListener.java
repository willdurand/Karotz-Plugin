package org.jenkinsci.plugins.karotz;

import hudson.model.AbstractBuild;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * KarotzBuildListener
 *
 * @author Seiji Sogabe
 */
public class KarotzBuildListener {

    private final AbstractBuild<?, ?> build;

    private final String projectName;

    private final KarotzClient client;

    /**
     * Constructor.
     *
     * @param build build
     * @param client {@link KarotzClient}
     */
    public KarotzBuildListener(AbstractBuild<?, ?> build, KarotzClient client) {
        if (build == null || client == null) {
            throw new IllegalArgumentException("build and client should not be null.");
        }
        this.build = build;
        this.client = client;
        this.projectName = build.getProject().getName();
    }

    /**
     * Prepare the text to speak (tts) by replacing variables with their values.
     *
     * @param textToSpeak text to speak
     * @return the text to speak by replaced vliables
     */
    private String prepareTTS(String textToSpeak) {
        return StringUtils.replace(textToSpeak, "${projectName}", projectName);
    }

    public void onStart() {
        String tts = prepareTTS("The project ${projectName} has started");
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build failure.
     */
    public void onFailure() {
        String tts = prepareTTS("The project ${projectName} has failed");
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build unstable.
     */
    public void onUnstable() {
        String tts = prepareTTS("The project ${projectName} is unstable");
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build recover.
     */
    public void onRecover() {
        String tts = prepareTTS("The project ${projectName} is back to stable");
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build success.
     */
    public void onSuccess() {
        String tts = prepareTTS("The project ${projectName} is ok");
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(KarotzBuildListener.class.getName());
}
