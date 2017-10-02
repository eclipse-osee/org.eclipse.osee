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

import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.deletionFlagEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.isDirty;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractTypeCollection<TYPE, MATCH_DATA extends OrcsWriteable, KEY, DATA> extends FilterableCollection<MATCH_DATA, KEY, DATA> {

   protected AbstractTypeCollection(Multimap<KEY, DATA> map) {
      super(map);
   }

   protected AbstractTypeCollection() {
      super();
   }

   protected abstract TYPE getType(DATA data);

   public Collection<TYPE> getExistingTypes(DeletionFlag includeDeleted) {
      Set<TYPE> toReturn = new LinkedHashSet<>();
      for (DATA data : getList(includeDeleted)) {
         if (isValid(data)) {
            toReturn.add(getType(data));
         }
      }
      return toReturn;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<DATA> getDirties() {
      Predicate matchDirties = isDirty();
      return getListByFilter(matchDirties);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public boolean hasDirty() {
      Predicate matchDirties = isDirty();
      return hasItemMatchingFilter(matchDirties);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<DATA> getList(DeletionFlag includeDeleted) {
      Predicate deletedStateMatch = deletionFlagEquals(includeDeleted);
      return getListByFilter(deletedStateMatch);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public ResultSet<DATA> getResultSet(DeletionFlag includeDeleted) {
      Predicate value = deletionFlagEquals(includeDeleted);
      return getResultSetByFilter(value);
   }

}
