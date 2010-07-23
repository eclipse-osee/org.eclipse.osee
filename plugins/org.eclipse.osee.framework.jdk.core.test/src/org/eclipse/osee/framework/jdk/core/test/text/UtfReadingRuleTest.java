/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.test.text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Stack;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests ability to read files in charsets, particularly UTF8
 * 
 * @link:Rule
 * @link:Lib
 * @author Karol M. Wilk
 */
public final class UtfReadingRuleTest extends TestCase {

	private static class Utf8TestRule extends Rule {
		private CharSequence lastOutput;

		public Utf8TestRule() {
			this.lastOutput = null;
		}

		@Override
		public ChangeSet computeChanges(final CharSequence seq) {
			lastOutput = seq;
			return new ChangeSet(seq);
		}

		public CharSequence getLastOutput() {
			return lastOutput;
		}
	}

	private static final String FILE_INPUT =
				"utf8_input.xml";

	private final Utf8TestRule rule = new Utf8TestRule();

	@Test
	public void testWrongFileName() {
		try {
			rule.process(new File("./notexistentFile.txt"));
		} catch (NullPointerException ex) {
			Assert.assertTrue(true);
		} catch (Exception ex) {
			Assert.fail("unexpected/wrong exception thrown testWrongFileName()");
		}
	}

	@Test
	public void testCharset() {
		Stack<String> charsetStack = new Stack<String>();
		charsetStack.add("UnknownCharset");
		charsetStack.add("UTF-8");

		while (!charsetStack.isEmpty()) {
			try {
				File inputFile = new File(
							UtfReadingRuleTest.class
										.getResource(FILE_INPUT).toURI());
				rule.setCharsetString(charsetStack.pop());
				rule.process(inputFile);
			} catch (Exception ex) {
				Assert.assertTrue(
							"unexpected/wrong exception thrown testCharset(), Exception: " + ex.toString(),
							ex instanceof UnsupportedCharsetException || ex instanceof UnsupportedEncodingException);
			}
		}
	}

	@Test
	public void testUtf8ReadData() {
		String expectedUtf8String = "<w:t>â‚¬</w:t>";
		try {
			File inputFile = new File(
						UtfReadingRuleTest.class
									.getResource(FILE_INPUT).toURI());
			rule.setCharsetString("UTF8");
			rule.process(inputFile);
		} catch (UnsupportedCharsetException ex) {
			Assert.assertTrue(true);
		} catch (Exception ex) {
			Assert.fail("unexpected/wrong exception thrown testCharset()");
		}

		//trim off extra data
		String actual =
					rule.getLastOutput().toString().trim();
		Assert.assertEquals(expectedUtf8String, actual);
	}
}
