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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public abstract class LazyObject<T> {
   private volatile T val;

   T result = val;

   public T get() throws OseeCoreException {
      if (result == null) {
         synchronized (this) {
            result = val;
            if (result == null) {
               val = result = instance();
            }
         }
      }
      return result;
   }

   public void set(T t) {
      val = t;
   }

   protected void invalidate() {
      synchronized (this) {
         val = result = null;
      }
   }

   protected abstract T instance() throws OseeCoreException;
}