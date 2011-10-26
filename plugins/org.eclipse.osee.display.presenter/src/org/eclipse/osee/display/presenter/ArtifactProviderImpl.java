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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.Graph;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.eclipse.osee.orcs.search.StringOperator;
import com.google.common.collect.MapMaker;

/**
 * @author John Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   private final OrcsApi oseeApi;
   private final ApplicationContext context;
   private final Graph graph;
   private final ConcurrentMap<ReadableArtifact, ReadableArtifact> parentCache;
   private final ArtifactSanitizer sanitizer;
   Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> searchFuture;

   private final Log logger;

   public ArtifactProviderImpl(Log logger, ExecutorAdmin executorAdmin, OrcsApi oseeApi, ApplicationContext context) {
      this.logger = logger;
      this.oseeApi = oseeApi;
      this.context = context;
      this.graph = oseeApi.getGraph(context);
      sanitizer = new ArtifactSanitizer(executorAdmin, this);

      this.parentCache = new MapMaker()//
      .initialCapacity(500)//
      .expiration(30, TimeUnit.MINUTES)//
      .makeMap();
   }

   protected QueryFactory getFactory() {
      return oseeApi.getQueryFactory(context);
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return getArtifactByGuid(branch, token.getGuid());
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      return sanitizer.sanitizeArtifact(getFactory().fromBranch(branch).andGuidsOrHrids(guid).getResults().getOneOrNull());
   }

   //   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
   //      List<Match<ReadableArtifact, ReadableAttribute<?>>> filtered = null;
   //
   //      IAttributeType type = nameOnly ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;
   //      QueryBuilder builder = getFactory().fromBranch(branch);
   //      builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase);
   //
   //      long startTime = 0;
   //      if (logger.isTraceEnabled()) {
   //         startTime = System.currentTimeMillis();
   //         logger.trace("Start Query: [%s]", Lib.getDateTimeString());
   //      }
   //
   //      ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> resultSet = builder.getMatches();
   //
   //      long delta = 0;
   //      if (logger.isTraceEnabled()) {
   //         logger.trace("End Query: [%s]", Lib.getElapseString(startTime));
   //         delta = System.currentTimeMillis();
   //      }
   //
   //      try {
   //         filtered = sanitizer.filter(resultSet.getList());
   //      } catch (Exception ex) {
   //         OseeExceptions.wrapAndThrow(ex);
   //      } finally {
   //         if (logger.isTraceEnabled()) {
   //            logger.trace("Sanitized in: [%s]", Lib.getElapseString(delta));
   //            logger.trace("Total Time: [%s]", Lib.getElapseString(startTime));
   //         }
   //      }
   //      return filtered;
   //   }

   @Override
   public void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, final AsyncSearchListener callback) throws OseeCoreException {
      IAttributeType type = nameOnly ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;
      QueryBuilder builder = getFactory().fromBranch(branch);
      builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase);
      ProviderExecutionCallback providerCallback = new ProviderExecutionCallback(callback);

      searchFuture = builder.searchWithMatches(providerCallback);
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return sanitizer.sanitizeArtifacts(graph.getRelatedArtifacts(art, relationTypeSide));
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return sanitizer.sanitizeArtifact(graph.getRelatedArtifact(art, relationTypeSide));
   }

   @Override
   public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
      ReadableArtifact parent = null;
      if (parentCache.containsKey(art)) {
         parent = parentCache.get(art);
      } else {
         parent = getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
         if (parent != null) {
            parentCache.put(art, parent);
         }
      }
      return sanitizer.sanitizeArtifact(parent);
   }

   @Override
   public Collection<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException {
      Collection<IRelationTypeSide> existingRelationTypes = graph.getExistingRelationTypes(art);
      Set<RelationType> toReturn = new HashSet<RelationType>();
      for (IRelationTypeSide side : existingRelationTypes) {
         toReturn.add(graph.getFullRelationType(side));
      }
      return toReturn;
   }

   @Override
   public void cancelSearch() {
      if (searchFuture != null) {
         searchFuture.cancel(true);
      }
   }

   private class ProviderExecutionCallback implements ExecutionCallback<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      private final AsyncSearchListener callback;
      private final long startTime = 0;

      public ProviderExecutionCallback(AsyncSearchListener callback) {
         this.callback = callback;
      }

      @Override
      public void onSuccess(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> result) {
         long delta = 0;
         if (logger.isTraceEnabled()) {
            logger.trace("End Query: [%s]", Lib.getElapseString(startTime));
            delta = System.currentTimeMillis();
         }
         try {
            List<Match<ReadableArtifact, ReadableAttribute<?>>> filtered = Collections.emptyList();
            if (result.getList() != null) {
               filtered = sanitizer.filter(result.getList());
            }
            callback.onSearchComplete(filtered);
         } catch (Exception ex) {
            logger.error(ex, "Error in async search");
         } finally {
            if (logger.isTraceEnabled()) {
               logger.trace("Sanitized in: [%s]", Lib.getElapseString(delta));
               logger.trace("Total Time: [%s]", Lib.getElapseString(startTime));
            }
         }
      }

      @Override
      public void onFailure(Throwable throwable) {
         callback.onSearchFailed(throwable);
      }

      @Override
      public void onCancelled() {
         callback.onSearchCancelled();
      }
   }

}
