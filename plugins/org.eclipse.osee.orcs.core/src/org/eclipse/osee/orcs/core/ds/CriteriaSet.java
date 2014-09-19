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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaSet implements Cloneable, Iterable<Criteria> {

   private final SetMultimap<Class<? extends Criteria>, Criteria> criterias = LinkedHashMultimap.create();

   public void add(Criteria criteria) {
      criterias.put(criteria.getClass(), criteria);
   }

   public boolean remove(Criteria criteria) {
      return criterias.remove(criteria.getClass(), criteria);
   }

   public Collection<Criteria> getCriterias() {
      return criterias.values();
   }

   public void reset() {
      criterias.clear();
   }

   @SuppressWarnings("unchecked")
   public <T extends Criteria> Set<T> getCriteriaByType(Class<T> type) {
      return (Set<T>) criterias.get(type);
   }

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      Set<Criteria> set = criterias.get(type);
      return set != null && !set.isEmpty();
   }

   @Override
   public CriteriaSet clone() {
      CriteriaSet clone = new CriteriaSet();
      clone.criterias.putAll(this.criterias);
      return clone;
   }

   @Override
   public Iterator<Criteria> iterator() {
      return getCriterias().iterator();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("criterias=[");
      builder.append(criterias.values());
      builder.append("]");
      return builder.toString();
   }
}
