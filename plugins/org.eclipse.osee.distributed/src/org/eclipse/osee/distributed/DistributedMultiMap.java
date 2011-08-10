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
package org.eclipse.osee.distributed;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Roberto E. Escobar
 */
public interface DistributedMultiMap<K, V> extends DistributedCollection {

   /**
    * Stores a key-value pair in the multimap.
    * 
    * @param key the key to be stored
    * @param value the value to be stored
    * @return true if size of the multimap is increased, false if the multimap already contains the key-value pair.
    */
   boolean put(K key, V value);

   /**
    * Returns the collection of values associated with the key.
    * 
    * @param key the key whose associated values are to be returned
    * @return the collection of the values associated with the key.
    */
   Collection<V> get(K key);

   /**
    * Removes the given key value pair from the multimap.
    * 
    * @param key the key of the entry to remove
    * @param value the value of the entry to remove
    * @return true if the size of the multimap changed after the remove operation, false otherwise.
    */
   boolean remove(Object key, Object value);

   /**
    * Removes all the entries with the given key.
    * 
    * @param key the key of the entries to remove
    * @return the collection of removed values associated with the given key. Returned collection might be modifiable
    * but it has no effect on the multimap
    */
   Collection<V> remove(Object key);

   /**
    * Returns the set of keys in the multimap.
    * 
    * @return the set of keys in the multimap. Returned set might be modifiable but it has no effect on the multimap
    */
   Set<K> keySet();

   /**
    * Returns the collection of values in the multimap.
    * 
    * @return the collection of values in the multimap. Returned collection might be modifiable but it has no effect on
    * the multimap
    */
   Collection<V> values();

   /**
    * Returns the set of key-value pairs in the multimap.
    * 
    * @return the set of key-value pairs in the multimap. Returned set might be modifiable but it has no effect on the
    * multimap
    */
   Set<Map.Entry<K, V>> entrySet();

   /**
    * Returns whether the multimap contains an entry with the key.
    * 
    * @param key the key whose existence is checked.
    * @return true if the multimap contains an entry with the key, false otherwise.
    */
   boolean containsKey(K key);

   /**
    * Returns whether the multimap contains an entry with the value.
    * 
    * @param value the value whose existence is checked.
    * @return true if the multimap contains an entry with the value, false otherwise.
    */
   boolean containsValue(Object value);

   /**
    * Returns whether the multimap contains the given key-value pair.
    * 
    * @param key the key whose existence is checked.
    * @param value the value whose existence is checked.
    * @return true if the multimap contains the key-value pair, false otherwise.
    */
   boolean containsEntry(K key, V value);

   /**
    * Returns the number of key-value pairs in the multimap.
    * 
    * @return the number of key-value pairs in the multimap.
    */
   int size();

   /**
    * Clears the multimap. Removes all key-value pairs.
    */
   void clear();

   /**
    * Returns number of values matching to given key in the multimap.
    * 
    * @param key the key whose values count are to be returned
    * @return number of values matching to given key in the multimap.
    */
   int valueCount(K key);

   /**
    * Acquires the lock for the specified key.
    * <p>
    * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
    * dormant until the lock has been acquired.
    * <p/>
    * Scope of the lock is this multimap only. Acquired lock is only for the key in this multimap.
    * <p/>
    * Locks are re-entrant so if the key is locked N times then it should be unlocked N times before another thread can
    * acquire it.
    * 
    * @param key key to lock.
    */
   void lock(K key);

   /**
    * Tries to acquire the lock for the specified key.
    * <p>
    * If the lock is not available then the current thread doesn't wait and returns false immediately.
    * 
    * @param key key to lock.
    * @return <tt>true</tt> if lock is acquired, <tt>false</tt> otherwise.
    */
   boolean tryLock(K key);

   /**
    * Tries to acquire the lock for the specified key.
    * <p>
    * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
    * dormant until one of two things happens:
    * <ul>
    * <li>The lock is acquired by the current thread; or
    * <li>The specified waiting time elapses
    * </ul>
    * 
    * @param time the maximum time to wait for the lock
    * @param timeunit the time unit of the <tt>time</tt> argument.
    * @return <tt>true</tt> if the lock was acquired and <tt>false</tt> if the waiting time elapsed before the lock was
    * acquired.
    */
   boolean tryLock(K key, long time, TimeUnit timeunit);

   /**
    * Releases the lock for the specified key. It never blocks and returns immediately.
    * 
    * @param key key to lock.
    */
   void unlock(K key);

   /**
    * Tries to acquire the lock for the entire map. The thread that locks the map can do all the operations but other
    * threads in the cluster cannot operate on the map.
    * <p>
    * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
    * dormant until one of two things happens:
    * <ul>
    * <li>The lock is acquired by the current thread; or
    * <li>The specified waiting time elapses
    * </ul>
    * 
    * @param time the maximum time to wait for the lock
    * @param timeunit the time unit of the <tt>time</tt> argument.
    * @return <tt>true</tt> if the lock was acquired and <tt>false</tt> if the waiting time elapsed before the lock was
    * acquired.
    */
   boolean lockMap(long time, TimeUnit timeunit);

   /**
    * Unlocks the map. It never blocks and returns immediately.
    */
   void unlockMap();

}
