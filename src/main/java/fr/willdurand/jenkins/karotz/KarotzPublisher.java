package fr.willdurand.jenkins.karotz;

import hudson.Launcher;
import hudson.Extension;

import hudson.util.FormValidation;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.model.Result;

import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * KarotzPublisher class.
 *
 * @author William Durand <william.durand1@gmail.com>
 */
public class KarotzPublisher extends Notifier {
    /**
     * API Key
     */
    private static final String KAROTZ_API_KEY = "";
    /**
     * API Secret
     */
    private static final String KAROTZ_API_SECRET = "";
    /**
     * Karotz install id
     */
    private final String installId;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public KarotzPublisher(String installId) {
        this.installId = installId;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getInstallId() {
        return this.installId;
    }

    /**
     * Descriptor for {@link KarotzPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * Performs on-the-fly validation of the form field 'install id'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
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

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this publisher can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Publish to your Karotz";
        }
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
    throws InterruptedException, IOException {
        if (StringUtils.isBlank(this.installId)) {
            listener.getLogger().println("No Karotz install id provided.");
            return false;
        }

        if (build.getResult() == Result.FAILURE) {

        } else if (build.getResult() == Result.UNSTABLE) {

        } else if (build.getResult() == Result.SUCCESS) {

        }

        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}

