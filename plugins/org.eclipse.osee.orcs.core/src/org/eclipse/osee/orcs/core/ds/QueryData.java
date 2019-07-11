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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;

/**
 * @author Roberto E. Escobar
 */
public final class QueryData implements HasOptions, HasBranch {
   private final List<List<Criteria>> criterias;
   private final SelectData selectData;
   private final Options options;
   private final BranchId branch;

   public QueryData(BranchId branch) {
      this.criterias = new ArrayList<>();
      this.selectData = new SelectData();
      this.options = OptionsUtil.createOptions();
      this.branch = branch;
      criterias.add(new ArrayList<>());
   }

   public QueryData() {
      this(BranchId.SENTINEL);
   }

   @Override
   public Options getOptions() {
      return options;
   }

   public List<Criteria> getAllCriteria() {
      List<Criteria> allCriterias = new ArrayList<>();
      for (List<Criteria> list : criterias) {
         allCriterias.addAll(list);
      }
      return allCriterias;
   }

   public boolean hasNoCriteria() {
      return criterias.get(0).isEmpty();
   }

   public List<List<Criteria>> getCriteriaSets() {
      return Collections.unmodifiableList(criterias);
   }

   public List<Criteria> getLastCriteriaSet() {
      return criterias.get(criterias.size() - 1);
   }

   public List<Criteria> newCriteriaSet() {
      List<Criteria> criteriaSet = new ArrayList<>();
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
      List<Criteria> criteriaSet = getLastCriteriaSet();
      for (Criteria criteria : criterias) {
         criteriaSet.add(criteria);
      }
   }

   public void addCriteria(Criteria criteria) {
      getLastCriteriaSet().add(criteria);
   }

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      for (List<Criteria> criteriaSet : criterias) {
         for (Criteria criteria : criteriaSet) {
            if (type.isInstance(criteria)) {
               return true;
            }
         }
      }
      return false;
   }

   public <T extends Criteria> List<T> getCriteriaByType(Class<T> type) {
      List<T> matchingCriteria = new ArrayList<>(2);
      for (List<Criteria> criteriaSet : criterias) {
         for (Criteria criteria : criteriaSet) {
            if (type.isInstance(criteria)) {
               matchingCriteria.add(type.cast(criteria));
            }
         }
      }
      return matchingCriteria;
   }

   public void reset() {
      OptionsUtil.reset(options);

      List<Criteria> criteriaSet = criterias.get(0);
      criteriaSet.clear();
      criterias.clear();
      criterias.add(criteriaSet);

      selectData.reset();
   }

   @Override
   public String toString() {
      return "QueryData [criterias=" + criterias + ", selects=" + selectData + ", options=" + options + "]";
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }
}