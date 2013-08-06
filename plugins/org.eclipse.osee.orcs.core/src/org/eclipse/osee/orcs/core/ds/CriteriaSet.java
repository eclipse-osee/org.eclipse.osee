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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import com.google.common.collect.HashMultimap;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaSet implements Cloneable, Iterable<Criteria> {

   private final HashMultimap<Class<? extends Criteria>, Criteria> criterias = HashMultimap.create();
   private final IOseeBranch branch;

   public CriteriaSet(IOseeBranch branch) {
      this.branch = branch;
   }

   public IOseeBranch getBranch() {
      return branch;
   }

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

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      return criterias.containsKey(type);
   }

   @Override
   public CriteriaSet clone() {
      CriteriaSet clone = new CriteriaSet(this.branch);
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
      builder.append("branch=[");
      builder.append(branch);
      builder.append("] criterias=[");
      builder.append(criterias.values());
      builder.append("]");
      return builder.toString();
   }
}
