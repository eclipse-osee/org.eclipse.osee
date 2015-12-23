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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Roberto E. Escobar
 */
public interface DistributedMap<K, V> extends ConcurrentMap<K, V>, DistributedCollection {

   /**
    * If this map has a MapStore and write-delay-seconds is bigger than 0 (write-behind) then this method flushes all
    * the local dirty entries by calling MapStore.storeAll()
    */
   void flush();

   /**
    * Returns the entries for the given keys.
    * 
    * @param keys keys to get
    * @return map of entries
    */
   Map<K, V> getAll(Set<K> keys);

   /**
    * Asynchronously gets the given key. <code>
    * Future future = map.getAsync(key);
    * // do some other stuff, when ready get the result
    * Object value = future.get();
    * </code> Future.get() will block until the actual map.get() completes. If the application requires timely response,
    * then Future.get(timeout, timeunit) can be used. <code>
    * try{
    * Future future = map.getAsync(key);
    * Object value = future.get(40, TimeUnit.MILLISECOND);
    * }catch (TimeoutException t) {
    * // time wasn't enough
    * }
    * </code> ExecutionException is never thrown.
    * 
    * @param key the key of the map entry
    * @return Future from which the value of the key can be retrieved.
    * @see java.util.concurrent.Future
    */
   Future<V> getAsync(K key);

   /**
    * Asynchronously puts the given key and value. <code>
    * Future future = map.putAsync(key, value);
    * // do some other stuff, when ready get the result
    * Object oldValue = future.get();
    * </code> Future.get() will block until the actual map.get() completes. If the application requires timely response,
    * then Future.get(timeout, timeunit) can be used. <code>
    * try{
    * Future future = map.putAsync(key, newValue);
    * Object oldValue = future.get(40, TimeUnit.MILLISECOND);
    * }catch (TimeoutException t) {
    * // time wasn't enough
    * }
    * </code> ExecutionException is never thrown.
    * 
    * @param key the key of the map entry
    * @param value the new value of the map entry
    * @return Future from which the old value of the key can be retrieved.
    * @see java.util.concurrent.Future
    */
   Future<V> putAsync(K key, V value);

   /**
    * Asynchronously removes the given key.
    * 
    * @param key The key of the map entry to remove.
    * @return A {@link java.util.concurrent.Future} from which the value removed from the map can be retrieved.
    */
   Future<V> removeAsync(K key);

   /**
    * Tries to remove the entry with the given key from this map within specified timeout value. If the key is already
    * locked by another thread and/or member, then this operation will wait timeout amount for acquiring the lock.
    * 
    * @param key key of the entry
    * @param timeout maximum time to wait for acquiring the lock for the key
    * @param timeunit time unit for the timeout
    * @return removed value of the entry
    * @throws java.util.concurrent.TimeoutException if lock cannot be acquired for the given key within timeout
    */
   Object tryRemove(K key, long timeout, TimeUnit timeunit) throws TimeoutException;

   /**
    * Tries to put the given key, value into this map within specified timeout value. If this method returns false, it
    * means that the caller thread couldn't acquire the lock for the key within timeout duration, thus put operation is
    * not successful.
    * 
    * @param key key of the entry
    * @param value value of the entry
    * @param timeout maximum time to wait
    * @param timeunit time unit for the timeout
    * @return <tt>true</tt> if the put is successful, <tt>false</tt> otherwise.
    */
   boolean tryPut(K key, V value, long timeout, TimeUnit timeunit);

   /**
    * Puts an entry into this map with a given ttl (time to live) value. Entry will expire and get evicted after the
    * ttl.
    * 
    * @param key key of the entry
    * @param value value of the entry
    * @param ttl maximum time for this entry to stay in the map
    * @param timeunit time unit for the ttl
    * @return old value of the entry
    */
   V put(K key, V value, long ttl, TimeUnit timeunit);

