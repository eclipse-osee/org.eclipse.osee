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

import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class DatabaseCallable<T> extends CancellableCallable<T> {

   private final IOseeDatabaseService service;
   private final Log logger;
   private Callable<?> innerWorker;

   protected DatabaseCallable(Log logger, IOseeDatabaseService service) {
      this.logger = logger;
      this.service = service;
   }

   protected IOseeDatabaseService getDatabaseService() {
      return service;
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
            if (inner instanceof CancellableCallable) {
               ((CancellableCallable<?>) inner).setCancel(isCancelled);
            }
         }
      }
   }

}
