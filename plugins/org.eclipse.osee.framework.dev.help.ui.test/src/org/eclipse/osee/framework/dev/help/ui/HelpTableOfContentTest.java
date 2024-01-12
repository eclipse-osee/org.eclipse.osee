/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.dev.help.ui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import java.util.Set;
import org.eclipse.osee.framework.dev.help.ui.util.BookParser;
import org.eclipse.osee.framework.dev.help.ui.util.HelpTestUtil;
import org.eclipse.osee.framework.dev.help.ui.util.HtmlParser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class HelpTableOfContentTest {

   private static final String PLUGIN_ID = "org.eclipse.osee.framework.help.ui";

   private static BookParser bookParser;
   private static Set<String> bookParserEntries;

   @BeforeClass
   public static void setUp() throws Exception {
      bookParser = new BookParser("book.xml");
      bookParser.parse();
      bookParserEntries = bookParser.getEntries();
   }

   @Test
   public void testAllTocReferencesExist() throws Exception {

      assertTrue("There were no entries found in the table of contents!", bookParserEntries.size() > 0);

      for (String reference : bookParserEntries) {
         URL url = HelpTestUtil.getResource(reference);
         assertNotNull(String.format("[%s] was not valid", reference), url);
      }
   }

   @Test
   public void testTocReferencesValid() throws Exception {
      HtmlParser htmlParser = new HtmlParser(PLUGIN_ID);

      for (String reference : bookParserEntries) {
         URL url = HelpTestUtil.getResource(reference);

         Set<String> entries = htmlParser.parse(url);
         for (String resource : entries) {
            URL referencedUrl = HelpTestUtil.getResource(reference);
            assertNotNull(String.format("[%s] was not valid", resource), referencedUrl);
         }

      }
   }

}