   /**
    * Same as {@link #put(K, V, long, TimeUnit)} but MapStore, if defined, will not be called to store/persist the
    * entry.
    * 
    * @param key key of the entry
    * @param value value of the entry
    * @param ttl maximum time for this entry to stay in the map
    * @param timeunit time unit for the ttl
    * @return old value of the entry
    */
   void putTransient(K key, V value, long ttl, TimeUnit timeunit);

   /**
    * Puts an entry into this map with a given ttl (time to live) value if the specified key is not already associated
    * with a value. Entry will expire and get evicted after the ttl.
    * 
    * @param key key of the entry
    * @param value value of the entry
    * @param ttl maximum time for this entry to stay in the map
    * @param timeunit time unit for the ttl
    * @return old value of the entry
    */
   V putIfAbsent(K key, V value, long ttl, TimeUnit timeunit);

   /**
    * Tries to acquire the lock for the specified key and returns the value of the key if lock is required in time.
    * <p>
    * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
    * dormant until one of two things happens:
    * <ul>
    * <li>The lock is acquired by the current thread; or
    * <li>The specified waiting time elapses
    * </ul>
    * 
    * @param key key of the entry
    * @param time maximum time to wait for the lock
    * @param timeunit time unit of the <tt>time</tt> argument.
    * @return value of the key in this map
    * @throws java.util.concurrent.TimeoutException if lock cannot be acquired in time.
    */
   V tryLockAndGet(K key, long time, TimeUnit timeunit) throws TimeoutException;

   /**
    * Puts the key and value into this map and unlocks the key if the calling thread owns the lock.
    * 
    * @param key key of the entry
    * @param value value of the entry
    */
   void putAndUnlock(K key, V value);

   /**
    * Acquires the lock for the specified key.
    * <p>
    * If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies
    * dormant until the lock has been acquired.
    * <p/>
    * Scope of the lock is this map only. Acquired lock is only for the key in this map.
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
    * @param key key to lock in this map
    * @param time maximum time to wait for the lock
    * @param timeunit time unit of the <tt>time</tt> argument.
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
    * @param time maximum time to wait for the lock
    * @param timeunit time unit of the <tt>time</tt> argument.
    * @return <tt>true</tt> if the lock was acquired and <tt>false</tt> if the waiting time elapsed before the lock was
    * acquired.
    */
   boolean lockMap(long time, TimeUnit timeunit);

   /**
    * Unlocks the map. It never blocks and returns immediately.
    */
   void unlockMap();

   /**
    * Returns the <tt>Map.Entry</tt> for the specified key.
    * 
    * @param key key of the entry
    * @return <tt>MapEntry</tt> of the specified key
    * @see MapEntry
    */
   Map.Entry<K, V> getMapEntry(K key);

   /**
    * Evicts the specified key from this map. If a <tt>MapStore</tt> defined for this map, then the entry is not deleted
    * from the underlying <tt>MapStore</tt>, evict only removes the entry from the memory.
    * 
    * @param key key to evict
    * @return <tt>true</tt> if the key is evicted, <tt>false</tt> otherwise.
    */
   boolean evict(Object key);

   /**
    * Queries the map based on the specified predicate and returns the keys of matching entries.
    * <p/>
    * Specified predicate runs on all members in parallel.
    * 
    * @param predicate query criteria
    * @return result key set of the query
    */
   Set<K> keySet(Predicate<K, V> predicate);

   /**
    * Queries the map based on the specified predicate and returns the matching entries.
    * <p/>
    * Specified predicate runs on all members in parallel.
    * 
    * @param predicate query criteria
    * @return result entry set of the query
    */
   Set<Map.Entry<K, V>> entrySet(Predicate<K, V> predicate);

   /**
    * Queries the map based on the specified predicate and returns the values of matching entries.
    * <p/>
    * Specified predicate runs on all members in parallel.
    * 
    * @param predicate query criteria
    * @return result value collection of the query
    */
   Collection<V> values(Predicate<K, V> predicate);

