/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatastoreTxCallable<T> extends AbstractDatastoreCallable<T> {

   private final String name;

   protected AbstractDatastoreTxCallable(Log logger, OrcsSession session, IOseeDatabaseService dbService, String name) {
      super(logger, session, dbService);
      this.name = name;
   }

   @Override
   public final T call() throws Exception {
      T value = null;
      onExecutionStart();
      try {
         OseeConnection connection = getDatabaseService().getConnection();
         try {
            InternalTxWork work = new InternalTxWork();
            DatabaseTransactions.execute(getDatabaseService(), connection, work);
            value = work.getResult();
         } finally {
            connection.close();
         }
      } finally {
         onExecutionComplete();
      }
      return value;
   }

   protected void onExecutionStart() {
      // 
   }

   protected void onExecutionComplete() {
      // 
   }

   protected abstract T handleTxWork(OseeConnection connection) throws OseeCoreException;

   protected void handleTxException(Exception ex) {
      // Do nothing
   }

   @SuppressWarnings("unused")
   protected void handleTxFinally() throws OseeCoreException {
      // Do nothing
   }

   private final class InternalTxWork implements IDbTransactionWork {
      private T result;

      @Override
      public String getName() {
         return name;
      }

      public T getResult() {
         return result;
      }

      @Override
      public void handleTxWork(OseeConnection connection) throws OseeCoreException {
         result = AbstractDatastoreTxCallable.this.handleTxWork(connection);
      }

      @Override
      public void handleTxException(Exception ex) {
         AbstractDatastoreTxCallable.this.handleTxException(ex);
      }

      @Override
      public void handleTxFinally() throws OseeCoreException {
         AbstractDatastoreTxCallable.this.handleTxFinally();
      }
   };
}
