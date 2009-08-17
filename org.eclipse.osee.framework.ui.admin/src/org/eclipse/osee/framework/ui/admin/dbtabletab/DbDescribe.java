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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;

public class DbDescribe {

   private final DbItem dbItem;
   private ArrayList<Describe> dbColumns;

   public DbDescribe(DbItem dbItem) {
      super();
      this.dbItem = dbItem;
      this.dbColumns = null;
   }

   public int indexOfColumn(String name) throws OseeDataStoreException {
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

   public List<Describe> getDescription() throws OseeDataStoreException {
      if (dbColumns == null) {
         dbColumns = new ArrayList<Describe>();
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            String sql = "SELECT * FROM " + dbItem.getTableName();
            chStmt.runPreparedQuery(sql);
            int numberOfColumns = chStmt.getColumnCount() + 1;
            for (int columnIndex = 1; columnIndex < numberOfColumns; columnIndex++) {
               Describe describe = new Describe();

               describe.name = chStmt.getColumnName(columnIndex).toUpperCase();
               describe.nullable = chStmt.isNullable(columnIndex);
               describe.type = chStmt.getColumnTypeName(columnIndex).toUpperCase();
               dbColumns.add(describe);
            }
         } finally {
            chStmt.close();
         }
      }
      return dbColumns;
   }

   public DbTaskList getDbTaskList(List<Describe> describeList) throws OseeDataStoreException {
      DbTaskList taskList = new DbTaskList();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         String sql = "SELECT * FROM " + dbItem.getTableName();
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            DbModel dbModel = new DbModel();
            int x = 0;
            for (Describe d : describeList) {
               if (d.type.contains("NUMBER")) {
                  Long l = chStmt.getLong(d.name);
                  dbModel.addColumn(x++, l);
               } else if (d.type.contains("VARCHAR")) {
                  String value = chStmt.getString(d.name);
                  dbModel.addColumn(x++, value);
               } else if (d.type.contains("INT")) {
                  Integer value = chStmt.getInt(d.name);
                  dbModel.addColumn(x++, value);
               } else if (d.type.contains("TIMESTAMP")) {
                  Timestamp value = chStmt.getTimestamp(d.name);
                  dbModel.addColumn(x++, value);
               } else {
                  dbModel.addColumn(x++, "Unknown object type");
               }
            }
            taskList.addTask(dbModel);
         }
      } finally {
         chStmt.close();
      }
      return taskList;
   }
   public static class Describe {
      public String name = "Unknown";
      public boolean nullable = false;
      public String type = "Unknown";
   }
}
