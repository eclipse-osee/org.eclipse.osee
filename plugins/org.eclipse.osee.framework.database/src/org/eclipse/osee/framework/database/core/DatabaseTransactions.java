/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.core;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

public final class DatabaseTransactions {

   private DatabaseTransactions() {
      // Utility class
   }

   public static void execute(IDbTransactionWork dbWork) throws OseeCoreException {
      execute(ConnectionHandler.getDatabase(), ConnectionHandler.getConnection(), dbWork);
   }

   public static void execute(IOseeDatabaseService dbService, OseeConnection connection, IDbTransactionWork dbWork) throws OseeCoreException {
      boolean initialAutoCommit = true;
      Exception saveException = null;
      try {
         OseeLog.logf(DatabaseTransactions.class, Level.FINEST, "Start Transaction: [%s]", dbWork.getName());

         initialAutoCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         deferConstraintChecking(dbService, connection);
         dbWork.handleTxWork(connection);

         connection.commit();
         OseeLog.logf(DatabaseTransactions.class, Level.FINEST, "End Transaction: [%s]", dbWork.getName());
      } catch (Exception ex) {
         saveException = ex;
         try {
            connection.rollback();
         } finally {
            try {
               connection.destroy();
            } finally {
               dbWork.handleTxException(ex);
            }
         }
      } finally {
         try {
            try {
               if (!connection.isClosed()) {
                  connection.setAutoCommit(initialAutoCommit);
                  connection.close();
               }
            } finally {
               dbWork.handleTxFinally();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(DatabaseTransactions.class, Level.SEVERE, ex);
            if (saveException == null) {
               saveException = ex;
            }
         }

         if (saveException != null) {
            OseeExceptions.wrapAndThrow(saveException);
         }
      }
   }

   private static void deferConstraintChecking(IOseeDatabaseService dbService, OseeConnection connection) throws OseeCoreException {
      SupportedDatabase dbType = SupportedDatabase.getDatabaseType(connection.getMetaData());
      switch (dbType) {
         case h2:
            dbService.runPreparedUpdate(connection, "SET REFERENTIAL_INTEGRITY = FALSE");
            break;
         case hsql:
            dbService.runPreparedUpdate(connection, "SET DATABASE REFERENTIAL INTEGRITY FALSE");
            break;
         default:
            // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
            dbService.runPreparedUpdate(connection, "SET CONSTRAINTS ALL DEFERRED");
            break;
      }
   }
}
