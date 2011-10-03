package fr.willdurand.jenkins.karotz;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;


/**
 * KarotzPublisher class.
 *
 * @author William Durand <william.durand1@gmail.com>
 */
public class KarotzPublisher extends Notifier {
    /**
     * Logger
     */
	protected static Logger logger = Logger.getLogger(KarotzPublisher.class.getName());
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
        return this.installId;
    }
    
    /**
     * Descriptor for {@link KarotzPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     */
    @Extension
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
    
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
    throws InterruptedException, IOException {
        if (StringUtils.isBlank(this.getInstallId())) {
            listener.getLogger().println("No Karotz install id provided.");
            return false;
        }

        KarotzClient client = new KarotzClient(this.getInstallId());
        String projectName  = build.getProject().getName();
        
        if (build.getResult() == Result.FAILURE) {
            this.onFailure(client, projectName);
        } else if (build.getResult() == Result.UNSTABLE) {
            this.onUnstable(client, projectName);
        } else if (build.getResult() == Result.SUCCESS) {
        	// Build recover
        	if (build.getPreviousBuild() != null && build.getPreviousBuild().getResult() == Result.FAILURE) {
        		this.onRecover(client, projectName);
        	} else {
        		this.onSuccess(client, projectName);
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
    protected String prepareTTS(String tts, String projectName)
    {
    	return StringUtils.replace(tts, "${projectName}", projectName);
    }
    
    /**
     * Triggered on  build failure.
     * 
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onFailure(KarotzClient client, String projectName)
    {
    	String tts = prepareTTS("The project ${projectName} has failed", projectName);
    	logger.info("TTS (failure):" + tts);
    	
    	client.speak(tts, "EN");
    }
    
    /**
     * Triggered on build unstable.
     * 
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name

     */
    protected void onUnstable(KarotzClient client, String projectName)
    {
    	String tts = prepareTTS("The project ${projectName} is unstable", projectName);
    	logger.info("TTS (unstable):" + tts);

    	client.speak(tts, "EN");    	
    }
    
    /**
     * Triggered on build recover.
     * 
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onRecover(KarotzClient client, String projectName)
    {
    	String tts = prepareTTS("The project ${projectName} is back to stable", projectName);
    	logger.info("TTS (success):" + tts);

    	client.speak(tts, "EN");
    }
    
    /**
     * Triggered on build success.
     * 
     * @param KarotzClient client	A Karotz client
     * @param String projectName	A project name
     */
    protected void onSuccess(KarotzClient client, String projectName)
    {
    	String tts = prepareTTS("The project ${projectName} is ok", projectName);
    	logger.info("TTS (success):" + tts);

    	client.speak(tts, "EN");
    }
}
