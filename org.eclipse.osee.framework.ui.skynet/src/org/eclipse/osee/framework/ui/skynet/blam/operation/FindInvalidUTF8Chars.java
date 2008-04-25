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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import java.sql.ResultSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class FindInvalidUTF8Chars extends AbstractBlam {
   private static final String READ_ATTRIBUTE_VALUES = "SELECT art_id, value FROM " + ATTRIBUTE_VERSION_TABLE;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {

      int count = 0;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(1000, READ_ATTRIBUTE_VALUES);
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            String value = rSet.getString("value");
            if (value != null) {
               count++;
               int length = value.length();
               for (int i = 0; i < length; i++) {
                  char c = value.charAt(i);
                  // based on http://www.w3.org/TR/2006/REC-xml-20060816/#charsets
                  if ((c < 0x20 && c != 0x9 && c != 0xA && c != 0xD) || (c > 0xD7FF && c < 0xE000) || (c > 0xFFFD && c < 0x10000) || c > 0x10FFFF) {
                     System.out.println("artifact id: " + rSet.getInt("art_id") + "   char: " + (int) c);
                  }
               }
            }
         }
      } finally {
         DbUtil.close(chStmt);
         System.out.println("count:  " + count);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}