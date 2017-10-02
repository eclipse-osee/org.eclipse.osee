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
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public final class AccessData {

   private final CompositeKeyHashMap<Object, Object, AccessDetail<?>> accessMap =
      new CompositeKeyHashMap<Object, Object, AccessDetail<?>>();

   public AccessData() {
      super();
   }

   public boolean isEmpty() {
      return accessMap.isEmpty();
   }

   public void addAll(Object key, Collection<AccessDetail<?>> datas)  {
      Conditions.checkNotNull(key, "access key");
      Conditions.checkNotNull(datas, "accessDetails");
      for (AccessDetail<?> data : datas) {
         add(key, data);
      }
   }

   public Set<Object> keySet() {
      Set<Object> toReturn = new HashSet<>();
      for (Pair<Object, Object> key : accessMap.keySet()) {
         toReturn.add(key.getFirst());
      }
      return toReturn;
   }

   public void add(Object key, AccessDetail<?> data)  {
      Conditions.checkNotNull(key, "access key");
      Conditions.checkNotNull(data, "access data");

      AccessDetail<?> original = accessMap.get(key, data.getAccessObject());

      AccessDetail<?> resolvedAccess = AccessDetail.resolveAccess(original, data);
      accessMap.put(key, data.getAccessObject(), resolvedAccess);
   }

   public Collection<AccessDetail<?>> getAccess(Object key)  {
      Conditions.checkNotNull(key, "access key");
      return accessMap.getValues(key);
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("accessData [");
      if (!accessMap.isEmpty()) {
         builder.append("\n");
         for (Object key : keySet()) {
            for (AccessDetail<?> detail : accessMap.getValues(key)) {
               builder.append("\t");
               builder.append(key);
               builder.append(" - ");
               builder.append(detail);
               builder.append(",\n");
            }
         }
      }
      builder.append("]");
      return builder.toString();
   }
}