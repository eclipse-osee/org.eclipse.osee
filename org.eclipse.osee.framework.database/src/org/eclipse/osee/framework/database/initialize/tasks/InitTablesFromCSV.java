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
package org.eclipse.osee.framework.database.initialize.tasks;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;

public class InitTablesFromCSV extends DbInitializationTask {

   private List<URL> csvTableInitData;
   private String schemaName;

   public InitTablesFromCSV(String schemaName, List<URL> csvTableInitData) {
      super();
      this.csvTableInitData = csvTableInitData;
      this.schemaName = schemaName;
   }

   public void run(Connection connection) throws Exception {
      for (URL file : csvTableInitData) {
         insertTableData(connection, schemaName, new File(file.getFile()));
      }
   }

   private void insertTableData(Connection connection, String schemaName, File file) throws SQLException {
      // SYSCS_UTIL.SYSCS_IMPORT_TABLE (
      // IN SCHEMANAME VARCHAR(128),
      // IN TABLENAME VARCHAR(128),
      // IN FILENAME VARCHAR(32672),
      // IN COLUMNDELIMITER CHAR(1),
      // IN CHARACTERDELIMITER CHAR(1),
      // IN CODESET VARCHAR(128),
      // IN REPLACE SMALLINT)
      String tableName = file.getName().replace(DbConfigFileInformation.getCSVFileExtension(), "").toUpperCase();
      PreparedStatement statement = null;
      try {
         statement = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
         statement.setString(1, schemaName);
         statement.setString(2, tableName);
         statement.setString(3, file.getAbsolutePath());
         statement.setNull(4, Types.CHAR);
         statement.setNull(5, Types.CHAR);
         statement.setNull(6, Types.VARCHAR);
         statement.setInt(7, 0);
         statement.execute();
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
   }
}
