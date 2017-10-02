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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatastoreTxCallable<T> extends AbstractDatastoreCallable<T> {

   protected AbstractDatastoreTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient) {
      super(logger, session, jdbcClient);
   }

   @Override
   public final T call() throws Exception {
      T value = null;
      onExecutionStart();
      try {
         InternalTxWork work = new InternalTxWork();
         getJdbcClient().runTransaction(work);
         value = work.getResult();
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

   protected abstract T handleTxWork(JdbcConnection connection) ;

   protected void handleTxException(Exception ex) {
      // Do nothing
   }

   protected void handleTxFinally()  {
      // Do nothing
   }

   private final class InternalTxWork extends JdbcTransaction {
      private T result;

      public T getResult() {
         return result;
      }

      @Override
      public void handleTxWork(JdbcConnection connection)  {
         result = AbstractDatastoreTxCallable.this.handleTxWork(connection);
      }

      @Override
      public void handleTxException(Exception ex) {
         AbstractDatastoreTxCallable.this.handleTxException(ex);
      }

      @Override
      public void handleTxFinally()  {
         AbstractDatastoreTxCallable.this.handleTxFinally();
      }
   };
}
