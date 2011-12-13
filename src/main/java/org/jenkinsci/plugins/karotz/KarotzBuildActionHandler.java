package org.jenkinsci.plugins.karotz;

import hudson.model.AbstractBuild;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * KarotzBuildActionHandler
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
