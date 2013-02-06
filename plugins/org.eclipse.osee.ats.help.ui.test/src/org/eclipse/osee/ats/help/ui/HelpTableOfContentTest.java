/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.help.ui;

import static org.junit.Assert.assertNotNull;
import java.net.URL;
import java.util.Set;
import org.eclipse.osee.ats.help.ui.util.HelpTestUtil;
import org.eclipse.osee.ats.help.ui.util.HtmlParser;
import org.eclipse.osee.ats.help.ui.util.TocParser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class HelpTableOfContentTest {

   private static final String PLUGIN_ID = "org.eclipse.osee.ats.help.ui";

   private static TocParser parser;

   @BeforeClass
   public static void setUp() throws Exception {
      parser = new TocParser("toc.xml");
      parser.parse();
   }

   @Test
   public void testAllTocReferencesExist() throws Exception {
      for (String reference : parser.getEntries()) {
         URL url = HelpTestUtil.getResource(reference);
         assertNotNull(String.format("[%s] was not valid", reference), url);
      }
   }

   @Test
   public void testTocReferencesValid() throws Exception {
      HtmlParser htmlParser = new HtmlParser(PLUGIN_ID);

      for (String reference : parser.getEntries()) {
         URL url = HelpTestUtil.getResource(reference);

         Set<String> entries = htmlParser.parse(url);
         for (String resource : entries) {
            URL referencedUrl = HelpTestUtil.getResource(reference);
            assertNotNull(String.format("[%s] was not valid", resource), referencedUrl);
         }

      }
   }
}
