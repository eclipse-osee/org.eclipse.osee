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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Roberto E. Escobar
 */
public class TransactionQueryImpl implements TransactionQuery {

   private final TransactionCallableQueryFactory queryFactory;
   private final TransactionCriteriaFactory criteriaFactory;
   private final OrcsSession session;
   private final QueryData queryData;

   public TransactionQueryImpl(TransactionCallableQueryFactory queryFactory, TransactionCriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
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
   public TransactionQuery andTxId(int... ids) throws OseeCoreException {
      Set<Integer> values = new LinkedHashSet<Integer>();
      for (int value : ids) {
         values.add(value);
      }
      return andTxIds(values);
   }

   @Override
   public TransactionQuery andTxIds(Collection<Integer> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByIdsCriteria(ids);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andTxId(Operator op, int id) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByIdWithOperator(op, id);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andTxId(Operator op1, int id1, Operator op2, int id2) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByIdWithTwoOperators(op1, id1, op2, id2);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andCommentEquals(String value) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newCommentCriteria(value, false);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andCommentPattern(String pattern) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newCommentCriteria(pattern, true);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andIs(TransactionDetailsType... types) throws OseeCoreException {
      return andIs(Arrays.asList(types));
   }

   @Override
   public TransactionQuery andIs(Collection<TransactionDetailsType> types) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newTxTypeCriteria(types);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andBranch(IOseeBranch... ids) throws OseeCoreException {
      return andBranch(Arrays.asList(ids));
   }

   @Override
   public TransactionQuery andBranch(Collection<? extends IOseeBranch> ids) throws OseeCoreException {
      Set<Long> values = new LinkedHashSet<Long>();
      for (IOseeBranch value : ids) {
         values.add(value.getUuid());
      }
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(values);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andBranchIds(long... ids) throws OseeCoreException {
      Set<Long> values = new LinkedHashSet<Long>();
      for (long value : ids) {
         values.add(value);
      }
      return andBranchIds(values);
   }

   @Override
   public TransactionQuery andBranchIds(Collection<Long> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(ids);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public ResultSet<TransactionReadable> getResults() throws OseeCoreException {
      ResultSet<TransactionReadable> result = null;
      try {
         result = createSearch().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public ResultSet<Integer> getResultsAsIds() throws OseeCoreException {
      ResultSet<Integer> result = null;
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
      return queryFactory.createTransactionCount(session, checkAndCloneQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<TransactionReadable>> createSearch() throws OseeCoreException {
      return queryFactory.createTransactionSearch(session, checkAndCloneQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<Integer>> createSearchResultsAsIds() throws OseeCoreException {
      return queryFactory.createTransactionAsIdSearch(session, checkAndCloneQueryData());
   }

   @Override
   public TransactionQuery andDate(Operator op, Timestamp date) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByDateWithOperator(op, date);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andDate(Timestamp from, Timestamp to) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByDateRange(from, to);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andAuthorLocalIds(ArtifactId... id) throws OseeCoreException {
      return andAuthorLocalIds(Arrays.asList(id));
   }

   @Override
   public TransactionQuery andAuthorLocalIds(Collection<ArtifactId> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByArtifactId(ids);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andAuthorIds(int... id) throws OseeCoreException {
      ArrayList<Integer> theList = new ArrayList<Integer>();
      for (int i = 0; i < id.length; i++) {
         theList.add(new Integer(id[i]));
      }
      return andAuthorIds(theList);
   }

   @Override
   public TransactionQuery andAuthorIds(Collection<Integer> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByAuthorId(ids);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andCommitIds(Integer... id) throws OseeCoreException {
      return andCommitIds(Arrays.asList(id));
   }

   @Override
   public TransactionQuery andNullCommitId() throws OseeCoreException {
      Collection<Integer> aNull = new ArrayList<Integer>();
      aNull.add(null);
      return andCommitIds(aNull);
   }

   @Override
   public TransactionQuery andCommitIds(Collection<Integer> ids) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newByCommitId(ids);
      addAndCheck(queryData, criteria);
      return this;
   }

   @Override
   public TransactionQuery andIsHead(IOseeBranch branch) throws OseeCoreException {
      return andIsHead(branch.getUuid());
   }

   @Override
   public TransactionQuery andIsHead(long branchUuid) throws OseeCoreException {
      Criteria criteria = criteriaFactory.newGetHead(branchUuid);
      addAndCheck(queryData, criteria);
      return this;
   }

   private QueryData checkAndCloneQueryData() throws OseeCoreException {
      QueryData queryData = getQueryData().clone();
      CriteriaSet criteriaSet = queryData.getCriteriaSet();
      if (criteriaSet.getCriterias().isEmpty()) {
         addAndCheck(queryData, criteriaFactory.createAllTransactionsCriteria());
      }
      return queryData;
   }

   private TransactionQuery addAndCheck(QueryData queryData, Criteria criteria) throws OseeCoreException {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return this;
   }

}
