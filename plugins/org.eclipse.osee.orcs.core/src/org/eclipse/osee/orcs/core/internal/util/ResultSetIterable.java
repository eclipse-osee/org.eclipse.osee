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
package org.eclipse.osee.orcs.core.internal.util;

import java.util.Iterator;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.ItemDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleItemsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import com.google.common.collect.Iterables;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetIterable<T> implements ResultSet<T> {

   private final Iterable<T> data;

   public ResultSetIterable(Iterable<T> iterable) {
      super();
      this.data = iterable;
   }

   @Override
   public T getOneOrNull() {
      T result = null;
      int size = size();
      if (size > 0) {
         result = iterator().next();
      }
      return result;
   }

   @Override
   public T getAtMostOneOrNull() throws OseeCoreException {
      T result = null;
      int size = size();
      if (size > 1) {
         throw createManyExistException(size);
      } else if (size == 1) {
         result = iterator().next();
      }
      return result;
   }

   @Override
   public T getExactlyOne() throws OseeCoreException {
      T result = getAtMostOneOrNull();
      if (result == null) {
         throw createDoesNotExistException();
      }
      return result;
   }

   private Iterable<T> getData() {
      return data;
   }

   @Override
   public Iterator<T> iterator() {
      return getData().iterator();
   }

   @Override
   public int size() {
      return Iterables.size(getData());
   }

   @Override
   public boolean isEmpty() {
      return Iterables.isEmpty(getData());
   }

   protected OseeCoreException createManyExistException(int count) {
      return new MultipleItemsExist("Multiple items found - total [%s]", count);
   }

   protected OseeCoreException createDoesNotExistException() {
      return new ItemDoesNotExist("No item found");
   }

}
