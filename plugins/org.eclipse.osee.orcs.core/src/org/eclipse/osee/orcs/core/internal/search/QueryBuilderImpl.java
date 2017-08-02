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
package org.eclipse.osee.orcs.core.internal.search;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class QueryBuilderImpl extends ArtifactQueryBuilderImpl<QueryBuilder> implements QueryBuilder {

   private final CallableQueryFactory queryFactory;
   private final OrcsSession session;

   public QueryBuilderImpl(CallableQueryFactory queryFactory, CriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryFactory = queryFactory;
      this.session = session;
   }

   @Override
   public ResultSet<ArtifactReadable> getResults() throws OseeCoreException {
      try {
         return createSearch().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() throws OseeCoreException {
      try {
         return createSearchWithMatches().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount() throws OseeCoreException {
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
   public ResultSet<? extends ArtifactId> getResultsAsLocalIds() throws OseeCoreException {
      try {
         return createSearchResultsAsLocalIds().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch() throws OseeCoreException {
      return queryFactory.createSearch(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches() throws OseeCoreException {
      return queryFactory.createSearchWithMatches(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<ResultSet<? extends ArtifactId>> createSearchResultsAsLocalIds() throws OseeCoreException {
      return queryFactory.createLocalIdSearch(session, buildAndCopy());
   }

   @Override
   public CancellableCallable<Integer> createCount() throws OseeCoreException {
      return queryFactory.createCount(session, buildAndCopy());
   }

}
