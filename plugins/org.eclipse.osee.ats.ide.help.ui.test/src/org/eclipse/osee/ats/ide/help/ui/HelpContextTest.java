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
package org.eclipse.osee.ats.ide.help.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.help.ui.util.ContextParser;
import org.eclipse.osee.ats.ide.help.ui.util.ContextParser.ContextEntry;
import org.eclipse.osee.ats.ide.help.ui.util.HelpTestUtil;
import org.eclipse.osee.framework.core.data.HelpContext;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class HelpContextTest {

   private static final String PLUGIN_ID = "org.eclipse.osee.ats.ide.help.ui";

   private static Map<String, HelpContext> contexts;
   private static ContextParser parser;

   @BeforeClass
   public static void setUp() throws Exception {
      contexts = getContexts(AtsHelpContext.class);
      parser = new ContextParser("contexts/contexts.xml");
      parser.parse();
   }

   @Test
   public void testAdd() throws Exception {
      assertEquals(12, contexts.size());
   }

   @Test
   public void testContextsXml() throws Exception {
      assertEquals(contexts.size(), parser.getEntries().size());

      Assert.assertFalse(Compare.isDifferent(contexts.keySet(), parser.getIds()));
      for (HelpContext context : contexts.values()) {
         assertEquals(PLUGIN_ID, context.getPluginId());

         String id = context.getName();
         ContextEntry entry = parser.getEntry(id);
         assertNotNull(entry);
      }
   }

   @Test
   public void testHrefFiles() throws Exception {
      for (ContextEntry entry : parser.getEntries()) {
         for (String reference : entry.getReferences()) {
            URL url = HelpTestUtil.getResource(reference);
            assertNotNull(String.format("[%s] was not valid", reference), url);
         }
      }
   }

   private static Map<String, HelpContext> getContexts(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
      Map<String, HelpContext> contexts = new HashMap<>();
      for (Field field : clazz.getFields()) {
         Object object = field.get(null);
         if (object instanceof HelpContext) {
            HelpContext context = (HelpContext) object;
            contexts.put(context.getName(), context);
         }
      }
      return contexts;
   }
}
