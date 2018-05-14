/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.console.admin.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractConsoleCallable<T> extends CancellableCallable<T> {

   private final Console console;
   private final ConsoleParameters params;
   private Callable<?> innerWorker;

   public AbstractConsoleCallable(Console console, ConsoleParameters params) {
      super();
      this.console = console;
      this.params = params;
   }

   @Override
   public final T call() throws Exception {
      long startTime = System.currentTimeMillis();
      T result;
      try {
         result = innerCall();
      } finally {
         console.writeln("Console Command - [%s] completed in [%s]", params.getRawString(),
            Lib.getElapseString(startTime));
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
         synchronized (inner) {
            if (inner instanceof HasCancellation) {
               ((HasCancellation) inner).setCancel(isCancelled);
            }
         }
      }
   }

}
