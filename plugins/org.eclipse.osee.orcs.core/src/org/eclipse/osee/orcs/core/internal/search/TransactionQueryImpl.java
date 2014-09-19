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

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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
      return queryFactory.createTransactionCount(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<TransactionReadable>> createSearch() throws OseeCoreException {
      return queryFactory.createTransactionSearch(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<Integer>> createSearchResultsAsIds() throws OseeCoreException {
      return queryFactory.createTransactionAsIdSearch(session, buildAndCopy());
   }

}
