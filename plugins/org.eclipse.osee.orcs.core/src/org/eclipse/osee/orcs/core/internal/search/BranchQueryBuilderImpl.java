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
package org.eclipse.osee.orcs.core.internal.search;

import static org.eclipse.osee.framework.core.enums.BranchArchivedState.UNARCHIVED;
import static org.eclipse.osee.framework.core.enums.BranchState.DELETED;
import static org.eclipse.osee.framework.core.enums.BranchState.DELETE_IN_PROGRESS;
import static org.eclipse.osee.framework.core.enums.BranchState.PURGED;
import static org.eclipse.osee.framework.core.enums.BranchState.PURGE_IN_PROGRESS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchIds;
import org.eclipse.osee.orcs.search.BranchQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class BranchQueryBuilderImpl<T> implements BranchQueryBuilder<T> {

   private final BranchCriteriaFactory criteriaFactory;
   private final QueryData queryData;
   private boolean includeArchived;

   public BranchQueryBuilderImpl(BranchCriteriaFactory criteriaFactory, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
      includeDeleted();
      includeArchived();
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public T includeDeleted() {
      return includeDeleted(true);
   }

   @Override
   public T excludeDeleted() {
      return includeDeleted(false);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeleted(boolean enabled) {
      OptionsUtil.setIncludeDeletedBranches(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return OptionsUtil.areDeletedBranchesIncluded(getOptions());
   }

   @Override
   public T includeArchived() {
      return includeArchived(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeArchived(boolean enabled) {
      includeArchived = enabled;
      return (T) this;
   }

   @Override
   public T excludeArchived() {
      return includeArchived(false);
   }

   @Override
   public boolean areArchivedIncluded() {
      return includeArchived;
   }

   @Override
   public T andIds(Collection<? extends BranchId> branchIds) {
      return addAndCheck(getQueryData(), new CriteriaBranchIds(branchIds));
   }

   @Override
   public T andId(BranchId branchId) {
      return addAndCheck(getQueryData(), new CriteriaBranchIds(branchId));
   }

   @Override
   public T andIsOfType(BranchType... branchType)  {
      Criteria criteria = criteriaFactory.createBranchTypeCriteria(Arrays.asList(branchType));
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andStateIs(BranchState... branchState)  {
      Criteria criteria = criteriaFactory.createBranchStateCriteria(Arrays.asList(branchState));
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNameEquals(String value)  {
      Criteria criteria = criteriaFactory.createBranchNameCriteria(value, false);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNamePattern(String pattern)  {
      Criteria criteria = criteriaFactory.createBranchNameCriteria(pattern, true);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andIsChildOf(BranchId parent)  {
      Criteria criteria = criteriaFactory.createBranchChildOfCriteria(parent);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andIsAncestorOf(BranchId child)  {
      Criteria criteria = criteriaFactory.createBranchAncestorOfCriteria(child);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andIsMergeFor(BranchId source, BranchId destination) {
      Criteria criteria = criteriaFactory.createMergeForCriteria(source, destination);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andAssociatedArtId(ArtifactId artId) {
      Criteria criteria = criteriaFactory.createAssociatedArtIdCriteria(artId);
      return addAndCheck(getQueryData(), criteria);
   }

   @SuppressWarnings("unchecked")
   private T addAndCheck(QueryData queryData, Criteria criteria)  {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return (T) this;
   }

   public QueryData buildAndCopy() {
      return build(true);
   }

   public QueryData build() {
      return build(false);
   }

   private QueryData build(boolean clone) {
      QueryData queryData = clone ? getQueryData().clone() : getQueryData();
      Collection<Criteria> criterias = queryData.getAllCriteria();
      if (criterias.isEmpty()) {
         addAndCheck(queryData, criteriaFactory.createAllBranchesCriteria());
      }
      if (!areArchivedIncluded()) {
         Collection<BranchArchivedState> states = Arrays.asList(UNARCHIVED);
         addAndCheck(queryData, criteriaFactory.createBranchArchivedCriteria(states));
      }
      if (!areDeletedIncluded()) {
         Collection<BranchState> states = new ArrayList<>();
         for (BranchState state : BranchState.values()) {
            if (state != DELETE_IN_PROGRESS && state != DELETED && state != PURGE_IN_PROGRESS && state != PURGED) {
               states.add(state);
            }
         }
         addAndCheck(queryData, criteriaFactory.createBranchStateCriteria(states));
      }
      return queryData;
   }

}
