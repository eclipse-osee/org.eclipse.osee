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

public class QueryData implements HasOptions, Cloneable {
   private final CriteriaSet criteriaSet;
   private final Options options;

   public QueryData(CriteriaSet criteriaSet, Options options) {
      this.criteriaSet = criteriaSet;
      this.options = options;
   }

   public CriteriaSet getCriteriaSet() {
      return criteriaSet;
   }

   @Override
   public Options getOptions() {
      return options;
   }

   public void addCriteria(Criteria... criterias) {
      for (Criteria criteria : criterias) {
         criteriaSet.add(criteria);
      }
   }

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      return criteriaSet.hasCriteriaType(type);
   }

   public void reset() {
      options.reset();
      criteriaSet.reset();
   }

   @Override
   public QueryData clone() {
      return new QueryData(criteriaSet.clone(), options.clone());
   }

   @Override
   public String toString() {
      return "QueryData [criteriaSet=" + criteriaSet + ", options=" + options + "]";
   }
}
