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

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import java.util.HashMap;
import java.util.Map;
import org.jenkinsci.plugins.karotz.KarotzClient;
import org.jenkinsci.plugins.karotz.KarotzException;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Test for KarotzAction
 *
 * @author Seiji Sogabe <s.sogabe@gmail.com>
 */
public class KarotzActionTest {

    /**
     * Test of execute method, of class KarotzAction.
     */
    @Test(expected = KarotzException.class)
    public void testExecute_BuildIsNull() throws Exception {
        // Mock setup
        AbstractBuild<?, ?> build = null;
        BuildListener listener = mock(BuildListener.class);
        KarotzAction target = new MockKarotzAction();

        target.execute(build, listener);
    }

    /**
     * Test of execute method, of class KarotzAction.
     */
    @Test(expected = KarotzException.class)
    public void testExecute_ListenerIsNull() throws Exception {
        // Mock setup
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        BuildListener listener = null;
        KarotzAction target = new MockKarotzAction();

        target.execute(build, listener);
    }

    /**
     * Test of execute method, of class KarotzAction.
     */
    @Test
    public void testExecute_NoInteractive() throws Exception {
        // Mock setup
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        BuildListener listener = mock(BuildListener.class);

        final KarotzClient clientMock = mock(KarotzClient.class);
        when(clientMock.isInteractive()).thenReturn(false);
        KarotzAction target = spy(new MockKarotzAction());
        doReturn(clientMock).when(target).getClient();

        target.execute(build, listener);
        verify(target, times(0)).getParameters();
    }

    /**
     * Test of execute method, of class KarotzAction.
     */
    @Test
    public void testExecute() throws Exception {
        // Mock setup
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        BuildListener listener = mock(BuildListener.class);

        final KarotzClient clientMock = mock(KarotzClient.class);
        when(clientMock.isInteractive()).thenReturn(true);
        when(clientMock.getInteractiveId()).thenReturn(anyString());
        when(clientMock.parseResponse(anyString(), "code")).thenReturn("OK");
        KarotzAction target = spy(new MockKarotzAction());
        doReturn(clientMock).when(target).getClient();

        target.execute(build, listener);
    }

    /**
     * Test of execute method, of class KarotzAction.
     */
    @Test(expected=KarotzException.class)
    public void testExecute_ReturnNG() throws Exception {
        // Mock setup
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        BuildListener listener = mock(BuildListener.class);

        final KarotzClient clientMock = mock(KarotzClient.class);
        when(clientMock.isInteractive()).thenReturn(true);
        when(clientMock.getInteractiveId()).thenReturn(anyString());
        when(clientMock.parseResponse(anyString(), "code")).thenReturn("ERROR");
        KarotzAction target = spy(new MockKarotzAction());
        doReturn(clientMock).when(target).getClient();

        target.execute(build, listener);
    }

    private static class MockKarotzAction extends KarotzAction {

        @Override
        public String getBaseUrl() {
            return "url";
        }

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> m = new HashMap<String, String>();
            m.put("action", "speak");
            return m;
        }
    }
}
