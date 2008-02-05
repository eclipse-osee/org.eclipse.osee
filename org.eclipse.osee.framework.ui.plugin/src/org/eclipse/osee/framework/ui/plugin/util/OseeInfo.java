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
package org.eclipse.osee.framework.ui.plugin.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * @author Donald G. Dunne
 */
public class OseeInfo {
   public static Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeInfo.class);
   private static final String GET_VALUE_SQL =
         "Select OSEE_VALUE FROM " + SkynetDatabase.OSEE_INFO_TABLE + " where OSEE_KEY=?";
   private static final String INSERT_KEY_VALUE_SQL =
         "INSERT INTO " + SkynetDatabase.OSEE_INFO_TABLE + "(OSEE_VALUE, OSEE_KEY) VALUES (?,?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM " + SkynetDatabase.OSEE_INFO_TABLE + " WHERE OSEE_KEY=?";

   public static final String SAVE_OUTFILE_IN_DB = "SAVE_OUTFILE_IN_DB";

   public static String getValue(String key) {
      String returnValue = "";
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_VALUE_SQL, SQL3DataType.VARCHAR, key);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            returnValue = rSet.getString("osee_value");
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, "Failed to get the value from table: OSEE_INFO.", ex);
      } finally {
         DbUtil.close(chStmt);
      }
      return returnValue;
   }

   public static void putValue(String key, String value) {
      try {
         ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, SQL3DataType.VARCHAR, key);
         ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, SQL3DataType.VARCHAR, key, SQL3DataType.VARCHAR,
               value);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

}
