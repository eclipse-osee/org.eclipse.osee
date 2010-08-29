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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Stack;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests ability to read files in charsets, particularly UTF8
 * 
 * <pre>
 * {@link Rule}
 * {@link Lib}
 * </pre>
 * 
 * @author Karol M. Wilk
 */
public final class UtfReadingRuleTest extends TestCase {

   private static final String FILE_INPUT = "utf8_input.xml";

   @org.junit.Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   private File getInputFile() throws IOException {
      File outfile = tempFolder.newFile("utf8_input_copy.xml");
      URL url = UtfReadingRuleTest.class.getResource(FILE_INPUT);
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         Lib.inputStreamToFile(inputStream, outfile);
      } finally {
         Lib.close(inputStream);
      }
      return outfile;
   }

   private final Utf8TestRule rule = new Utf8TestRule();

   @Test(expected = NullPointerException.class)
   public void testWrongFileName() throws IOException {
      rule.process(new File("./notexistentFile.txt"));
   }

   @Test
   public void testCharset() {
      Stack<String> charsetStack = new Stack<String>();
      charsetStack.add("UnknownCharset");
      charsetStack.add("UTF-8");

      while (!charsetStack.isEmpty()) {
         try {
            File inputFile = getInputFile();
            rule.setCharsetString(charsetStack.pop());
            rule.process(inputFile);
         } catch (Exception ex) {
            Assert.assertTrue("unexpected/wrong exception thrown testCharset(), Exception: " + ex.toString(),
               ex instanceof UnsupportedCharsetException || ex instanceof UnsupportedEncodingException);
         }
      }
   }

   @Test
   public void testUtf8ReadData() throws IOException {
      try {
         File inputFile = getInputFile();
         rule.process(inputFile);
      } catch (UnsupportedCharsetException ex) {
         Assert.assertTrue(true);
      } catch (Exception ex) {
         Assert.fail("unexpected/wrong exception thrown testCharset()");
      }

      //trim off extra data
      String expectedUtf8String = Lib.fileToString(getInputFile());
      String actual = rule.getLastOutput().toString().trim();
      Assert.assertEquals(expectedUtf8String, actual);
   }

   private static final class Utf8TestRule extends Rule {
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
}
