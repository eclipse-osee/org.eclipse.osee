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
package org.eclipse.osee.cluster.hazelcast.internal;

import com.hazelcast.core.MapEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.osee.distributed.DistributedMap;
import org.eclipse.osee.distributed.Predicate;

/**
 * @author Roberto E. Escobar
 */
public class DistributedMapProxy<K, V> implements DistributedMap<K, V> {

   private final com.hazelcast.core.IMap<K, V> proxyObject;

   public DistributedMapProxy(com.hazelcast.core.IMap<K, V> proxyObject) {
      super();
      this.proxyObject = proxyObject;
   }

   @Override
   public Object getId() {
      return proxyObject.getId();
   }

   @Override
   public void dispose() {
      proxyObject.destroy();
   }

   @Override
   public String getName() {
      return proxyObject.getName();
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return proxyObject.putIfAbsent(key, value);
   }

   @Override
   public boolean remove(Object key, Object value) {
      return proxyObject.remove(key, value);
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      return proxyObject.replace(key, oldValue, newValue);
   }

   @Override
   public V replace(K key, V value) {
      return proxyObject.replace(key, value);
   }

   @Override
   public int size() {
      return proxyObject.size();
   }

   @Override
   public boolean isEmpty() {
      return proxyObject.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return proxyObject.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return proxyObject.containsValue(value);
   }

   @Override
   public V get(Object key) {
      return proxyObject.get(key);
   }

   @Override
   public V put(K key, V value) {
      return proxyObject.put(key, value);
   }

   @Override
   public V remove(Object key) {
      return proxyObject.remove(key);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      proxyObject.putAll(m);
   }

   @Override
   public void clear() {
      proxyObject.clear();
   }

   @Override
   public Set<K> keySet() {
      return proxyObject.keySet();
   }

   @Override
   public Collection<V> values() {
      return proxyObject.values();
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      return proxyObject.entrySet();
   }

   @Override
   public void flush() {
      proxyObject.flush();
   }

   @Override
   public Map<K, V> getAll(Set<K> keys) {
      return proxyObject.getAll(keys);
   }

   @Override
   public Future<V> getAsync(K key) {
      return proxyObject.getAsync(key);
   }

   @Override
   public Future<V> putAsync(K key, V value) {
      return proxyObject.putAsync(key, value);
   }

   @Override
   public Future<V> removeAsync(K key) {
      return proxyObject.removeAsync(key);
   }

   @Override
   public Object tryRemove(K key, long timeout, TimeUnit timeunit) throws TimeoutException {
      return proxyObject.tryRemove(key, timeout, timeunit);
   }

   @Override
   public boolean tryPut(K key, V value, long timeout, TimeUnit timeunit) {
      return proxyObject.tryPut(key, value, timeout, timeunit);
   }

   @Override
   public V put(K key, V value, long ttl, TimeUnit timeunit) {
      return proxyObject.put(key, value, ttl, timeunit);
   }

   @Override
   public void putTransient(K key, V value, long ttl, TimeUnit timeunit) {
      proxyObject.putTransient(key, value, ttl, timeunit);
   }

   @Override
   public V putIfAbsent(K key, V value, long ttl, TimeUnit timeunit) {
      return proxyObject.putIfAbsent(key, value, ttl, timeunit);
   }

   @Override
   public V tryLockAndGet(K key, long time, TimeUnit timeunit) throws TimeoutException {
      return proxyObject.tryLockAndGet(key, time, timeunit);
   }

   @Override
   public void putAndUnlock(K key, V value) {
      proxyObject.putAndUnlock(key, value);
   }

   @Override
   public void lock(K key) {
      proxyObject.lock(key);
   }

   @Override
   public boolean tryLock(K key) {
      return proxyObject.tryLock(key);
   }

   @Override
   public boolean tryLock(K key, long time, TimeUnit timeunit) {
      return proxyObject.tryLock(key, time, timeunit);
   }

   @Override
   public void unlock(K key) {
      proxyObject.unlock(key);
   }

   @Override
   public boolean lockMap(long time, TimeUnit timeunit) {
      return proxyObject.lockMap(time, timeunit);
   }

   @Override
   public void unlockMap() {
      proxyObject.unlockMap();
   }

   @Override
   public java.util.Map.Entry<K, V> getMapEntry(K key) {
      return proxyObject.getMapEntry(key);
   }

   @Override
   public boolean evict(Object key) {
      return proxyObject.evict(key);
   }

   @Override
   public Set<K> keySet(Predicate<K, V> predicate) {
      return proxyObject.keySet(toPredicate(predicate));
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet(Predicate<K, V> predicate) {
      return proxyObject.entrySet(toPredicate(predicate));
   }

   @Override
   public Collection<V> values(Predicate<K, V> predicate) {
      return proxyObject.values(toPredicate(predicate));
   }

   private com.hazelcast.query.Predicate<K, V> toPredicate(final Predicate<K, V> predicate) {
      com.hazelcast.query.Predicate<K, V> toReturn = new com.hazelcast.query.Predicate<K, V>() {

         private static final long serialVersionUID = 1L;

         @Override
         public boolean apply(MapEntry<K, V> mapEntry) {
            return predicate.apply(mapEntry);
         }
      };
      return toReturn;
   }

}
