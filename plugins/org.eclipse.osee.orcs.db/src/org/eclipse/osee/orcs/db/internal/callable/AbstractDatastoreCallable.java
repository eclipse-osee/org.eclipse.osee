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

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatastoreCallable<T> extends CancellableCallable<T> {

   private final OrcsSession session;
   private final JdbcClient jdbcClient;
   private final Log logger;
   private Callable<?> innerWorker;

   protected AbstractDatastoreCallable(Log logger, OrcsSession session, JdbcClient jdbcClient) {
      this.logger = logger;
      this.session = session;
      this.jdbcClient = jdbcClient;
   }

   protected OrcsSession getSession() {
      return session;
   }

   protected JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   protected Log getLogger() {
      return logger;
   }

   protected <K> K callAndCheckForCancel(Callable<K> callable) throws Exception {
      checkForCancelled();
      setInnerWorker(callable);
      K result = callable.call();
      setInnerWorker(null);
      return result;
   }

   private synchronized void setInnerWorker(Callable<?> callable) {
      innerWorker = callable;
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      final Callable<?> inner = innerWorker;
      if (inner != null) {
         synchronized (inner) {
            if (inner instanceof HasCancellation) {
               ((HasCancellation) inner).setCancel(isCancelled);
            }
         }
      }
   }
}