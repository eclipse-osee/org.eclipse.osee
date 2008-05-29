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
package org.eclipse.osee.framework.ui.admin.dbtabletab;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;

public class DbDescribe {

   private final DbItem dbItem;
   private ArrayList<Describe> dbColumns;

   public DbDescribe(DbItem dbItem) {
      super();
      this.dbItem = dbItem;
      this.dbColumns = null;
   }

   public int indexOfColumn(String name) throws SQLException {
      int toReturn = -1;
      List<Describe> items = getDescription();
      for (int index = 0; index < items.size(); index++) {
         if (items.get(index).name.equals(name)) {
            toReturn = index;
            break;
         }
      }
      return toReturn;
   }

   public List<Describe> getDescription() throws SQLException {
      if (dbColumns == null) {
         dbColumns = new ArrayList<Describe>();
         ConnectionHandlerStatement chStmt = null;
         try {
            String sql = "SELECT * FROM " + dbItem.getTableName();
            chStmt = ConnectionHandler.runPreparedQuery(sql);
            ResultSetMetaData meta = chStmt.getRset().getMetaData();
            int numberOfColumns = meta.getColumnCount() + 1;
            for (int columnIndex = 1; columnIndex < numberOfColumns; columnIndex++) {
               Describe describe = new Describe();

               describe.name = meta.getColumnName(columnIndex).toUpperCase();
               describe.nullable = meta.isNullable(columnIndex) == ResultSetMetaData.columnNullable;
               describe.type = meta.getColumnTypeName(columnIndex).toUpperCase();
               dbColumns.add(describe);
            }
         } finally {
            chStmt.close();
         }
      }
      return dbColumns;
   }

   public DbTaskList getDbTaskList(List<Describe> describeList) throws SQLException {
      DbTaskList taskList = new DbTaskList();
      ConnectionHandlerStatement chStmt = null;
      try {
         String sql = "SELECT * FROM " + dbItem.getTableName();
         chStmt = ConnectionHandler.runPreparedQuery(sql);
         while (chStmt.next()) {
            DbModel dbModel = new DbModel();
            int x = 0;
            for (Describe d : describeList) {
               if (d.type.contains("NUMBER")) {
                  Long l = chStmt.getRset().getLong(d.name);
                  dbModel.addColumn(x++, l);
               } else if (d.type.contains("VARCHAR")) {
                  String value = chStmt.getRset().getString(d.name);
                  dbModel.addColumn(x++, value);
               } else if (d.type.contains("INT")) {
                  Integer value = chStmt.getRset().getInt(d.name);
                  dbModel.addColumn(x++, value);
               } else if (d.type.contains("TIMESTAMP")) {
                  Timestamp value = chStmt.getRset().getTimestamp(d.name);
                  dbModel.addColumn(x++, value);
               } else {
                  dbModel.addColumn(x++, new String("Unknown object type"));
               }
            }
            taskList.addTask(dbModel);
         }
      } finally {
         chStmt.close();
      }
      return taskList;
   }
   public class Describe {
      public String name = "Unknown";
      public boolean nullable = false;
      public String type = "Unknown";
   }
}
