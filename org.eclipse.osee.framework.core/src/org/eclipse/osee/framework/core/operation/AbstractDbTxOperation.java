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
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDbTxOperation extends AbstractOperation {

   /**
    * @param operationName
    * @param pluginId
    */
   public AbstractDbTxOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      Transaction transaction = new Transaction(monitor);
      transaction.execute();
   }

   protected abstract void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException;

   private final class Transaction extends DbTransaction {
      private final IProgressMonitor monitor;

      private Transaction(IProgressMonitor monitor) throws OseeCoreException {
         super();
         this.monitor = monitor;
      }

      @Override
      protected String getTxName() {
         return AbstractDbTxOperation.this.getName();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxWork(org.eclipse.osee.framework.db.connection.OseeConnection)
       */
      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         AbstractDbTxOperation.this.doTxWork(monitor, connection);
      }
   }
}
