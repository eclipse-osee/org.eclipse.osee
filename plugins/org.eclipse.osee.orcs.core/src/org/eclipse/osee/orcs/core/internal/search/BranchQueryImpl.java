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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchQueryImpl implements BranchQuery {

   private final BranchCallableQueryFactory queryFactory;
   private final BranchCriteriaFactory criteriaFactory;
   private final OrcsSession session;
   private final QueryData queryData;
   private boolean includeArchived;

   public BranchQueryImpl(BranchCallableQueryFactory queryFactory, BranchCriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      this.queryFactory = queryFactory;
      this.criteriaFactory = criteriaFactory;
      this.session = session;
      this.queryData = queryData;
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public BranchQuery includeDeleted() {
      includeDeleted(true);
      return this;
   }

   @Override
   public BranchQuery excludeDeleted() {
      includeDeleted(false);
      return this;
   }

   @Override
   public BranchQuery includeDeleted(boolean enabled) {
      OptionsUtil.setIncludeDeleted(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return OptionsUtil.areDeletedIncluded(getOptions());
   }

   @Override
   public BranchQuery includeArchived() {
      includeArchived(true);
      return this;
   }

   @Override
   public BranchQuery includeArchived(boolean enabled) {
      includeArchived = enabled;
      return this;
   }

   @Override
   public BranchQuery excludeArchived() {
      includeArchived(false);
      return this;
   }

   @Override
   public boolean areArchivedIncluded() {
      return includeArchived;
   }

   @Override
   public BranchQuery andLocalId(int... ids) throws OseeCoreException {
      Set<Integer> allIds = new HashSet<Integer>();
      for (Integer id : ids) {
         allIds.add(id);
      }
      return andLocalIds(allIds);
   }

   @Override
   public BranchQuery andLocalIds(Collection<Integer> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchIdsCriteria(ids);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andUuids(String... ids) throws OseeCoreException {
      return andUuids(Arrays.asList(ids));
   }

   @Override
   public BranchQuery andUuids(Collection<String> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchUuidsCriteria(ids);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andIds(IOseeBranch... ids) throws OseeCoreException {
      return andIds(Arrays.asList(ids));
   }

   @Override
   public BranchQuery andIds(Collection<? extends IOseeBranch> ids) throws OseeCoreException {
      Set<String> allIds = new HashSet<String>();
      for (IOseeBranch token : ids) {
         allIds.add(token.getGuid());
      }
      Criteria criteria = criteriaFactory.createBranchUuidsCriteria(allIds);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andIsOfType(BranchType... branchType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchTypeCriteria(Arrays.asList(branchType));
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andStateIs(BranchState... branchState) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchStateCriteria(Arrays.asList(branchState));
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andNameEquals(String value) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchNameCriteria(value, false);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andNamePattern(String pattern) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchNameCriteria(pattern, true);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andIsChildOf(IOseeBranch parent) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchChildOfCriteria(parent);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public BranchQuery andIsAncestorOf(IOseeBranch child) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createBranchAncestorOfCriteria(child);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public ResultSet<BranchReadable> getResults() throws OseeCoreException {
      ResultSet<BranchReadable> result = null;
      try {
         result = createSearch().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public ResultSet<IOseeBranch> getResultsAsId() throws OseeCoreException {
      ResultSet<IOseeBranch> result = null;
      try {
         result = createSearchResultsAsIds().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public int getCount() throws OseeCoreException {
      Integer result = -1;
      try {
         result = createCount().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public CancellableCallable<Integer> createCount() throws OseeCoreException {
      return queryFactory.createBranchCount(session, checkAndCloneQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<BranchReadable>> createSearch() throws OseeCoreException {
      return queryFactory.createBranchSearch(session, checkAndCloneQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<IOseeBranch>> createSearchResultsAsIds() throws OseeCoreException {
      return queryFactory.createBranchAsIdSearch(session, checkAndCloneQueryData());
   }

   private QueryData checkAndCloneQueryData() throws OseeCoreException {
      QueryData queryData = getQueryData().clone();
      CriteriaSet criteriaSet = queryData.getCriteriaSet();
      if (criteriaSet.getCriterias().isEmpty()) {
         addAndCheck(queryData, criteriaFactory.createAllBranchesCriteria());
      }
      if (!areArchivedIncluded()) {
         Collection<BranchArchivedState> states = Arrays.asList(UNARCHIVED);
         addAndCheck(queryData, criteriaFactory.createBranchArchivedCriteria(states));
      }
      if (!areDeletedIncluded()) {
         Collection<BranchState> states = new ArrayList<BranchState>();
         for (BranchState state : BranchState.values()) {
            if (state != DELETE_IN_PROGRESS && state != DELETED && state != PURGE_IN_PROGRESS && state != PURGED) {
               states.add(state);
            }
         }
         addAndCheck(queryData, criteriaFactory.createBranchStateCriteria(states));
      }
      return queryData;
   }

   private BranchQuery addAndCheck(QueryData queryData, Criteria criteria) throws OseeCoreException {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return this;
   }

}
