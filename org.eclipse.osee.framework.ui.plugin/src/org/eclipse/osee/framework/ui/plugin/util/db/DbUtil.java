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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Ryan D. Brooks
 */
public final class DbUtil {
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DbUtil.class);

   public static void close(Statement statement) {
      if (statement != null) {
         try {
            statement.close();
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
   }

   public static void close(ConnectionHandlerStatement chStmt) {
      if (chStmt != null) {
         close(chStmt.getStatement());
      }
   }

   /**
    * Cause constraint checking to be deferred until the end of the current transaction.
    * 
    * @param connection
    * @throws SQLException
    */
   public static void deferConstraintChecking(Connection connection) throws SQLException {
      // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
      PreparedStatement statement = connection.prepareStatement("SET CONSTRAINTS ALL DEFERRED");

      statement.execute();
      statement.close();
   }
}
