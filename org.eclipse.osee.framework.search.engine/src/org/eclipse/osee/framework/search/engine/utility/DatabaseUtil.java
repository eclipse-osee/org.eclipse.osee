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
package org.eclipse.osee.framework.search.engine.utility;

import java.sql.Connection;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseUtil {

   public static void executeQuery(final Connection connection, final String sql, final IRowProcessor processor, final Object... data) throws Exception {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(connection, sql, data);
         while (chStmt.next()) {
            processor.processRow(chStmt.getRset());
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static void executeQueryInternalConnection(final String sql, final IRowProcessor processor, final Object... data) throws Exception {
      ConnectionHandlerStatement chStmt = null;
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         chStmt = ConnectionHandler.runPreparedQuery(connection, sql, data);
         while (chStmt.next()) {
            processor.processRow(chStmt.getRset());
         }
      } finally {
         DbUtil.close(chStmt);
         if (connection != null) {
            connection.close();
         }
      }
   }
}
