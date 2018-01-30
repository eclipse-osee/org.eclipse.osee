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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetList<T> implements ResultSet<T> {
   private final List<T> data;

   public ResultSetList(List<T> data) {
      this.data = data;
   }

   @Override
   public T getOneOrNull() {
      return data.isEmpty() ? null : data.iterator().next();
   }

   @Override
   public T getAtMostOneOrNull() {
      if (data.size() > 1) {
         throw createManyExistException(data.size());
      }
      return data.isEmpty() ? null : iterator().next();
   }

   @Override
   public T getExactlyOne() {
      T result = getAtMostOneOrNull();
      if (result == null) {
         throw createDoesNotExistException();
      }
      return result;
   }

   @Override
   public List<T> getList() {
      return data;
   }

   @Override
   public Iterator<T> iterator() {
      return data.iterator();
   }

   protected OseeCoreException createManyExistException(int count) {
      return new MultipleItemsExist("Multiple items found - total [%s]", count);
   }

   protected OseeCoreException createDoesNotExistException() {
      return new ItemDoesNotExist("No item found");
   }

   @Override
   public int size() {
      return data.size();
   }

   @Override
   public boolean isEmpty() {
      return data.isEmpty();
   }

   @Override
   public ResultSet<T> sort(Comparator<T> comparator) {
      Collections.sort(data, comparator);
      return this;
   }
}