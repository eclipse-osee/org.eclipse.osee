/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.display.presenter.internal.ArtifactProviderCache;
import org.eclipse.osee.display.presenter.internal.FilteredArtifactCallable;
import org.eclipse.osee.display.presenter.internal.FilteredResultSetCallable;
import org.eclipse.osee.display.presenter.internal.SearchExecutionCallback;
import org.eclipse.osee.display.presenter.internal.SearchParameters;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.PassThroughCallable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import com.google.common.collect.Lists;

/**
 * @author John R. Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final QueryFactory queryFactory;

   private final ArtifactProviderCache cache = new ArtifactProviderCache();
   private final ArtifactFilter filter = new ArtifactFilter(this);
   private final GraphReadable graph;

   public ArtifactProviderImpl(Log logger, ExecutorAdmin executorAdmin, QueryFactory queryFactory, GraphReadable graph) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.queryFactory = queryFactory;
      this.graph = graph;
   }

   protected void setFilterAllTypesAllowed(boolean allTypesAllowed) {
      filter.setAllTypesAllowed(allTypesAllowed);
   }

   protected QueryFactory getFactory() {
      return queryFactory;
   }

   @Override
   public ArtifactReadable getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return getArtifactByGuid(branch, token.getGuid());
   }

   @Override
   public ArtifactReadable getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      ArtifactReadable item = getFactory().fromBranch(branch).andGuidsOrHrids(guid).getResults().getOneOrNull();
      try {
         if (!filter.accept(item)) {
            item = null;
         }
      } catch (Exception ex) {
         logger.error(ex, "Sanitization error");
         OseeExceptions.wrapAndThrow(ex);
      }
      return item;
   }

   @Override
   public void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, AsyncSearchListener callback) throws OseeCoreException {
      SearchParameters params = new SearchParameters(branch, nameOnly, searchPhrase);

      Callable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> callable =
         createSearchCallable(params, callback);

      SearchExecutionCallback providerCallback = new SearchExecutionCallback(cache, callback);

      Future<?> searchFuture = null;
      try {
         searchFuture = executorAdmin.schedule(callable, providerCallback);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      cache.clearSearchCache();
      cache.cacheSearch(params);
      cache.cacheSearchFuture(searchFuture);
   }

   private Callable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchCallable(SearchParameters params, AsyncSearchListener callback) throws OseeCoreException {
      Callable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> callable;
      if (cache.isSearchCached(params)) {
         callable =
            new PassThroughCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>>(cache.getSearchResults());
      } else {
         IAttributeType type = params.isNameOnly() ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;
         QueryBuilder builder = getFactory().fromBranch(params.getBranch());
         builder.and(type, params.getSearchPhrase(), TokenDelimiterMatch.ANY, CaseType.IGNORE_CASE,
            TokenOrderType.ANY_ORDER, MatchTokenCountType.IGNORE_TOKEN_COUNT);
         callable = new FilteredResultSetCallable(executorAdmin, filter, builder.createSearchWithMatches());
      }
      return callable;
   }

   @Override
   public ResultSet<ArtifactReadable> getRelatedArtifacts(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      final ResultSet<ArtifactReadable> artifacts = graph.getRelatedArtifacts(relationTypeSide, art);
      List<ArtifactReadable> results = Collections.emptyList();
      try {
         FilteredArtifactCallable callable = new FilteredArtifactCallable(executorAdmin, filter, artifacts);
         Future<? extends Iterable<ArtifactReadable>> future = executorAdmin.schedule(callable);
         results = Utility.sort(future.get());
      } catch (Exception ex) {
         logger.error(ex, "Sanitization error");
         OseeExceptions.wrapAndThrow(ex);
      }
      return new ResultSetList<ArtifactReadable>(results);
   }

   @Override
   public ArtifactReadable getRelatedArtifact(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      ArtifactReadable item = graph.getRelatedArtifacts(relationTypeSide, art).getOneOrNull();
      try {
         if (!filter.accept(item)) {
            item = null;
         }
      } catch (Exception ex) {
         logger.error(ex, "Sanitization error");
         OseeExceptions.wrapAndThrow(ex);
      }
      return item;
   }

   @Override
   public ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException {
      ArtifactReadable parent = null;
      if (cache.isParentCached(art)) {
         parent = cache.getParent(art);
      } else {
         parent = getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
         cache.cacheParent(art, parent);
      }
      return parent;
   }

   @Override
   public Collection<? extends IRelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException {
      Collection<IRelationTypeSide> existingRelationTypes = graph.getExistingRelationTypes(art);
      Set<IRelationType> toReturn = new HashSet<IRelationType>();
      for (IRelationTypeSide side : existingRelationTypes) {
         toReturn.add(side);
      }
      List<? extends IRelationType> listToReturn = Lists.newLinkedList(toReturn);
      java.util.Collections.sort(listToReturn);
      return listToReturn;
   }

   @Override
   public String getSideAName(IRelationType type) throws OseeCoreException {
      return graph.getTypes().getSideAName(type);
   }

   @Override
   public String getSideBName(IRelationType type) throws OseeCoreException {
      return graph.getTypes().getSideBName(type);
   }

   @Override
   public void cancelSearch() {
      if (cache.isSearchInProgress()) {
         cache.getSearchFuture().cancel(true);
      }
   }

}
