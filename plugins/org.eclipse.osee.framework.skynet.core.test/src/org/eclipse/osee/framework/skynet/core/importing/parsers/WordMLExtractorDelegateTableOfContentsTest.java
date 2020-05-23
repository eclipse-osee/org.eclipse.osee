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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.operation.StringOperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if a table of contents with hlinks gets detected, if it does, warning should be enabled.
 * 
 * @link WordOutlineExtractorDelegate
 * @author Karol M. Wilk
 */
public final class WordMLExtractorDelegateTableOfContentsTest {

   private static final Pattern PARAGRAPHREGEX = Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);

   private static final String TABLE_OF_CONTENTS_FILE = "tableOfContentsHyperlinkTest.xml";
   private final WordOutlineExtractorDelegate delegate = new WordOutlineExtractorDelegate();

   @Test
   public void tableOfContentsLinksInput() throws Exception {
      delegate.initialize();

      StringOperationLogger logger = new StringOperationLogger();

      Matcher matcher = PARAGRAPHREGEX.matcher(Lib.fileToString(getClass(), TABLE_OF_CONTENTS_FILE));
      boolean foundSomething = false;
      while (matcher.find()) {
         foundSomething = true;
         delegate.processContent(logger, null, false, false, null, null, null, matcher.group(), false);
      }

      Assert.assertFalse("Warnings generated and Table of Contents in WordML should be detected",
         logger.toString().isEmpty());
      Assert.assertTrue(foundSomething);
   }
}