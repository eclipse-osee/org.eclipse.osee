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
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class CacheProxy<K, V> implements Cache<K, V> {

   private final LoadingCache<K, V> proxied;
   private final ReadDataAccessor<K, V> accessor;

   public CacheProxy(LoadingCache<K, V> proxied, ReadDataAccessor<K, V> accessor) {
      this.proxied = proxied;
      this.accessor = accessor;
   }

   @Override
   public Iterable<V> getAll() throws OseeCoreException {
      Iterable<? extends K> keys = accessor.getAllKeys();
      ImmutableCollection<V> toReturn = null;
      try {
         toReturn = proxied.getAll(keys).values();
      } catch (ExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (InvalidCacheLoadException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (UncheckedExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (ExecutionError ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public V get(K key) throws OseeCoreException {
      V toReturn = null;
      try {
         toReturn = proxied.get(key);
      } catch (ExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (InvalidCacheLoadException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (UncheckedExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (ExecutionError ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public void refresh(K key) {
      proxied.refresh(key);
   }

   @Override
   public void invalidateAll() {
      proxied.invalidateAll();
   }

   @Override
   public void invalidateAll(Iterable<? extends K> keys) {
      proxied.invalidateAll(keys);
   }

   @Override
   public void invalidate(K key) {
      proxied.invalidate(key);
   }

   @Override
   public long size() {
      return proxied.size();
   }

   @Override
   public V get(K key, Callable<? extends V> callable) throws OseeCoreException {
      V toReturn = null;
      try {
         toReturn = proxied.get(key, callable);
      } catch (ExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (InvalidCacheLoadException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (UncheckedExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (ExecutionError ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

}
