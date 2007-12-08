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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Robert A. Fisher
 */
public class ExecuteBatcher {
   private Statement statement;
   private boolean needToExecuteBatch;

   /**
    * @param connection The connection for this object to use for acquiring statements
    */
   public ExecuteBatcher(Connection connection) {
      this.needToExecuteBatch = false;
      try {
         this.statement = connection.createStatement();
      } catch (SQLException ex) {
         throw new IllegalStateException(ex);
      }
   }

   public void execute(String sql) throws SQLException {
      statement.addBatch(sql);
      needToExecuteBatch = true;
   }

   public ResultSet executeQuery(String sql) throws SQLException {
      purge();
      return statement.executeQuery(sql);
   }

   public void purge() throws SQLException {
      if (needToExecuteBatch) {
         statement.executeBatch();
         needToExecuteBatch = false;
      }
   }
}
