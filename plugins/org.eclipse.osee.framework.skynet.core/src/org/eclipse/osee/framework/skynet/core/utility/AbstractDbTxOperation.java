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
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDbTxOperation extends AbstractOperation {

   private final IOseeDatabaseService databaseService;

   public AbstractDbTxOperation(IOseeDatabaseService databaseService, String operationName, String pluginId) {
      this(databaseService, operationName, pluginId, NullOperationLogger.getSingleton());
   }

   public AbstractDbTxOperation(IOseeDatabaseService databaseService, String operationName, String pluginId, OperationLogger logger) {
      super(operationName, pluginId, logger);
      this.databaseService = databaseService;
   }

   protected IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      Transaction transaction = new Transaction(monitor);
      transaction.execute();
   }

   protected abstract void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException;

   protected void handleTxException(IProgressMonitor monitor, Exception ex) {
      // default implementation
   }

   //OseeCoreException is thrown by inheriting class
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      // default implementation
   }

   private final class Transaction extends DbTransaction {
      private final IProgressMonitor monitor;

      private Transaction(IProgressMonitor monitor) {
         this.monitor = monitor;
      }

      @Override
      protected String getTxName() {
         return AbstractDbTxOperation.this.getName();
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         AbstractDbTxOperation.this.doTxWork(monitor, connection);
      }

      @Override
      protected void handleTxException(Exception ex) {
         AbstractDbTxOperation.this.handleTxException(monitor, ex);
      }

      @Override
      protected void handleTxFinally() throws OseeCoreException {
         AbstractDbTxOperation.this.handleTxFinally(monitor);
      }
   }
}