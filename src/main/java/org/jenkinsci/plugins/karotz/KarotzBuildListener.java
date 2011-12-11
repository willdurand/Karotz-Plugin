package org.jenkinsci.plugins.karotz;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * KarotzBuildListener
 *
 * @author Seiji Sogabe
 */
public class KarotzBuildListener {

    /**
     * Prepare the text to speak (tts) by replacing variables with their values.
     *
     * @param String tts
     * @param String projectName
     * @return String
     */
    protected String prepareTTS(String tts, String projectName) {
        return StringUtils.replace(tts, "${projectName}", projectName);
    }

    /**
     * Triggered on build failure.
     *
     * @param KarotzClient client
     * @param String projectName
     */
    protected void onFailure(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} has failed", projectName);
        LOGGER.log(Level.INFO, "TTS (failure):{0}", tts);
        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build unstable.
     *
     * @param KarotzClient client
     * @param String projectName
     *
     */
    protected void onUnstable(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is unstable", projectName);
        LOGGER.log(Level.INFO, "TTS (unstable):{0}", tts);

        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build recover.
     *
     * @param KarotzClient client
     * @param String projectName
     */
    protected void onRecover(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is back to stable", projectName);
        LOGGER.log(Level.INFO, "TTS (success):{0}", tts);

        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Triggered on build success.
     *
     * @param KarotzClient client
     * @param String projectName
     */
    protected void onSuccess(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is ok", projectName);

        try {
            client.speak(tts, "EN");
        } catch (KarotzException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(KarotzBuildListener.class.getName());
}
