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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.TxQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TxQueryBuilderImpl<T> implements TxQueryBuilder<T> {

   private final TransactionCriteriaFactory criteriaFactory;
   private final QueryData queryData;

   public TxQueryBuilderImpl(TransactionCriteriaFactory criteriaFactory, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public T andTxIds(Collection<TransactionId> ids) {
      return addAndCheck(queryData, new CriteriaTxIds(ids));
   }

   @Override
   public T andTxId(Operator op, int id) {
      Criteria criteria = criteriaFactory.newByIdWithOperator(op, id);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andTxId(Operator op1, int id1, Operator op2, int id2) {
      Criteria criteria = criteriaFactory.newByIdWithTwoOperators(op1, id1, op2, id2);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andCommentEquals(String value) {
      Criteria criteria = criteriaFactory.newCommentCriteria(value, false);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andCommentPattern(String pattern) {
      Criteria criteria = criteriaFactory.newCommentCriteria(pattern, true);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andIs(TransactionDetailsType... types) {
      return andIs(Arrays.asList(types));
   }

   @Override
   public T andIs(Collection<TransactionDetailsType> types) {
      Criteria criteria = criteriaFactory.newTxTypeCriteria(types);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andBranch(BranchId... ids) {
      return andBranch(Arrays.asList(ids));
   }

   @Override
   public T andBranch(Collection<? extends BranchId> ids) {
      Set<Long> values = new LinkedHashSet<>();
      for (BranchId value : ids) {
         values.add(value.getUuid());
      }
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(values);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andBranchIds(long... ids) {
      Set<Long> values = new LinkedHashSet<>();
      for (long value : ids) {
         values.add(value);
      }
      return andBranchIds(values);
   }

   @Override
   public T andBranchIds(Collection<Long> ids) {
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(ids);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andDate(Operator op, Timestamp date) {
      Criteria criteria = criteriaFactory.newByDateWithOperator(op, date);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andDate(Timestamp from, Timestamp to) {
      Criteria criteria = criteriaFactory.newByDateRange(from, to);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andAuthorLocalIds(ArtifactId... id) {
      return andAuthorLocalIds(Arrays.asList(id));
   }

   @Override
   public T andAuthorLocalIds(Collection<ArtifactId> ids) {
      return addAndCheck(queryData, new CriteriaTxArtifactIds(ids));
   }

   @Override
   public T andAuthorIds(int... id) {
      ArrayList<Integer> theList = new ArrayList<>();
      for (int i = 0; i < id.length; i++) {
         theList.add(new Integer(id[i]));
      }
      return andAuthorIds(theList);
   }

   @Override
   public T andAuthorIds(Collection<Integer> ids) {
      Criteria criteria = criteriaFactory.newByAuthorId(ids);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andCommitIds(Integer... id) {
      return andCommitIds(Arrays.asList(id));
   }

   @Override
   public T andNullCommitId() {
      Collection<Integer> aNull = new ArrayList<>();
      aNull.add(null);
      return andCommitIds(aNull);
   }

   @Override
   public T andCommitIds(Collection<Integer> ids) {
      Criteria criteria = criteriaFactory.newByCommitId(ids);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andIsHead(BranchId branch) {
      Criteria criteria = criteriaFactory.newGetHead(branch);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andIsPriorTx(TransactionId txId) {
      Criteria criteria = criteriaFactory.newGetPriorTx(txId);
      return addAndCheck(queryData, criteria);
   }

   @SuppressWarnings("unchecked")
   private T addAndCheck(QueryData queryData, Criteria criteria) {
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
      if (queryData.getAllCriteria().isEmpty()) {
         addAndCheck(queryData, criteriaFactory.createAllTransactionsCriteria());
      }
      return queryData;
   }

   @Override
   public T andTxId(TransactionId id) {
      return addAndCheck(queryData, new CriteriaTxIds(id));
   }
}