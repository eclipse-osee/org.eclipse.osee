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
package org.eclipse.osee.framework.core.data;

import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;

/**
 * @author Roberto E. Escobar
 */
public abstract class LazyObject<T> {
   private final AtomicReference<FutureTask<T>> loaderReference = new AtomicReference<FutureTask<T>>();
   private final AtomicReference<T> instanceReference = new AtomicReference<T>();

   public final T get() throws OseeCoreException {
      T cache = instanceReference.get();
      if (cache == null) {
         FutureTask<T> newTask = createLoaderTask();
         if (loaderReference.compareAndSet(null, newTask)) {
            newTask.run();
         }
         FutureTask<T> task = loaderReference.get();
         try {
            cache = task.get();
            instanceReference.set(cache);
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return cache;
   }

   public final void invalidate() {
      loaderReference.set(null);
      instanceReference.set(null);
   }

   protected abstract FutureTask<T> createLoaderTask();

}