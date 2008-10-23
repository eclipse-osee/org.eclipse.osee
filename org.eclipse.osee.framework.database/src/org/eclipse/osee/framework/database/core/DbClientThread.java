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
package org.eclipse.osee.framework.database.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class DbClientThread extends Thread {
   protected Connection connection;
   protected DbInformation databaseService;

   public DbClientThread(String threadName, DbInformation databaseService) {
      this.setName(threadName);
      this.connection = null;
      this.databaseService = databaseService;
   }

   @Override
   public void run() {
      OseeLog.log(DatabaseActivator.class, Level.INFO, "Starting " + getName() + "...");
      try {
         connection = OseeDbConnection.getConnection(databaseService);
         processTask();
      } catch (Exception ex) {
         OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.getMessage(), ex);
      } catch (ExceptionInInitializerError ex) {
         OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.getMessage(), ex);
      } finally {
         if (connection != null) {
            try {
               OseeLog.log(DatabaseActivator.class, Level.INFO, "Closing " + getName() + " Connection...");
               connection.close();
            } catch (SQLException ex) {
               OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.getMessage(), ex);
            }
         }
      }
   }

   public abstract void processTask() throws Exception;
}
