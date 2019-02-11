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
package org.eclipse.osee.orcs.core.internal.admin;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractAdminCallable<T> extends CancellableCallable<T> {

   private final Log logger;
   private final OrcsSession session;
   private Callable<?> innerWorker;

   public AbstractAdminCallable(Log logger, OrcsSession session) {
      super();
      this.logger = logger;
      this.session = session;
   }

   protected Log getLogger() {
      return logger;
   }

   protected OrcsSession getSession() {
      return session;
   }

   @Override
   public final T call() throws Exception {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }
      T result;
      try {
         Conditions.checkNotNull(session, "session");
         result = innerCall();
      } finally {
         if (logger.isTraceEnabled()) {
            logger.trace("Admin [%s] completed in [%s]", getClass().getSimpleName(), Lib.getElapseString(startTime));
         }
      }
      return result;
   }

   protected abstract T innerCall() throws Exception;

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
         if (inner instanceof HasCancellation) {
            ((HasCancellation) inner).setCancel(isCancelled);
         }
      }
   }
}
