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
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.ui.plugin.util.db.DBConnection;

/**
 * @author Roberto E. Escobar
 */
public abstract class DbClientThread extends Thread {
   protected Connection connection;
   protected DbInformation databaseService;
   protected Logger logger;

   public DbClientThread(Logger logger, String threadName, DbInformation databaseService) {
      this.setName(threadName);
      this.connection = null;
      this.databaseService = databaseService;
      this.logger = logger;
   }

   public void run() {
      logger.log(Level.INFO, "Starting " + getName() + "...");
      try {
         connection = DBConnection.getNewConnection(databaseService, false);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

         processTask();

      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } catch (DatabaseNotSupportedException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } catch (ExceptionInInitializerError ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } finally {
         if (connection != null) {
            try {
               logger.log(Level.INFO, "Closing " + getName() + " Connection...");
               connection.close();
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
         }
      }
   }

   public abstract void processTask() throws SQLException, DatabaseNotSupportedException, Exception;
}
