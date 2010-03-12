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
package org.eclipse.osee.framework.ui.data.model.editor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class TypeManager<T extends DataType> {

   private final static DataTypeComparator DATA_TYPE_COMPARATOR = new DataTypeComparator();
   private final Map<String, T> types;

   public TypeManager() {
      this.types = new HashMap<String, T>();
   }

   public Set<String> getIds() {
      return types.keySet();
   }

   public List<T> getAll() {
      return new ArrayList<T>(types.values());
   }

   public T getById(String uniqueId) {
      return types.get(uniqueId);
   }

   public void add(T type) {
      types.put(type.getUniqueId(), type);
   }

   public void addAll(Collection<T> types) {
      for (T type : types) {
         add(type);
      }
   }

   public int size() {
      return types.size();
   }

   public T getFirst() {
      return types.isEmpty() ? null : types.values().iterator().next();
   }

   public boolean removeById(String uniqueId) {
      return types.remove(uniqueId) != null;
   }

   public boolean remove(T object) {
      String toRemove = null;
      for (String key : types.keySet()) {
         if (object.equals(types.get(key))) {
            toRemove = key;
            break;
         }
      }
      return toRemove != null ? types.remove(toRemove) != null : false;
   }

   public List<T> getAllSorted() {
      List<T> list = getAll();
      Collections.sort(list, DATA_TYPE_COMPARATOR);
      return list;
   }

   private final static class DataTypeComparator implements Comparator<DataType> {

      @Override
      public int compare(DataType o1, DataType o2) {
         String text1 = ODMConstants.getDataTypeText(o1);
         String text2 = ODMConstants.getDataTypeText(o2);
         return text1.compareTo(text2);
      }
   }
}
