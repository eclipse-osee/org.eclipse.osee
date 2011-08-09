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
package org.eclipse.osee.cluster.admin.hazelcast.internal;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.distributed.DistributedMultiMap;

/**
 * @author Roberto E. Escobar
 */
public class DistributedMultiMapProxy<K, V> implements DistributedMultiMap<K, V> {

   private final com.hazelcast.core.MultiMap<K, V> proxyObject;

   public DistributedMultiMapProxy(com.hazelcast.core.MultiMap<K, V> proxyObject) {
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
   public boolean put(K key, V value) {
      return proxyObject.put(key, value);
   }

   @Override
   public Collection<V> get(K key) {
      return proxyObject.get(key);
   }

   @Override
   public boolean remove(Object key, Object value) {
      return proxyObject.remove(key, value);
   }

   @Override
   public Collection<V> remove(Object key) {
      return proxyObject.remove(key);
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
   public Set<Entry<K, V>> entrySet() {
      return proxyObject.entrySet();
   }

   @Override
   public boolean containsKey(K key) {
      return proxyObject.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return proxyObject.containsValue(value);
   }

   @Override
   public boolean containsEntry(K key, V value) {
      return proxyObject.containsEntry(key, value);
   }

   @Override
   public int size() {
      return proxyObject.size();
   }

   @Override
   public void clear() {
      proxyObject.clear();
   }

   @Override
   public int valueCount(K key) {
      return proxyObject.valueCount(key);
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

}
