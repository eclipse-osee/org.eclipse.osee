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
package org.eclipse.osee.framework.database.init;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;

public class PrintTables implements IDbInitializationTask {

   private final Map<String, SchemaData> userConfig;

   public PrintTables(Map<String, SchemaData> userConfig) {
      super();
      this.userConfig = userConfig;
   }

   public void run() throws OseeCoreException {
      Set<String> keys = userConfig.keySet();
      for (String key : keys) {
         SchemaData schemaData = userConfig.get(key);
         Set<String> tables = schemaData.getTableMap().keySet();
         for (String tableName : tables) {
            printTable(tableName);
         }
      }
   }

   private void printTable(String tableName) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery("select * from " + tableName);
         int numberOfColumns = chStmt.getColumnCount();
         String header = "\nTable:\t" + tableName + "\n";
         header += "Columns:\t";
         for (int index = 1; index <= numberOfColumns; index++) {
            header += chStmt.getColumnName(index);
            if (index + 1 <= numberOfColumns) {
               header += ", ";
            }
         }
         header += "\n";

         System.out.print(header);

         String results = "";
         while (chStmt.next()) {
            results = "Data:\t";
            for (int index = 1; index <= numberOfColumns; index++) {
               results += chStmt.getObject(index).toString();
               if (index + 1 <= numberOfColumns) {
                  results += ", ";
               }
            }
            results += "\n";
            System.out.print(results);
         }
      } finally {
         chStmt.close();
      }
   }
}
