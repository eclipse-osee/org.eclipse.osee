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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Iterator;

/**
 * Immediately removes each element from the underlying iterator when it is returned from calling next(). One use for a
 * DrainingIterator is when an iterator from a ConcurrentHashMap (i.e. ConcurrentHashMap.values().iterator()) is passed
 * to another method for iteration and the subsequent removal of each element is required. In that case, this class is
 * more than just a convenience because it is not safe to simply call ConcurrentHashMap.clear() after the iterator
 * finishes since the ConcurrentHashMap may well have been added to concurrently and this may or may not be reflected in
 * the iterator. Thus new additions to the ConcurrentHashMap might otherwise get cleared with out ever being accessed
 * through iteration.
 * 
 * @author Ryan D. Brooks
 */
public class DrainingIterator<T> implements Iterator<T>, Iterable<T> {
   private final Iterator<T> iterator;

   public DrainingIterator(Iterator<T> iterator) {
      this.iterator = iterator;
   }

   public DrainingIterator(Iterable<T> iterable) {
      this.iterator = iterable.iterator();
   }

   @Override
   public boolean hasNext() {
      return iterator.hasNext();
   }

   @Override
   public T next() {
      T value = iterator.next();
      iterator.remove();
      return value;
   }

   @Override
   /**
    * It is always unnecessary to call remove from a DrainingIterator since every call to next has already called
    * remove. This method always throws UnsupportedOperationException.
    */
   public void remove() {
      throw new UnsupportedOperationException(
         "It is always unnecessary to call remove from a DrainingIterator since every call to next has already called remove.");
   }

   @Override
   public Iterator<T> iterator() {
      return this;
   }
}