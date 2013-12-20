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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetTransform<I, F extends Identity<I>, T extends Identity<I>> implements ResultSet<T> {

   public static interface Function<I, F extends Identity<I>, T extends Identity<I>> {
      T apply(F source);
   }

   private final ResultSet<F> result;
   private final Function<I, F, T> function;
   private final Map<I, T> objectMap = new HashMap<I, T>();

   protected ResultSetTransform(ResultSet<F> result, Function<I, F, T> factory) {
      this.function = factory;
      this.result = result;
   }

   @Override
   public T getOneOrNull() throws OseeCoreException {
      return toObject(result.getOneOrNull());
   }

   @Override
   public T getExactlyOne() throws OseeCoreException {
      return toObject(result.getExactlyOne());
   }

   @Override
   public T getAtMostOneOrNull() throws OseeCoreException {
      return toObject(result.getAtMostOneOrNull());
   }

   @Override
   public int size() {
      return result.size();
   }

   @Override
   public boolean isEmpty() {
      return result.isEmpty();
   }

   @Override
   public Iterator<T> iterator() {
      final Iterator<F> iterator = result.iterator();
      return new Iterator<T>() {

         private T current = null;

         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public T next() {
            current = toObject(iterator.next());
            return current;
         }

         @Override
         public void remove() {
            iterator.remove();
            if (current != null) {
               synchronized (objectMap) {
                  objectMap.remove(current.getGuid());
               }
            }
         }
      };
   }

   private T toObject(F source) {
      I key = source.getGuid();
      T object;
      synchronized (objectMap) {
         object = objectMap.get(key);
         if (object == null) {
            object = function.apply(source);
            objectMap.put(key, object);
         }
      }
      return object;
   }
}
