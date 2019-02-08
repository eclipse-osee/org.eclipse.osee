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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaIdQuery;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTokenQuery;
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
   private final QueryEngine queryEngine;

   public QueryBuilderImpl(CallableQueryFactory queryFactory, CriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryFactory = queryFactory;
      this.session = session;
      this.queryEngine = queryFactory.getQueryEngine();
   }

   @Override
   public ArtifactToken loadArtifactToken() {
      return loadArtifact(this::loadArtifactTokens);
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens() {
      return loadArtifactTokens(Name);
   }

   @Override
   public Map<ArtifactId, ArtifactToken> loadArtifactTokenMap() {
      getQueryData().addCriteria(new CriteriaTokenQuery(Name));
      return queryEngine.loadArtifactTokenMap(getQueryData());
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens(AttributeTypeId attributeType) {
      getQueryData().addCriteria(new CriteriaTokenQuery(attributeType));
      return queryEngine.loadArtifactTokens(getQueryData());
   }

   @Override
   public Map<ArtifactId, ArtifactReadable> loadArtifactMap() {
      Map<ArtifactId, ArtifactReadable> artifacts = new HashMap<>(10000);
      getResults().forEach(artifact -> artifacts.put(artifact, artifact));
      return artifacts;
   }

   @Override
   public ArtifactId loadArtifactId() {
      return loadArtifact(this::loadArtifactIds);
   }

   private <T> T loadArtifact(Supplier<List<T>> supplier) {
      List<T> artifacts = supplier.get();
      if (artifacts.size() != 1) {
         throw new OseeCoreException("Expected exactly 1 artifact not %s", artifacts.size());
      }
      return artifacts.get(0);
   }

   @Override
   public List<ArtifactId> loadArtifactIds() {
      getQueryData().addCriteria(new CriteriaIdQuery());
      return queryEngine.loadArtifactIds(getQueryData());
   }

   @Override
   public ResultSet<ArtifactReadable> getResults() {
      try {
         return createSearch().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ArtifactReadable getArtifact() {
      return getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable getArtifactOrNull() {
      return getResults().getAtMostOneOrNull();
   }

   @Override
   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() {
      try {
         return createSearchWithMatches().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount() {
      return queryEngine.getArtifactCount(getQueryData());
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }

   @Override
   public ResultSet<? extends ArtifactId> getResultsIds() {
      try {
         return createSearchResultsAsIds().call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch() {
      return queryFactory.createSearch(session, getQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches() {
      return queryFactory.createSearchWithMatches(session, getQueryData());
   }

   @Override
   public CancellableCallable<ResultSet<? extends ArtifactId>> createSearchResultsAsIds() {
      return queryFactory.createLocalIdSearch(session, getQueryData());
   }

   @Override
   public ArtifactToken getArtifactOrSentinal() {
      ArtifactToken art = getArtifactOrNull();
      if (art == null) {
         return ArtifactToken.SENTINEL;
      }
      return art;
   }

   @Override
   public ArtifactToken getAtMostOneOrSentinal() {
      ResultSet<ArtifactReadable> artifacts = getResults();
      if (artifacts.isEmpty()) {
         return ArtifactToken.SENTINEL;
      } else if (artifacts.size() > 1) {
         throw new OseeStateException(String.format("Expected 0..1, found %s", artifacts.size()));
      }
      return artifacts.iterator().next();
   }
}