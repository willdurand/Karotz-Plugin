package org.jenkinsci.plugins.karotz;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
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
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
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

    @DataBoundConstructor
    public KarotzPublisher() {
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        KarotzPublisherDescriptor d = Jenkins.getInstance().getDescriptorByType(KarotzPublisherDescriptor.class);
        KarotzClient client = new KarotzClient(d.getApiKey(), d.getSecretKey(), d.getInstallId());
        try {
            client.startInteractiveMode();
        } catch (KarotzException ex) {
            return true;
        }
        KarotzHandler handler = new KarotzBuildActionHandler();
        handler.onStart(build);
        return true;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        KarotzPublisherDescriptor d = Jenkins.getInstance().getDescriptorByType(KarotzPublisherDescriptor.class);
        KarotzClient client = new KarotzClient(d.getApiKey(), d.getSecretKey(), d.getInstallId());
        try {
            client.startInteractiveMode();
        } catch (KarotzException ex) {
            return true;
        }
        KarotzHandler handler = new KarotzBuildActionHandler();
        fire(handler, build);
        return true;
    }

    private void fire(KarotzHandler listener, AbstractBuild<?, ?> build) {
        if (build.getResult() == Result.FAILURE) {
            listener.onFailure(build);
        } else if (build.getResult() == Result.UNSTABLE) {
            listener.onUnstable(build);
        } else if (build.getResult() == Result.SUCCESS) {
            if (build.getPreviousBuild() != null && build.getPreviousBuild().getResult() == Result.FAILURE) {
                listener.onRecover(build);
            } else {
                listener.onSuccess(build);
            }
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Descriptor for {@link KarotzPublisher}. Used as a singleton. The class is
     * marked as public so that it can be accessed from views.
     */
    @Extension
    public static final class KarotzPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        private String apiKey;

        private String secretKey;

        private String installId;

        public String getApiKey() {
            return apiKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public String getInstallId() {
            return installId;
        }

        public KarotzPublisherDescriptor() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws Descriptor.FormException {
            apiKey = Util.fixEmptyAndTrim(json.getString("apiKey"));
            secretKey = Util.fixEmptyAndTrim(json.getString("secretKey"));
            installId = Util.fixEmptyAndTrim(json.getString("installId"));
            save();
            return true;
        }

        public FormValidation doCheckApiKey(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.validateRequired(value);
        }

        public FormValidation doCheckSecretKey(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.validateRequired(value);
        }

        public FormValidation doCheckInstallId(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.validateRequired(value);
        }

        public FormValidation doStartInteractiveMode(
                @QueryParameter String apiKey, @QueryParameter String secretKey, @QueryParameter String installId) {
            String _apiKey = Util.fixEmptyAndTrim(apiKey);
            String _secretKey = Util.fixEmptyAndTrim(secretKey);
            String _installId = Util.fixEmptyAndTrim(installId);
            if (_apiKey == null || _secretKey == null || _installId == null) {
                return FormValidation.warning("enter all settings.");
            }
            KarotzClient client = new KarotzClient(_apiKey, _secretKey, _installId);
            try {
                client.startInteractiveMode();
            } catch (KarotzException e) {
                return FormValidation.warning(e.getMessage());
            }
            return FormValidation.ok("OK");
        }

        public FormValidation doStopInteractiveMode() {
            KarotzClient client = new KarotzClient(apiKey, secretKey, installId);
            try {
                client.stopInteractiveMode();
            } catch (KarotzException e) {
                return FormValidation.warning(e.getMessage());
            }
            return FormValidation.ok("OK");
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this publisher can be used with all kinds of project types
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish with your Karotz";
        }
    }
}
