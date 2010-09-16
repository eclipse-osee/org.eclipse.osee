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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

public final class DatabaseTransactions {

   private DatabaseTransactions() {
      // Utility class
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
         saveException = OseeExceptions.wrap(ex);
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
