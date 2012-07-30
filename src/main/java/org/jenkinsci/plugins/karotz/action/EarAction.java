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

import java.util.HashMap;
import java.util.Map;

/**
 * Led Light Action.
 * 
 * @author Miquel Martin
 */
public class EarAction extends KarotzAction {

	private static final int EAR_RESET_TIME = 2500;
	private Integer right;
	private Integer left;
	private Boolean relative;
	private Boolean reset;

	public EarAction() {
		this.reset = true;
	}

	/**
	 * Move the karotz ears to an absolute position.
	 * 
	 * @param left
	 *            if not null, indicates the desired position.
	 * @param right
	 *            if not null, indicates the desired position.
	 */
	public EarAction(Integer left, Integer right, boolean relative) {
		this.left = left;
		this.right = right;
		this.relative = relative;
	}

	@Override
	public String getBaseUrl() {
		return "http://api.karotz.com/api/karotz/ears";
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		if (reset != null) {
			params.put("reset", reset.toString());
		} else {
			if (left != null) {
				params.put("left", left.toString());
			}
			if (right != null) {
				params.put("right", right.toString());
			}
			if (relative != null) {
				params.put("relative", relative.toString());
			}
		}
		return params;
	}

	@Override
	public long getDuration() {
		return EAR_RESET_TIME;
	}
}
