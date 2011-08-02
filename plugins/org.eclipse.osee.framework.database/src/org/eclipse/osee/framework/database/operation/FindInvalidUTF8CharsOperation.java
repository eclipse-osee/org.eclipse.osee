/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Shawn F. Cook
 */
public class FindInvalidUTF8CharsOperation extends AbstractDbTxOperation {
   private static final String READ_ATTRIBUTE_VALUES = "SELECT art_id, value FROM osee_attribute";

   public FindInvalidUTF8CharsOperation(IOseeDatabaseService databaseService, IOseeCachingService cachingService, OperationLogger logger) {
      super(databaseService, "Find Invalid UTF8 Chars Operation", Activator.PLUGIN_ID, logger);
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {

      log();
      log("Find Invalid UTF8 Chars in Table osee_attribute:");

      int count = 0;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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
                  if (c < 0x20 && c != 0x9 && c != 0xA && c != 0xD || c > 0xD7FF && c < 0xE000 || c > 0xFFFD && c < 0x10000 || c > 0x10FFFF) {
                     log("artifact id: " + chStmt.getInt("art_id") + "   char: " + (int) c);
                  }
               }
            }
         }
      } finally {
         chStmt.close();
         log("count:  " + count);
      }

      log("...done.");
   }
}
