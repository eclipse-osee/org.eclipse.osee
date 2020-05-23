/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Stack;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
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
public final class UtfReadingRuleTest {

   private static final String FILE_INPUT = "utf8_input.xml";

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   private File inputFile;

   @Before
   public void testSetup() throws IOException {
      inputFile = getInputFile();
   }

   private File getInputFile() throws IOException {
      String copyName = String.format("utf8_input_copy.%s.xml", Lib.getDateTimeString());

      File outfile = tempFolder.newFile(copyName);
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

   @Test
   public void testWrongFileName() throws IOException {
      Utf8TestRule rule = new Utf8TestRule();
      rule.process(new File("./notexistentFile.txt"));
      Assert.assertNull(rule.lastOutput);
   }

   @Test
   public void testCharset() {
      Stack<String> charsetStack = new Stack<>();
      charsetStack.add("UnknownCharset");
      charsetStack.add("UTF-8");

      Utf8TestRule rule = new Utf8TestRule();
      while (!charsetStack.isEmpty()) {
         try {
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
      Utf8TestRule rule = new Utf8TestRule();
      try {
         rule.process(inputFile);
      } catch (UnsupportedCharsetException ex) {
         Assert.assertTrue(true);
      } catch (Exception ex) {
         Assert.fail("unexpected/wrong exception thrown testCharset()");
      }

      String expectedUtf8String = Lib.fileToString(getClass(), FILE_INPUT);
      String actual = rule.getLastOutput().toString().trim();
      Assert.assertEquals(expectedUtf8String, actual);
   }

   private static final class Utf8TestRule extends org.eclipse.osee.framework.jdk.core.text.Rule {
      private CharSequence lastOutput;

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
