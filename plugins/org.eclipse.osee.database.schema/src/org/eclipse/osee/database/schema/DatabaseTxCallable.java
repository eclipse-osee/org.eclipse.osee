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
package org.eclipse.osee.database.schema;

import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class DatabaseTxCallable<T> extends DatabaseCallable<T> {

   private final String name;

   protected DatabaseTxCallable(Log logger, IOseeDatabaseService dbService, String name) {
      super(logger, dbService);
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
         result = DatabaseTxCallable.this.handleTxWork(connection);
      }

      @Override
      public void handleTxException(Exception ex) {
         DatabaseTxCallable.this.handleTxException(ex);
      }

      @Override
      public void handleTxFinally() throws OseeCoreException {
         DatabaseTxCallable.this.handleTxFinally();
      }
   };
}
