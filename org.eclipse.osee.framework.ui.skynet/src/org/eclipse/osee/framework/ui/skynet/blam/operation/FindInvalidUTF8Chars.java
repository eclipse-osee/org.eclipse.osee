/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class FindInvalidUTF8Chars extends AbstractBlam {
   private static final String READ_ATTRIBUTE_VALUES = "SELECT art_id, value FROM " + ATTRIBUTE_VERSION_TABLE;

   @Override
   public String getName() {
      return "Find Invalid UTF8 Chars";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      int count = 0;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(1000, READ_ATTRIBUTE_VALUES);
         while (chStmt.next()) {
            String value = chStmt.getString("value");
            if (value != null) {
               count++;
               int length = value.length();
               for (int i = 0; i < length; i++) {
                  char c = value.charAt(i);
                  // based on http://www.w3.org/TR/2006/REC-xml-20060816/#charsets
                  if ((c < 0x20 && c != 0x9 && c != 0xA && c != 0xD) || (c > 0xD7FF && c < 0xE000) || (c > 0xFFFD && c < 0x10000) || c > 0x10FFFF) {
                     System.out.println("artifact id: " + chStmt.getInt("art_id") + "   char: " + (int) c);
                  }
               }
            }
         }
      } finally {
         chStmt.close();
         System.out.println("count:  " + count);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return AbstractBlam.emptyXWidgetsXml;
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }
}