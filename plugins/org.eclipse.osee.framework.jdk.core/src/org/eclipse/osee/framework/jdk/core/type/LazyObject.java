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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roberto E. Escobar
 */
public abstract class LazyObject<T> {

   private final AtomicReference<T> instanceReference = new AtomicReference<>();

   private final Object lock = new Object();
   private FutureTask<T> lastLoader;

   public final T get()  {
      T object = instanceReference.get();
      if (object == null) {
         FutureTask<T> task;
         synchronized (lock) {
            if (lastLoader != null) {
               task = lastLoader;
            } else {
               task = createLoaderTask();
               lastLoader = task;
               task.run();
            }
         }
         try {
            object = task.get();
            instanceReference.set(object);
         } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
               cause = ex;
            }
            throw OseeCoreException.wrap(cause);
         }
      }
      return object;
   }

   public final void invalidate() {
      synchronized (lock) {
         instanceReference.set(null);
         lastLoader = null;
      }
   }

   protected abstract FutureTask<T> createLoaderTask();

   protected Object getLock() {
      return lock;
   }
}