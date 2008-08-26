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
package org.eclipse.osee.framework.db.connection.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;

/**
 * @author Donald G. Dunne
 */
public class OseeInfo {
   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   public static final String SAVE_OUTFILE_IN_DB = "SAVE_OUTFILE_IN_DB";

   public static String getValue(String key) throws SQLException {
      String returnValue = "";
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_VALUE_SQL, key);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            returnValue = rSet.getString("osee_value");
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return returnValue;
   }

   public static void putValue(String key, String value) throws SQLException {
      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
   }
}