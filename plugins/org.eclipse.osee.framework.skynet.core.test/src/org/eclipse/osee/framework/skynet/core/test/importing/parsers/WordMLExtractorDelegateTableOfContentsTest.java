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
package org.eclipse.osee.framework.skynet.core.test.importing.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.junit.Test;

/**
 * Tests for if table of contents links pass as valid outline numbers, outline titles, ultimately forming artifacts.
 * 
 * @link:WordOutlineExtractorDelegate
 * @author Karol M. Wilk
 */
public class WordMLExtractorDelegateTableOfContentsTest {

   private static final Pattern PARAGRAPHREGEX = Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);

   private static final String TABLE_OF_CONTENTS_FILE = "tableOfContentsHyperlinkTest.xml";
   private final WordOutlineExtractorDelegate delegate = new WordOutlineExtractorDelegate();

   @Test
   public void tableOfContentsLinksInput() throws Exception {
      delegate.initialize();
      Matcher matcher = PARAGRAPHREGEX.matcher(getFileAsString(TABLE_OF_CONTENTS_FILE));
      boolean foundSomething = false;
      if (matcher.find()) {
         foundSomething = true;
         try {
            delegate.processContent(null, false, false, null, null, null, matcher.group(), false);
         } catch (OseeCoreException ex) {
            //we should get an exception because 
            Assert.assertTrue(true);
         }
      }
      Assert.assertTrue(foundSomething);
      Assert.assertTrue(delegate.getLastHeaderNumber().isEmpty());
      Assert.assertTrue(delegate.getLastHeaderName().isEmpty());
      Assert.assertTrue(delegate.getLastContent().isEmpty());
   }

   private static String getFileAsString(String nameOfFile) {
      StringBuilder buffer = new StringBuilder();
      InputStream inputStream = null;
      try {
         inputStream = WordMLExtractorDelegateTableOfContentsTest.class.getResourceAsStream(TABLE_OF_CONTENTS_FILE);
         buffer.append(Lib.inputStreamToString(inputStream));
         Assert.assertTrue(buffer.length() != 0);
      } catch (IOException ex) {
         Assert.assertTrue(String.format("%s something when wrong while reading a file...",
            WordMLExtractorDelegateTableOfContentsTest.class.getName()), true);
      } finally {
         Lib.close(inputStream);
      }

      return buffer.toString();
   }

}
