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
package org.eclipse.osee.framework.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public class MySqlLaunch {
   private static final int NUMBER_OF_TRIES = 10;
   private static final long SLEEP_TIME = 2000;
   private String installLocation;
   private IDatabaseInfo dbInfo;

   public MySqlLaunch(IDatabaseInfo dbInfo, String installLocation) {
      this.installLocation = installLocation;
      this.dbInfo = dbInfo;
   }

   private void startMySql() throws IOException {
      if (installLocation == null) {
         System.out.println("install location null");
         throw new IllegalArgumentException(
               "A `MySqlInfo` element in the oseeSiteConfig.xml file does not exist or has not specified a `InstallLocation` attribute.");
      }
      File exe = new File(installLocation, "bin/mysqld.exe");
      Runtime.getRuntime().exec(exe.getAbsolutePath());
   }

   public Connection getLocalMysqlConnection() throws IOException, InterruptedException {
      startMySql();
      Connection connection = null;
      for (int i = 0; i < NUMBER_OF_TRIES && connection == null; i++) {
         Thread.sleep(SLEEP_TIME);
         try {
            connection = OseeDbConnection.getConnection(dbInfo);
         } catch (OseeDataStoreException ex) {
         }
      }
      return connection;
   }
}
