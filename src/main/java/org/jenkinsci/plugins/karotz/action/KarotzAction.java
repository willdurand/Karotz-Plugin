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

import java.util.Map;
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

    public abstract String getBaseUrl();

    public abstract Map<String, String> getParameters();

    public void execute() throws KarotzException {
        KarotzClient client = getClient();

        if (!client.isInteractive()) {
            return;
        }

        Map<String, String> params = getParameters();
        params.put("interactiveid", client.getInteractiveId());
        String url = getBaseUrl() + '?' + KarotzUtil.buildQuery(params);

        String result = client.doRequest(url);
        String code = client.parseResponse(result, "code");
        if ("OK".equalsIgnoreCase(code)) {
            throw new KarotzException("failed to do action: " + code);
        }
    }

    private KarotzClient getClient() {
        KarotzPublisher.KarotzPublisherDescriptor d
                = Jenkins.getInstance().getDescriptorByType(KarotzPublisher.KarotzPublisherDescriptor.class);
        return new KarotzClient(d.getApiKey(), d.getSecretKey(), d.getInstallId());
    }
}
