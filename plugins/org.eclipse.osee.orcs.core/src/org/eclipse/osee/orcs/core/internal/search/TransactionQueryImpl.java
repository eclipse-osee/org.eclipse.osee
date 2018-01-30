/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Roberto E. Escobar
 */
public class TransactionQueryImpl extends TxQueryBuilderImpl<TransactionQuery> implements TransactionQuery {

   private final TransactionCallableQueryFactory queryFactory;
   private final OrcsSession session;

   public TransactionQueryImpl(TransactionCallableQueryFactory queryFactory, TransactionCriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryFactory = queryFactory;
      this.session = session;
   }

   @Override
   public ResultSet<TransactionReadable> getResults() {
      try {
         return createSearch().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResultSet<TransactionToken> getTokens() {
      List<TransactionToken> tokens = new ArrayList<>();
      for (TransactionReadable tx : getResults()) {
         tokens.add(tx);
      }
      return ResultSets.newResultSet(tokens);
   }

   @Override
   public ResultSet<Long> getResultsAsIds() {
      try {
         return createSearchResultsAsIds().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount() {
      try {
         return createCount().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }

   @Override
   public CancellableCallable<Integer> createCount() {
      return queryFactory.createTransactionCount(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<TransactionReadable>> createSearch() {
      return queryFactory.createTransactionSearch(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<Long>> createSearchResultsAsIds() {
      return queryFactory.createTransactionAsIdSearch(session, buildAndCopy());
   }

}
