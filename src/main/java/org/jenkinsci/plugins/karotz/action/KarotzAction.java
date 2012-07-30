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
package org.jenkinsci.plugins.karotz.action;

import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.karotz.KarotzClient;
import org.jenkinsci.plugins.karotz.KarotzException;
import org.jenkinsci.plugins.karotz.KarotzPublisher;
import org.jenkinsci.plugins.karotz.KarotzUtil;

/**
 * karotz Action
 * 
 * @author Seiji Sogabe
 */
public abstract class KarotzAction {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(KarotzAction.class
			.getName());

	public abstract String getBaseUrl();

	public abstract Map<String, String> getParameters();

	/**
	 * Get the duration of the action, used to calculate how long the client
	 * should wait before stopping the Interactive Mode, which cancels all
	 * running actions.
	 * 
	 * @return the number of milliseconds this action will take
	 */
	public abstract long getDuration();

	public void execute(AbstractBuild<?, ?> build, BuildListener listener)
			throws KarotzException {
		if (build == null || listener == null) {
			throw new KarotzException("build and listener should be not null");
		}
		KarotzClient client = getClient();
		execute(client);
	}

	public void execute(KarotzClient client) throws KarotzException {

		if (!client.isInteractive()) {
			return;
		}

		Map<String, String> params = getParameters();
		params.put("interactiveid", client.getInteractiveId());
		String url = getBaseUrl() + '?' + KarotzUtil.buildQuery(params);
		client.addActionDuration(getDuration());
		String result = client.doRequest(url);
		String code = client.parseResponse(result, "code");
		if (!"OK".equalsIgnoreCase(code)) {
			throw new KarotzException("failed to do action: " + code);
		}
		LOGGER.log(Level.INFO, "Success.", result);

	}

	protected KarotzClient getClient() {
		KarotzPublisher.KarotzPublisherDescriptor d = Jenkins.getInstance()
				.getDescriptorByType(
						KarotzPublisher.KarotzPublisherDescriptor.class);
		return new KarotzClient(d.getApiKey(), d.getSecretKey(),
				d.getInstallId());
	}
}
