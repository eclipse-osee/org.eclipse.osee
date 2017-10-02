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

import com.google.common.base.Predicate;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public abstract class FilterableCollection<MATCH_DATA, KEY, DATA> {

   private final Multimap<KEY, DATA> map;

   protected FilterableCollection(Multimap<KEY, DATA> map) {
      super();
      this.map = map;
   }

   protected FilterableCollection() {
      this(Multimaps.synchronizedMultimap(LinkedHashMultimap.<KEY, DATA> create()));
   }

   protected abstract ResultSet<DATA> createResultSet(List<DATA> values);

   protected abstract <T extends DATA> ResultSet<T> createResultSet(KEY attributeType, List<T> values);

   public void add(KEY type, DATA data) {
      map.put(type, data);
   }

   public void remove(KEY type, DATA data) {
      map.remove(type, data);
   }

   public Collection<DATA> getAll() {
      return map.values();
   }

   public Collection<DATA> getAllByType(KEY type) {
      return map.get(type);
   }

   protected List<DATA> getListByFilter(Predicate<MATCH_DATA> matcher)  {
      return getListByFilter(getAll(), matcher);
   }

   protected ResultSet<DATA> getResultSetByFilter(Predicate<MATCH_DATA> matcher)  {
      return getResultSetByFilter(getAll(), matcher);
   }

   protected <T extends DATA> ResultSet<T> getSetByFilter(KEY type, Predicate<MATCH_DATA> matcher)  {
      List<T> result = getListByFilter(type, matcher);
      ResultSet<T> resultSet = createResultSet(type, result);
      return resultSet;
   }

   protected <T extends DATA> List<T> getListByFilter(KEY type, Predicate<MATCH_DATA> matcher)  {
      return getListByFilter(getAllByType(type), matcher);
   }

   protected boolean hasItemMatchingFilter(Predicate<MATCH_DATA> matcher) {
      return hasItemMatchingFilter(getAll(), matcher);
   }

   protected boolean hasItemMatchingFilter(KEY type, Predicate<MATCH_DATA> matcher) {
      return hasItemMatchingFilter(getAllByType(type), matcher);
   }

   private ResultSet<DATA> getResultSetByFilter(Collection<DATA> source, Predicate<MATCH_DATA> matcher)  {
      List<DATA> values = getListByFilter(source, matcher);
      return createResultSet(values);
   }

   @SuppressWarnings({"unchecked"})
   private <T extends DATA> List<T> getListByFilter(Collection<DATA> source, Predicate<MATCH_DATA> matcher)  {
      List<T> toReturn;
      if (source != null && !source.isEmpty()) {
         toReturn = new LinkedList<>();
         for (Iterator<DATA> iterator = source.iterator(); iterator.hasNext();) {
            DATA data = iterator.next();
            if (isValid(data)) {
               MATCH_DATA toMatch = asMatcherData(data);
               if (matcher.apply(toMatch)) {
                  toReturn.add((T) data);
               }
            } else {
               iterator.remove();
            }
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   private boolean hasItemMatchingFilter(Collection<DATA> source, Predicate<MATCH_DATA> matcher) {
      boolean result = false;
      if (source != null && !source.isEmpty()) {
         for (Iterator<DATA> iterator = source.iterator(); iterator.hasNext();) {
            DATA data = iterator.next();
            if (isValid(data)) {
               MATCH_DATA toMatch = asMatcherData(data);
               if (matcher.apply(toMatch)) {
                  result = true;
                  break;
               }
            } else {
               iterator.remove();
            }
         }
      }
      return result;
   }

   protected boolean isValid(DATA data) {
      return true;
   }

   protected abstract MATCH_DATA asMatcherData(DATA data);

   @Override
   public String toString() {
      return "FilterableCollection [mapSize=" + map.size() + "]";
   }
}
