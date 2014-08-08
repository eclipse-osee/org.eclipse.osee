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
         setConstraintChecking(dbService, connection, false);
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
                  try {
                     setConstraintChecking(dbService, connection, true);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(DatabaseTransactions.class, Level.WARNING, "Error while enabling constraint checking",
                        ex);
                  } finally {
                     connection.setAutoCommit(initialAutoCommit);
                     connection.close();
                  }
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

   private static void setConstraintChecking(IOseeDatabaseService dbService, OseeConnection connection, boolean enable) throws OseeCoreException {
      String cmd = null;
      SupportedDatabase dbType = SupportedDatabase.getDatabaseType(connection.getMetaData());
      switch (dbType) {
         case h2:
            cmd = String.format("SET REFERENTIAL_INTEGRITY = %s", Boolean.toString(enable).toUpperCase());
            break;
         case hsql:
            cmd = String.format("SET DATABASE REFERENTIAL INTEGRITY %s", Boolean.toString(enable).toUpperCase());
            break;
         default:
            // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
            cmd = String.format("SET CONSTRAINTS ALL %s", enable ? "IMMEDIATE" : "DEFERRED");
            break;
      }
      dbService.runPreparedUpdate(connection, cmd);
   }
}
