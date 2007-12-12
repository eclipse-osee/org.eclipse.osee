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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;

public class DbDescribe {

   private final DbItem dbItem;

   public DbDescribe(DbItem dbItem) {
      super();
      this.dbItem = dbItem;
   }

   public void open() throws SQLException {
   }

   public void close() throws SQLException {

   }

   public ArrayList<Describe> getDescription() throws SQLException {
      ArrayList<Describe> desc = new ArrayList<Describe>();
      open();
      String sql =
            "SELECT  column_name, nullable, " + " concat(concat(concat(data_type,'('),data_length),')') \"type\"" + " FROM " + " user_tab_columns" + " WHERE " + " table_name='" + dbItem.getTableName() + "'";
      System.out.println("sql *" + sql + "*");
      Query.acquireCollection(desc, sql, new RsetProcessor<Describe>() {

         public Describe process(ResultSet rset) throws SQLException {
            return processDescribeRsetLine(rset);
         }

         public boolean validate(Describe d) {
            return d.name != null;
         }

      });
      close();
      return desc;
   }

   public DbTaskList getDbTaskList(ArrayList<Describe> describeList) throws SQLException {
      DbTaskList taskList = new DbTaskList();
      ConnectionHandlerStatement chStmt;
      open();
      String sql = "SELECT * FROM " + dbItem.getTableName();
      System.out.println("sql *" + sql + "*");
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
            } else {
               dbModel.addColumn(x++, new String("Unknown object type"));
            }
         }
         taskList.addTask(dbModel);
      }
      DbUtil.close(chStmt);
      close();
      return taskList;
   }

   public class Describe {
      public String name = "Unknown";
      public boolean nullable = false;
      public String type = "Unknown";

   }

   private Describe processDescribeRsetLine(ResultSet rset) throws SQLException {
      Describe describe = new Describe();
      if (rset == null) return null;
      describe.name = rset.getString(1);
      String nullable = rset.getString(2);
      if (nullable != null) describe.nullable = nullable.equals("Y");
      describe.type = rset.getString(3);
      return describe;
   }

}
