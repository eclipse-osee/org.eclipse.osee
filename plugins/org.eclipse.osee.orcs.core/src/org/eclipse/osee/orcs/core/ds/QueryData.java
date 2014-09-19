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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class QueryData implements HasOptions, Cloneable {

   private final List<CriteriaSet> criterias;
   private final SelectData selectData;
   private final Options options;

   public QueryData(Options options, List<CriteriaSet> criterias, SelectData selectData) {
      this.criterias = criterias;
      this.selectData = selectData;
      this.options = options;
   }

   public QueryData(CriteriaSet criteriaSet, Options options) {
      this(options, new ArrayList<CriteriaSet>(), new SelectData());
      criterias.add(criteriaSet);
   }

   @Override
   public Options getOptions() {
      return options;
   }

   public List<Criteria> getAllCriteria() {
      List<Criteria> allCriterias = new ArrayList<Criteria>();
      for (CriteriaSet set : criterias) {
         allCriterias.addAll(set.getCriterias());
      }
      return allCriterias;
   }

   public List<CriteriaSet> getCriteriaSets() {
      return Collections.unmodifiableList(criterias);
   }

   public CriteriaSet getLastCriteriaSet() {
      return !criterias.isEmpty() ? criterias.get(criterias.size() - 1) : null;
   }

   public CriteriaSet newCriteriaSet() {
      CriteriaSet criteriaSet = new CriteriaSet();
      criterias.add(criteriaSet);
      selectData.newSelectSet();
      return criteriaSet;
   }

   public SelectSet getSelectSet() {
      SelectSet data = selectData.getLast();
      if (data == null) {
         data = selectData.newSelectSet();
      }
      return data;
   }

   public List<SelectSet> getSelectSets() {
      return selectData.getAll();
   }

   public void addCriteria(Criteria... criterias) {
      CriteriaSet criteriaSet = getLastCriteriaSet();
      for (Criteria criteria : criterias) {
         criteriaSet.add(criteria);
      }
   }

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      boolean result = false;
      for (CriteriaSet criteriaSet : criterias) {
         if (criteriaSet.hasCriteriaType(type)) {
            result = true;
            break;
         }
      }
      return result;
   }

   public void reset() {
      options.reset();

      CriteriaSet criteriaSet = null;
      if (!criterias.isEmpty()) {
         criteriaSet = criterias.get(0);
         criteriaSet.reset();
      }
      criterias.clear();
      if (criteriaSet != null) {
         criterias.add(criteriaSet);
      }
   }

   @Override
   public QueryData clone() {
      List<CriteriaSet> newCriterias = new ArrayList<CriteriaSet>(criterias.size());
      for (CriteriaSet criteriaSet : criterias) {
         newCriterias.add(criteriaSet.clone());
      }
      SelectData newSelectData = selectData.clone();
      return new QueryData(options.clone(), newCriterias, newSelectData);
   }

   @Override
   public String toString() {
      return "QueryData [criterias=" + criterias + ", selects=" + selectData + ", options=" + options + "]";
   }

}
