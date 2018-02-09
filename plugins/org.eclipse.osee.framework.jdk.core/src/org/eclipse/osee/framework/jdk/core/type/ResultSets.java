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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author Roberto E. Escobar
 */
public final class ResultSets {

   @SuppressWarnings({"rawtypes", "unchecked"})
   private static final ResultSet EMPTY_RESULT_SET = new ResultSetList(Collections.emptyList());

   private ResultSets() {
      // Utility
   }

   @SuppressWarnings("unchecked")
   public static <T> ResultSet<T> newResultSet(T... item) {
      ResultSet<T> toReturn;
      if (item == null || item.length <= 0) {
         toReturn = ResultSets.emptyResultSet();
      } else {
         toReturn = ResultSets.newResultSet(Arrays.asList(item));
      }
      return toReturn;
   }

   public static <T> ResultSet<T> singleton(T item) {
      ResultSet<T> toReturn;
      if (item == null) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetList<>(Collections.singletonList(item));
      }
      return toReturn;
   }

   public static <T> ResultSet<T> newResultSet(List<T> list) {
      ResultSet<T> toReturn;
      if (list == null || list.isEmpty()) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetList<>(list);
      }
      return toReturn;
   }

   public static <T> ResultSet<T> newResultSet(Iterable<T> iterable) {
      ResultSet<T> toReturn;
      if (iterable == null || !iterable.iterator().hasNext()) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetIterable<>(iterable);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   public static <T> ResultSet<T> emptyResultSet() {
      return EMPTY_RESULT_SET;
   }

   public static <T, R> ResultSet<R> transform(ResultSet<T> inputSet, Function<T, R> factory) {
      if (inputSet == null || inputSet.isEmpty()) {
         return emptyResultSet();
      }
      List<R> data = new ArrayList<>(inputSet.size());
      inputSet.forEach(in -> data.add(factory.apply(in)));
      return new ResultSetList<>(data);
   }
}