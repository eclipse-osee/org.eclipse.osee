/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.cache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeLoadingCache<T extends NamedId> extends AbstractOseeCache<T> implements IOseeLoadingCache<T> {
   private final IOseeDataAccessor<T> dataAccessor;
   private final AtomicBoolean wasLoaded;
   private long lastLoaded;

   protected AbstractOseeLoadingCache(IOseeDataAccessor<T> dataAccessor) {
      super();
      this.lastLoaded = 0;
      this.wasLoaded = new AtomicBoolean(false);
      this.dataAccessor = dataAccessor;
   }

   @Override
   public long getLastLoaded() {
      return lastLoaded;
   }

   private void setLastLoaded(long lastLoaded) {
      this.lastLoaded = lastLoaded;
   }

   @Override
   public boolean isLoaded() {
      return wasLoaded.get();
   }

   @Override
   public void invalidate() {
      wasLoaded.set(false);
   }

   @Override
   public synchronized void decacheAll() {
      super.decacheAll();
      invalidate();
   }

   @Override
   public synchronized void ensurePopulated() {
      if (wasLoaded.compareAndSet(false, true)) {
         try {
            reloadCache();
         } catch (OseeCoreException ex) {
            wasLoaded.set(false);
            throw ex;
         }
      }
   }

   @Override
   public synchronized boolean reloadCache() {
      dataAccessor.load(this);
      OseeLog.log(this.getClass(), Level.INFO, "Loaded " + getClass());
      setLastLoaded(System.currentTimeMillis());
      wasLoaded.set(true);
      return true;
   }
}