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
package org.eclipse.osee.jdbc.internal.schema.ops;

import java.io.Writer;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class PrintTableDataCallable implements Callable<Void> {

   private final JdbcClient client;
   private final Writer writer;
   private final Set<String> tables;

   public PrintTableDataCallable(JdbcClient client, Writer writer, Set<String> tables) {
      this.client = client;
      this.writer = writer;
      this.tables = tables;
   }

   @Override
   public Void call() throws Exception {
      for (String tableName : tables) {
         printTable(tableName);
      }
      return null;
   }

   private void printTable(String tableName) throws Exception {
      JdbcStatement chStmt = client.getStatement();
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
