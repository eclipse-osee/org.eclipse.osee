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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDbTxOperation extends AbstractOperation {

   private final JdbcClient jdbcClient;

   public AbstractDbTxOperation(JdbcClient jdbcClient, String operationName, String pluginId) {
      this(jdbcClient, operationName, pluginId, NullOperationLogger.getSingleton());
   }

   public AbstractDbTxOperation(JdbcClient jdbcClient, String operationName, String pluginId, OperationLogger logger) {
      super(operationName, pluginId, logger);
      this.jdbcClient = jdbcClient;
   }

   protected JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      getJdbcClient().runTransaction(new Transaction(monitor));
   }

   protected abstract void doTxWork(IProgressMonitor monitor, JdbcConnection connection) ;

   protected void handleTxException(IProgressMonitor monitor, Exception ex) {
      // default implementation
   }

   //OseeCoreException is thrown by inheriting class
   protected void handleTxFinally(IProgressMonitor monitor)  {
      // default implementation
   }

   private final class Transaction extends JdbcTransaction {
      private final IProgressMonitor monitor;

      private Transaction(IProgressMonitor monitor) {
         this.monitor = monitor;
      }

      @Override
      public void handleTxWork(JdbcConnection connection) {
         AbstractDbTxOperation.this.doTxWork(monitor, connection);
      }

      @Override
      public void handleTxException(Exception ex) {
         AbstractDbTxOperation.this.handleTxException(monitor, ex);
      }

      @Override
      public void handleTxFinally() {
         AbstractDbTxOperation.this.handleTxFinally(monitor);
      }
   }
}