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

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.access.PermissionList;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

public class OseeInfoDbItem extends DbItem {

   public OseeInfoDbItem() {
      super("OSEE_INFO");
   }

   @Override
   public boolean isWriteable(String columnName) {
      return true;
   }

   @Override
   public boolean isBems(String columnName) {
      return false;
   }

   public String returnTic(String str) {
      return "'" + str + "'";
   }

   @Override
   public int getColumnWidth(String columnName) {
      return 100;
   }

   public boolean exists(String key) {
      try {
         String query = "SELECT * FROM " + getTableName() + " WHERE KEY = " + returnTic(key);
         ConnectionHandlerStatement chStmt = ConnectionHandler.runPreparedQuery(query);
         boolean b = chStmt.next();
         DbUtil.close(chStmt);
         return b;
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      return false;
   }

   @Override
   public void save(DbModel model) {
      try {
         String key = (String) model.getColumn(0);
         String value = (String) model.getColumn(1);
         String query;
         if (exists(key)) {
            query = "UPDATE " + getTableName() + " SET KEY = ?, VALUE = ? WHERE KEY = ?";
            ConnectionHandler.runPreparedUpdate(query, SQL3DataType.VARCHAR, key, SQL3DataType.VARCHAR, value,
                  SQL3DataType.VARCHAR, key);
         } else {
            query = "INSERT INTO " + getTableName() + " (key,value) VALUES (?, ?)";
            ConnectionHandler.runPreparedUpdate(query, SQL3DataType.VARCHAR, key, SQL3DataType.VARCHAR, value);
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
   }

   @Override
   public DbModel createNewRow(DbModel example) {
      DbModel dbModel = new DbModel();
      for (int x = 0; x < example.getValues().length; x++) {
         dbModel.addColumn(x, "");
      }
      dbModel.setColumn(0, "NEW");
      dbModel.setColumnChanged("KEY");
      return dbModel;
   }

   @Override
   public PermissionList getPermission() {
      PermissionList permissionList = new PermissionList();
      return permissionList;
   }
}
