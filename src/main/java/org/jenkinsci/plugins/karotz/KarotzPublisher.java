package org.jenkinsci.plugins.karotz;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * KarotzPublisher class.
 *
 * @author William Durand <william.durand1@gmail.com>
 */
public class KarotzPublisher extends Notifier {

    /**
     * Logger
     */
    protected static final Logger LOGGER = Logger.getLogger(KarotzPublisher.class.getName());

    /**
     * Install Id
     */
    protected String installId;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public KarotzPublisher(String installId) {
        this.installId = installId;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getInstallId() {
        return installId;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        if (StringUtils.isBlank(getInstallId())) {
            listener.getLogger().println("No Karotz install id provided.");
            return false;
        }

        KarotzPublisherDescriptor d = Jenkins.getInstance().getDescriptorByType(KarotzPublisherDescriptor.class);
        KarotzClient client = new KarotzClient(d.getApiKey(), d.getSecretKey(), getInstallId());
        String projectName = build.getProject().getName();

        if (build.getResult() == Result.FAILURE) {
            onFailure(client, projectName);
        } else if (build.getResult() == Result.UNSTABLE) {
            onUnstable(client, projectName);
        } else if (build.getResult() == Result.SUCCESS) {
            // Build recover
            if (build.getPreviousBuild() != null && build.getPreviousBuild().getResult() == Result.FAILURE) {
                onRecover(client, projectName);
            } else {
                onSuccess(client, projectName);
            }
        }

        return true;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

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
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onFailure(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} has failed", projectName);
        LOGGER.log(Level.INFO, "TTS (failure):{0}", tts);

        client.speak(tts, "EN");
    }

    /**
     * Triggered on build unstable.
     *
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     *
     */
    protected void onUnstable(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is unstable", projectName);
        LOGGER.log(Level.INFO, "TTS (unstable):{0}", tts);

        client.speak(tts, "EN");
    }

    /**
     * Triggered on build recover.
     *
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onRecover(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is back to stable", projectName);
        LOGGER.log(Level.INFO, "TTS (success):{0}", tts);

        client.speak(tts, "EN");
    }

    /**
     * Triggered on build success.
     *
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onSuccess(KarotzClient client, String projectName) {
        String tts = prepareTTS("The project ${projectName} is ok", projectName);
        LOGGER.log(Level.INFO, "TTS (success):{0}", tts);

        client.speak(tts, "EN");
    }

    /**
     * Descriptor for {@link KarotzPublisher}. Used as a singleton. The class is
     * marked as public so that it can be accessed from views.
     */
    @Extension
    public static final class KarotzPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        private String apiKey;

        private String secretKey;

        public String getApiKey() {
            return apiKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public KarotzPublisherDescriptor() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws Descriptor.FormException {
            apiKey = json.getString("apiKey");
            secretKey = json.getString("secretKey");
            save();
            return true;
        }

        /**
         * Performs on-the-fly validation of the form field 'install id'.
         *
         * @param value This parameter receives the value that the user has
         * typed.
         * @return Indicates the outcome of the validation. This is sent to the
         * browser.
         */
        public FormValidation doCheckInstallId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set an install id");
            }
            if (value.length() < 10) {
                // TODO: check the real length for the install id
                return FormValidation.warning("Isn't the install id too short?");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this publisher can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "Publish with your Karotz";
        }
    }
}
