/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.cache.admin;

import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class CacheConfiguration {

   private static final int DEFAULT_UNSET = -1;
   private static final Pair<Long, TimeUnit> UNSET_EXPIRATION = new Pair<>(-1L, TimeUnit.NANOSECONDS);

   private int initialCapacity = DEFAULT_UNSET;
   private long maxSize = DEFAULT_UNSET;

   private Pair<Long, TimeUnit> expireAfterAccess = UNSET_EXPIRATION;
   private Pair<Long, TimeUnit> expireAfterWrite = UNSET_EXPIRATION;
   private Pair<Long, TimeUnit> refreshAfterWrite = UNSET_EXPIRATION;

   private CacheConfiguration() {
      //
   }

   public static CacheConfiguration newConfiguration() {
      return new CacheConfiguration();
   }

   public boolean hasInitialCapacity() {
      return initialCapacity >= 0;
   }

   public void setInitialCapacity(int initialCapacity) {
      this.initialCapacity = initialCapacity;
   }

   public boolean hasMaximumSize() {
      return maxSize >= 0;
   }

   public void setMaximumSize(long maxSize) {
      this.maxSize = maxSize;
   }

   public boolean isExpireAfterAccess() {
      return !UNSET_EXPIRATION.equals(expireAfterAccess);
   }

   public void setExpireAfterAccess(long duration, TimeUnit timeUnit) {
      this.expireAfterAccess = new Pair<>(duration, timeUnit);
   }

   public boolean isExpireAfterWrite() {
      return !UNSET_EXPIRATION.equals(expireAfterWrite);
   }

   public void setExpireAfterWrite(long duration, TimeUnit timeUnit) {
      this.expireAfterWrite = new Pair<>(duration, timeUnit);
   }

   public boolean isRefreshAfterWrite() {
      return !UNSET_EXPIRATION.equals(refreshAfterWrite);
   }

   public void setRefreshAfterWrite(long duration, TimeUnit timeUnit) {
      this.refreshAfterWrite = new Pair<>(duration, timeUnit);
   }

   public int getInitialCapacity() {
      return initialCapacity;
   }

   public long getMaximumSize() {
      return maxSize;
   }

   public Pair<Long, TimeUnit> getExpireAfterAccess() {
      return expireAfterAccess;
   }

   public Pair<Long, TimeUnit> getExpireAfterWrite() {
      return expireAfterWrite;
   }

   public Pair<Long, TimeUnit> getRefreshAfterWrite() {
      return refreshAfterWrite;
   }

}