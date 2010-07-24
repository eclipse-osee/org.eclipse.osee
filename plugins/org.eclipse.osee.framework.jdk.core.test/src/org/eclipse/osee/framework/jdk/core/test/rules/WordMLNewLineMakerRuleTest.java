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
package org.eclipse.osee.framework.jdk.core.test.rules;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.text.rules.WordMLNewLineMaker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Test;

/**
 * Tests ability of this rule to make new paragraphs, i.e. injecting <w:p> at specific points in the file.
 * 
 * @link:WordMLNewLineMaker
 * @author Karol M. Wilk
 */
public final class WordMLNewLineMakerRuleTest extends TestCase {

   private final static String INPUT_FILE_NAME = "sample_icd_formatted.xml";
   private final static String NEW_WP_INJECTION_FINGERPRINT =
      "<w:p wsp:rsidR=\"01234567\" wsp:rsidRDefault=\"01234567\">";

   private final WordMLNewLineMaker wordNewLineMaker = new WordMLNewLineMaker();
   private final StringBuilder pathArgument = new StringBuilder();
   private final Pattern paragraphRegex = Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);

   @Test
   public void testNewLineInserts() {
      ChangeSet modifiedFile = null;
      try {
         modifiedFile = wordNewLineMaker.computeChanges(getResourceData(INPUT_FILE_NAME));
      } catch (IOException ioex) {
         System.err.println("Error: " + ioex.getMessage());
         Assert.fail("Unable to process the file, " + "WordMLNewLineMaker threw an IOException...");
      } catch (IllegalCharsetNameException illegalName) {
         System.err.println("Error: " + illegalName.getMessage());
         Assert.fail("Illegal charset name specified, " + "WordMLNewLineMaker threw an " + "IllegalCharsetNameException...");
      } catch (UnsupportedCharsetException unsupportedEx) {
         System.err.println("Error: " + unsupportedEx.getMessage());
         Assert.fail("Unsupported charset, " + "WordMLNewLineMaker threw an " + "UnsupportedCharsetException...");
      }

      Assert.assertTrue(modifiedFile.getSourceLength() != 0);

      //Test Does it contain new paragraph injected signature ?
      String modifiedFileAsString = modifiedFile.toString();
      Assert.assertTrue(modifiedFileAsString.contains(NEW_WP_INJECTION_FINGERPRINT));

      //Test Does it have 2 <w:p>s instead of 1 ?
      Matcher wpMatcher = paragraphRegex.matcher(modifiedFileAsString);
      int count = 0;
      while (wpMatcher.find()) {
         count++;
      }
      Assert.assertTrue(count == 2);

   }

   private static String getResourceData(String name) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = WordMLNewLineMakerRuleTest.class.getResourceAsStream(name);
         String data = Lib.inputStreamToString(inputStream);
         Assert.assertTrue(Strings.isValid(data));
         return data;
      } finally {
         Lib.close(inputStream);
      }
   }
}
