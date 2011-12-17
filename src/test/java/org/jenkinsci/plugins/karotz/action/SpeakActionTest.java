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
import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Test for SpeakAction.
 *
 * @author Seiji Sogabe <s.sogabe@gmail.com>
 */
public class SpeakActionTest {

    /**
     * Test of getParameters method, of class SpeakAction.
     */
    @Test
    public void testGetParameters() {
        SpeakAction action = new SpeakAction("text", "EN");

        Map<String, String> params = action.getParameters();
        assertNotNull(params);

        assertEquals(3, params.size());
        assertEquals("speak", params.get("action"));
        assertEquals("text", params.get("text"));
        assertEquals("EN", params.get("lang"));
    }
}
