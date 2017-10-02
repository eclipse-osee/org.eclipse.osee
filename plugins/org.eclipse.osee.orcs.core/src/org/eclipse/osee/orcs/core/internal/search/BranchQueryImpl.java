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

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchQueryImpl extends BranchQueryBuilderImpl<BranchQuery> implements BranchQuery {

   private final BranchCallableQueryFactory queryFactory;
   private final OrcsSession session;

   public BranchQueryImpl(BranchCallableQueryFactory queryFactory, BranchCriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryFactory = queryFactory;
      this.session = session;
   }

   @Override
   public ResultSet<BranchReadable> getResults()  {
      try {
         return createSearch().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResultSet<IOseeBranch> getResultsAsId()  {
      try {
         return createSearchResultsAsIds().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount()  {
      try {
         return createCount().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public CancellableCallable<Integer> createCount()  {
      return queryFactory.createBranchCount(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<BranchReadable>> createSearch()  {
      return queryFactory.createBranchSearch(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<IOseeBranch>> createSearchResultsAsIds()  {
      return queryFactory.createBranchAsIdSearch(session, buildAndCopy());
   }

}
