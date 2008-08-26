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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.skynet.core.access.PermissionList;

public class SiteGssflRpcr extends DbItem {

   public SiteGssflRpcr() {
      super("OSEE_SITE_GSSFL_RPCR");
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
      if (columnName.equals("DIRECTORY")) return 400;
      return 100;
   }

   public boolean exists(String program) {
      ConnectionHandlerStatement chStmt;
      try {
         String query = "SELECT * FROM " + getTableName() + " WHERE PROGRAM = " + returnTic(program);
         chStmt = ConnectionHandler.runPreparedQuery(query);

         boolean b = chStmt.next();
         DbUtil.close(chStmt);
         return (b);
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      return false;

   }

   @Override
   public void save(DbDescribe describe, DbModel model) {
      try {
         String program = (String) model.getColumn(describe.indexOfColumn("PROGRAM"));
         String dir = (String) model.getColumn(describe.indexOfColumn("DIRECTORY"));
         String programId = (String) model.getColumn(describe.indexOfColumn("PROGRAM_ID"));
         String query;
         if (exists(program)) {
            query = "UPDATE " + getTableName() + " SET directory = ?, program_id = ? WHERE PROGRAM = ?";
            ConnectionHandler.runPreparedUpdate(query, dir, programId, program);
         } else {
            query = "INSERT INTO " + getTableName() + " (program,directory,program_id) VALUES (?,?,?)";
            ConnectionHandler.runPreparedUpdate(query, program, dir, programId);
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
      dbModel.setColumnChanged("PROGRAM");
      return dbModel;
   }

   @Override
   public PermissionList getPermission() {
      PermissionList permissionList = new PermissionList();
      //      permissionList.addPermission(Permission.PermPermissionEnum.GSSFLWRITE);
      return permissionList;
   }
}
