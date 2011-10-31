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
import org.eclipse.osee.display.presenter.internal.FilteredResultSetCallable;
import org.eclipse.osee.display.presenter.internal.SearchExecutionCallback;
import org.eclipse.osee.display.presenter.internal.SearchParameters;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.PassThroughCallable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.Graph;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.eclipse.osee.orcs.search.StringOperator;
import com.google.common.collect.Lists;

/**
 * @author John Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final QueryFactory queryFactory;
   private final Graph graph;

   private final ArtifactProviderCache cache = new ArtifactProviderCache();
   private final ArtifactFilter filter = new ArtifactFilter(this);

   public ArtifactProviderImpl(Log logger, ExecutorAdmin executorAdmin, QueryFactory queryFactory, Graph graph) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.queryFactory = queryFactory;
      this.graph = graph;
   }

   protected QueryFactory getFactory() {
      return queryFactory;
   }

   protected Graph getGraph() {
      return graph;
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return getArtifactByGuid(branch, token.getGuid());
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      ReadableArtifact item = getFactory().fromBranch(branch).andGuidsOrHrids(guid).getResults().getOneOrNull();
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

      Callable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> callable =
         createSearchCallable(params, callback);

      SearchExecutionCallback providerCallback = new SearchExecutionCallback(logger, cache, callback);

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

   private Callable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> createSearchCallable(SearchParameters params, AsyncSearchListener callback) throws OseeCoreException {
      Callable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> callable;
      if (cache.isSearchCached(params)) {
         callable =
            new PassThroughCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>>(cache.getSearchResults());
      } else {
         IAttributeType type = params.isNameOnly() ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;
         QueryBuilder builder = getFactory().fromBranch(params.getBranch());
         builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, params.getSearchPhrase());
         callable = new FilteredResultSetCallable(executorAdmin, filter, builder.createSearchWithMatches());
      }
      return callable;
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<ReadableArtifact> artifacts = graph.getRelatedArtifacts(art, relationTypeSide);
      try {
         Utility.filter(artifacts, filter);
      } catch (Exception ex) {
         logger.error(ex, "Sanitization error");
         OseeExceptions.wrapAndThrow(ex);
      }
      Utility.sort(artifacts);
      return artifacts;
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      ReadableArtifact item = graph.getRelatedArtifact(art, relationTypeSide);
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
   public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
      ReadableArtifact parent = null;
      if (cache.isParentCached(art)) {
         parent = cache.getParent(art);
      } else {
         parent = getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
         if (parent != null) {
            cache.cacheParent(art, parent);
         }
      }
      return parent;
   }

   @Override
   public List<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException {
      Collection<IRelationTypeSide> existingRelationTypes = graph.getExistingRelationTypes(art);
      Set<RelationType> toReturn = new HashSet<RelationType>();
      for (IRelationTypeSide side : existingRelationTypes) {
         toReturn.add(graph.getFullRelationType(side));
      }
      List<RelationType> listToReturn = Lists.newLinkedList(toReturn);
      Collections.sort(listToReturn);
      return listToReturn;
   }

   @Override
   public void cancelSearch() {
      if (cache.isSearchInProgress()) {
         cache.getSearchFuture().cancel(true);
      }
   }
}
