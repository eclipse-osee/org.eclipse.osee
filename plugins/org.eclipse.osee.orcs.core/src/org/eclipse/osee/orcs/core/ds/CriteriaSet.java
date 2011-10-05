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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaSet implements Cloneable {

   private final Set<Criteria> criterias = new HashSet<Criteria>();
   private Criteria baseCriteria;

   public CriteriaSet() {

   }

   public Criteria getBaseCriteria() {
      return baseCriteria;
   }

   public void setBaseCriteria(Criteria baseCriteria) {
      this.baseCriteria = baseCriteria;
   }

   public void add(Criteria criteria) {
      criterias.add(criteria);
   }

   public boolean remove(Criteria criteria) {
      return criterias.remove(criteria);
   }

   public Set<Criteria> getCriterias() {
      return criterias;
   }

   public void reset() {
      criterias.clear();
   }

   @Override
   public CriteriaSet clone() {
      CriteriaSet clone = new CriteriaSet();
      clone.baseCriteria = this.baseCriteria;
      clone.criterias.addAll(this.criterias);
      return clone;
   }

}
