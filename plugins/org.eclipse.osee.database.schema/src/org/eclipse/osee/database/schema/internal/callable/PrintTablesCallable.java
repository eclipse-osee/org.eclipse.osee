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
package org.eclipse.osee.database.schema.internal.callable;

import java.io.Writer;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.database.schema.DatabaseCallable;
import org.eclipse.osee.database.schema.internal.data.SchemaData;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class PrintTablesCallable extends DatabaseCallable<Object> {

   private final Map<String, SchemaData> userConfig;
   private final Writer writer;

   public PrintTablesCallable(Log logger, IOseeDatabaseService dbService, Map<String, SchemaData> userConfig, Writer writer) {
      super(logger, dbService);
      this.userConfig = userConfig;
      this.writer = writer;
   }

   @Override
   public Object call() throws Exception {
      Set<String> keys = userConfig.keySet();
      for (String key : keys) {
         SchemaData schemaData = userConfig.get(key);
         Set<String> tables = schemaData.getTableMap().keySet();
         for (String tableName : tables) {
            printTable(tableName);
         }
      }
      return null;
   }

   private void printTable(String tableName) throws Exception {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery("select * from " + tableName);
         int numberOfColumns = chStmt.getColumnCount();

         StringBuilder builder = new StringBuilder();

         builder.append("\nTable:\t");
         builder.append(tableName);
         builder.append("\n");
         builder.append("Columns:\t");
         for (int index = 1; index <= numberOfColumns; index++) {
            builder.append(chStmt.getColumnName(index));
            if (index + 1 <= numberOfColumns) {
               builder.append(", ");
            }
         }
         builder.append("\n");

         writer.write(builder.toString());
         builder.delete(0, builder.length());

         while (chStmt.next()) {
            builder.append("Data:\t");
            for (int index = 1; index <= numberOfColumns; index++) {
               builder.append(chStmt.getObject(index).toString());
               if (index + 1 <= numberOfColumns) {
                  builder.append(", ");
               }
            }
            builder.append("\n");
            writer.write(builder.toString());
            builder.delete(0, builder.length());
         }
      } finally {
         chStmt.close();
      }
   }

}
