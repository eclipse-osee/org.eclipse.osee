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

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import com.google.common.collect.Iterables;

/**
 * @author Roberto E. Escobar
 */
public final class ResultSets {

   @SuppressWarnings({"rawtypes", "unchecked"})
   private static final ResultSet EMPTY_RESULT_SET = new ResultSetList(Collections.emptyList());

   private ResultSets() {
      // Utility
   }

   public static <T> ResultSet<T> singleton(T item) {
      ResultSet<T> toReturn;
      if (item == null) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetList<T>(Collections.singletonList(item));
      }
      return toReturn;
   }

   public static <T> ResultSet<T> newResultSet(List<T> list) {
      ResultSet<T> toReturn;
      if (list.isEmpty()) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetList<T>(list);
      }
      return toReturn;
   }

   public static <T> ResultSet<T> newResultSet(Iterable<T> iterable) {
      ResultSet<T> toReturn;
      if (Iterables.isEmpty(iterable)) {
         toReturn = emptyResultSet();
      } else {
         toReturn = new ResultSetIterable<T>(iterable);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   public static <T> ResultSet<T> emptyResultSet() {
      return EMPTY_RESULT_SET;
   }

}
