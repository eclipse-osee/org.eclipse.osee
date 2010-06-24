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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.io.Writer;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Roberto E. Escobar
 */
public class PrintTables extends AbstractOperation {

   private final Map<String, SchemaData> userConfig;
   private final Writer writer;

   public PrintTables(Map<String, SchemaData> userConfig, Writer writer) {
      super("Print Schema", Activator.PLUGIN_ID);
      this.userConfig = userConfig;
      this.writer = writer;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Set<String> keys = userConfig.keySet();
      for (String key : keys) {
         SchemaData schemaData = userConfig.get(key);
         Set<String> tables = schemaData.getTableMap().keySet();
         for (String tableName : tables) {
            printTable(tableName);
         }
      }
   }

   private void printTable(String tableName) throws Exception {
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

         writer.write(header);

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
            writer.write(results);
         }
      } finally {
         chStmt.close();
      }
   }
}
