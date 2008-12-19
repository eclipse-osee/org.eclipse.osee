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
package org.eclipse.osee.framework.database.initialize.tasks;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

public class PrintTables implements IDbInitializationTask {

   private Map<String, SchemaData> userConfig;

   public PrintTables(Map<String, SchemaData> userConfig) {
      super();
      this.userConfig = userConfig;
   }

   public void run(OseeConnection connection) throws OseeCoreException {
      Set<String> keys = userConfig.keySet();
      for (String key : keys) {
         SchemaData schemaData = userConfig.get(key);
         Set<String> tables = schemaData.getTableMap().keySet();
         for (String tableName : tables) {
            printTable(connection, tableName);
         }
      }
   }

   private void printTable(OseeConnection connection, String tableName) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
