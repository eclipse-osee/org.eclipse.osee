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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.ui.plugin.util.db.data.SchemaData;

public class PrintTables extends DbInitializationTask {

   private Map<String, SchemaData> userConfig;

   public PrintTables(Map<String, SchemaData> userConfig) {
      super();
      this.userConfig = userConfig;
   }

   public void run(Connection connection) throws Exception {
      Set<String> keys = userConfig.keySet();
      for (String key : keys) {
         SchemaData schemaData = userConfig.get(key);
         Set<String> tables = schemaData.getTableMap().keySet();
         for (String tableName : tables) {
            printTable(connection, tableName);
         }
      }
   }

   private void printTable(Connection connection, String tableName) {
      Statement statement;
      try {
         statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery("select * from " + tableName);
         ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
         int numberOfColumns = resultSetMetaData.getColumnCount();
         String header = "\nTable:\t" + tableName + "\n";
         header += "Columns:\t";
         for (int index = 1; index <= numberOfColumns; index++) {
            header += resultSetMetaData.getColumnLabel(index);
            if (index + 1 <= numberOfColumns) {
               header += ", ";
            }
         }
         header += "\n";

         System.out.print(header);

         String results = "";
         while (resultSet.next()) {
            results = "Data:\t";
            for (int index = 1; index <= numberOfColumns; index++) {
               results += resultSet.getObject(index).toString();
               if (index + 1 <= numberOfColumns) {
                  results += ", ";
               }
            }
            results += "\n";
            System.out.print(results);
         }
         resultSet.close();
         statement.close();
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
   }
}
