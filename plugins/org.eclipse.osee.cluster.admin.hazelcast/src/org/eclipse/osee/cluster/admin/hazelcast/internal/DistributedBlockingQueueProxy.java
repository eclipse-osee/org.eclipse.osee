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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.distributed.DistributedBlockingQueue;

/**
 * @author Roberto E. Escobar
 */
public class DistributedBlockingQueueProxy<E> implements DistributedBlockingQueue<E> {

   private final com.hazelcast.core.IQueue<E> proxyObject;

   public DistributedBlockingQueueProxy(com.hazelcast.core.IQueue<E> proxyObject) {
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
   public E remove() {
      return proxyObject.remove();
   }

   @Override
   public E poll() {
      return proxyObject.poll();
   }

   @Override
   public E element() {
      return proxyObject.element();
   }

   @Override
   public E peek() {
      return proxyObject.peek();
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
   public Iterator<E> iterator() {
      return proxyObject.iterator();
   }

   @Override
   public Object[] toArray() {
      return proxyObject.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return proxyObject.toArray(a);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return proxyObject.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      return proxyObject.containsAll(c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return proxyObject.removeAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return proxyObject.retainAll(c);
   }

   @Override
   public void clear() {
      proxyObject.clear();
   }

   @Override
   public boolean add(E e) {
      return proxyObject.add(e);
   }

   @Override
   public boolean offer(E e) {
      return proxyObject.offer(e);
   }

   @Override
   public void put(E e) throws InterruptedException {
      proxyObject.put(e);
   }

   @Override
   public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
      return proxyObject.offer(e, timeout, unit);
   }

   @Override
   public E take() throws InterruptedException {
      return proxyObject.take();
   }

   @Override
   public E poll(long timeout, TimeUnit unit) {
      return proxyObject.poll();
   }

   @Override
   public int remainingCapacity() {
      return proxyObject.remainingCapacity();
   }

   @Override
   public boolean remove(Object o) {
      return proxyObject.remove(o);
   }

   @Override
   public boolean contains(Object o) {
      return proxyObject.contains(o);
   }

   @Override
   public int drainTo(Collection<? super E> c) {
      return proxyObject.drainTo(c);
   }

   @Override
   public int drainTo(Collection<? super E> c, int maxElements) {
      return proxyObject.drainTo(c, maxElements);
   }
}
