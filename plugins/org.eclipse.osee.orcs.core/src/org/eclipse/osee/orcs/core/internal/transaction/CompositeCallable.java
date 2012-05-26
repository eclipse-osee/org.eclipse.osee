package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

public class CompositeCallable extends CancellableCallable<Boolean> {

   private final List<Callable<?>> callables;
   private final Log logger;
   private Callable<?> innerWorker;

   public CompositeCallable(Log logger, List<Callable<?>> callables) {
      super();
      this.logger = logger;
      this.callables = callables;
   }

   protected Log getLogger() {
      return logger;
   }

   @Override
   public final Boolean call() throws Exception {
      long startTime = 0;
      if (getLogger().isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }
      try {
         for (Callable<?> callable : callables) {
            callAndCheckForCancel(callable);
         }
      } finally {
         if (getLogger().isTraceEnabled()) {
            getLogger().trace("Admin [%s] completed in [%s]", getClass().getSimpleName(),
               Lib.getElapseString(startTime));
         }
      }
      return Boolean.TRUE;
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