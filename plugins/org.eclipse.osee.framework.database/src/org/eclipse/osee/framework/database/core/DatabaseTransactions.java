/*
 * Created on Jun 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.core;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

public final class DatabaseTransactions {

   private DatabaseTransactions() {
   }

   public static void execute(IDbTransactionWork dbWork) throws OseeCoreException {
      execute(ConnectionHandler.getConnection(), dbWork);
   }

   public static void execute(OseeConnection connection, IDbTransactionWork dbWork) throws OseeCoreException {
      boolean initialAutoCommit = true;
      OseeCoreException saveException = null;
      try {
         OseeLog.log(Activator.class, Level.FINEST, String.format("Start Transaction: [%s]", dbWork.getName()));

         initialAutoCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         ConnectionHandler.deferConstraintChecking(connection);
         dbWork.handleTxWork(connection);

         connection.commit();
         OseeLog.log(Activator.class, Level.FINEST, String.format("End Transaction: [%s]", dbWork.getName()));
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            saveException = (OseeCoreException) ex;
         } else {
            saveException = new OseeWrappedException(ex);
         }
         try {
            connection.rollback();
            connection.destroy();
         } finally {
            dbWork.handleTxException(ex);
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
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            if (saveException == null) {
               saveException = ex;
            }
         }

         if (saveException != null) {
            throw saveException;
         }
      }
   }

}