   //   /**
   //    * Adds an index to this map for the specified entries so that queries can run faster.
   //    * <p/>
   //    * Let's say your map values are Employee objects.
   //    *
   //    * <pre>
   //    * public class Employee implements Serializable {
   //    *    private boolean active = false;
   //    *    private int age;
   //    *    private String name = null;
   //    *    // other fields.
   //    *
   //    *    // getters setter
   //    *
   //    * }
   //    * </pre>
   //    * <p/>
   //    * If you are querying your values mostly based on age and active then you should consider indexing these fields.
   //    *
   //    * <pre>
   //    * IMap imap = Hazelcast.getMap(&quot;employees&quot;);
   //    * imap.addIndex(&quot;age&quot;, true); // ordered, since we have ranged queries for this field
   //    * imap.addIndex(&quot;active&quot;, false); // not ordered, because boolean field cannot have range
   //    * </pre>
   //    * <p/>
   //    * Index attribute should either have a getter method or be public. You should also make sure to add the indexes
   //    * before adding entries to this map.
   //    *
   //    * @param attribute attribute of value
   //    * @param ordered <tt>true</tt> if index should be ordered, <tt>false</tt> otherwise.
   //    */
   //   void addIndex(String attribute, boolean ordered);
   //
   //   /**
   //    * Adds an index to this map based on the provided expression.
   //    *
   //    * @param expression expression for the index.
   //    * @param ordered <tt>true</tt> if index should be ordered, <tt>false</tt> otherwise.
   //    */
   //   void addIndex(Expression<?> expression, boolean ordered);
   //   /**
   //    * Returns LocalMapStats for this map. LocalMapStats is the statistics for the local portion of this distributed map
   //    * and contains information such as ownedEntryCount backupEntryCount, lastUpdateTime, lockedEntryCount.
   //    * <p/>
   //    * Since this stats are only for the local portion of this map, if you need the cluster-wide MapStats then you need
   //    * to get the LocalMapStats from all members of the cluster and combine them.
   //    *
   //    * @return this map's local statistics.
   //    */
   //   LocalMapStats getLocalMapStats();

   //   /**
   //    * Adds a local entry listener for this map. Added listener will be only listening for the events
   //    * (add/remove/update/evict) of the locally owned entries.
   //    * <p/>
   //    * Note that entries in distributed map are partitioned across the cluster members; each member owns and manages the
   //    * some portion of the entries. Owned entries are called local entries. This listener will be listening for the
   //    * events of local entries. Let's say your cluster has member1 and member2. On member2 you added a local listener and
   //    * from member1, you call <code>map.put(key2, value2)</code>. If the key2 is owned by member2 then the local listener
   //    * will be notified for the add/update event. Also note that entries can migrate to other nodes for load balancing
   //    * and/or membership change.
   //    *
   //    * @param listener entry listener
   //    * @see #localKeySet()
   //    */
   //   void addLocalEntryListener(EntryListener<K, V> listener);
   //
   //   /**
   //    * Adds an entry listener for this map. Listener will get notified for all map add/remove/update/evict events.
   //    *
   //    * @param listener entry listener
   //    * @param includeValue <tt>true</tt> if <tt>EntryEvent</tt> should contain the value.
   //    */
   //   void addEntryListener(EntryListener<K, V> listener, boolean includeValue);
   //
   //   /**
   //    * Removes the specified entry listener Returns silently if there is no such listener added before.
   //    *
   //    * @param listener entry listener
   //    */
   //   void removeEntryListener(EntryListener<K, V> listener);
   //
   //   /**
   //    * Adds the specified entry listener for the specified key. The listener will get notified for all
   //    * add/remove/update/evict events of the specified key only.
   //    *
   //    * @param listener entry listener
   //    * @param key key to listen
   //    * @param includeValue <tt>true</tt> if <tt>EntryEvent</tt> should contain the value.
   //    */
   //   void addEntryListener(EntryListener<K, V> listener, K key, boolean includeValue);
   //
   //   /**
   //    * Removes the specified entry listener for the specified key. Returns silently if there is no such listener added
   //    * before for the key.
   //    *
   //    * @param listener
   //    * @param key
   //    */
   //   void removeEntryListener(EntryListener<K, V> listener, K key);
}
