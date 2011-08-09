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
import java.util.List;
import java.util.ListIterator;
import org.eclipse.osee.distributed.DistributedList;

/**
 * @author Roberto E. Escobar
 */
public class DistributedListProxy<E> implements DistributedList<E> {

   private final com.hazelcast.core.IList<E> proxyObject;

   public DistributedListProxy(com.hazelcast.core.IList<E> proxyObject) {
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
   public int size() {
      return proxyObject.size();
   }

   @Override
   public boolean isEmpty() {
      return proxyObject.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return proxyObject.contains(o);
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
   public boolean add(E e) {
      return proxyObject.add(e);
   }

   @Override
   public boolean remove(Object o) {
      return proxyObject.remove(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return proxyObject.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      return proxyObject.addAll(c);
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> c) {
      return proxyObject.addAll(index, c);
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
   public E get(int index) {
      return proxyObject.get(index);
   }

   @Override
   public E set(int index, E element) {
      return proxyObject.set(index, element);
   }

   @Override
   public void add(int index, E element) {
      proxyObject.add(index, element);
   }

   @Override
   public E remove(int index) {
      return proxyObject.remove(index);
   }

   @Override
   public int indexOf(Object o) {
      return proxyObject.indexOf(o);
   }

   @Override
   public int lastIndexOf(Object o) {
      return proxyObject.lastIndexOf(o);
   }

   @Override
   public ListIterator<E> listIterator() {
      return proxyObject.listIterator();
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      return proxyObject.listIterator(index);
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      return proxyObject.subList(fromIndex, toIndex);
   }
}
