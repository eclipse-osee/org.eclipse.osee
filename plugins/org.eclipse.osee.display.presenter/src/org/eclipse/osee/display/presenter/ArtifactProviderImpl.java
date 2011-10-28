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
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.type.RelationType;
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
import com.google.common.collect.Lists;
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
   private Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> searchFuture;
   private ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> cachedResults;
   private SearchParameters lastSearchParameters;
   private final ExecutorAdmin executor;

   private final Log logger;

   public ArtifactProviderImpl(Log logger, ExecutorAdmin executorAdmin, OrcsApi oseeApi, ApplicationContext context) {
      this.logger = logger;
      this.oseeApi = oseeApi;
      this.context = context;
      this.graph = oseeApi.getGraph(context);
      sanitizer = new ArtifactSanitizer(executorAdmin, this);
      executor = executorAdmin;

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

   @Override
   public void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, AsyncSearchListener callback) throws OseeCoreException {
      SearchParameters params = new SearchParameters(branch, nameOnly, searchPhrase);

      ProviderExecutionCallback providerCallback = new ProviderExecutionCallback(callback);
      if (lastSearchParameters != null && lastSearchParameters.equals(params)) {
         CachedResultsCallable callable = new CachedResultsCallable(cachedResults);
         try {
            searchFuture = executor.schedule(callable, providerCallback);
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      } else {
         IAttributeType type = nameOnly ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;
         QueryBuilder builder = getFactory().fromBranch(branch);
         builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase);

         lastSearchParameters = params;
         cachedResults = null;

         searchFuture = builder.searchWithMatches(providerCallback);
      }
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      List<ReadableArtifact> toReturn = sanitizer.sanitizeArtifacts(graph.getRelatedArtifacts(art, relationTypeSide));
      Utility.sort(toReturn);
      return toReturn;
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
      if (searchFuture != null) {
         searchFuture.cancel(true);
      }
   }

   private class ProviderExecutionCallback implements ExecutionCallback<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      private final AsyncSearchListener callback;

      public ProviderExecutionCallback(AsyncSearchListener callback) {
         this.callback = callback;
      }

      @Override
      public void onSuccess(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> result) {
         FilteredResultSet results = new FilteredResultSet(result);
         cachedResults = results;
         try {
            callback.onSearchComplete(results.getList());
         } catch (Exception ex) {
            logger.error(ex, "Error in async search");
         }
      }

      @Override
      public void onFailure(Throwable throwable) {
         lastSearchParameters = null;
         callback.onSearchFailed(throwable);
      }

      @Override
      public void onCancelled() {
         lastSearchParameters = null;
         callback.onSearchCancelled();
      }
   }

   private class FilteredResultSet implements ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> {

      private final ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> decorated;

      private List<Match<ReadableArtifact, ReadableAttribute<?>>> filtered;

      public FilteredResultSet(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> decorated) {
         this.decorated = decorated;
         this.filtered = null;
      }

      @Override
      public Match<ReadableArtifact, ReadableAttribute<?>> getOneOrNull() throws OseeCoreException {
         List<Match<ReadableArtifact, ReadableAttribute<?>>> result = getList();
         return result.isEmpty() ? null : result.iterator().next();
      }

      @Override
      public Match<ReadableArtifact, ReadableAttribute<?>> getExactlyOne() throws OseeCoreException {
         List<Match<ReadableArtifact, ReadableAttribute<?>>> result = getList();
         if (result.isEmpty()) {
            throw new ArtifactDoesNotExist("No artifacts found");
         } else if (result.size() > 1) {
            throw new MultipleArtifactsExist("Multiple artifact found - total [%s]", result.size());
         }
         return result.iterator().next();
      }

      @Override
      public List<Match<ReadableArtifact, ReadableAttribute<?>>> getList() throws OseeCoreException {
         if (filtered == null) {
            filter();
         }
         return filtered;
      }

      @Override
      public Iterable<Match<ReadableArtifact, ReadableAttribute<?>>> getIterable(int fetchSize) throws OseeCoreException {
         return getList();
      }

      private synchronized void filter() throws OseeCoreException {
         try {
            filtered = sanitizer.filter(decorated.getList());
            Utility.sortResults(filtered);
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }

   }

   private class CachedResultsCallable implements Callable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      private final ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> lastResults;

      public CachedResultsCallable(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> lastResults) {
         this.lastResults = lastResults;
      }

      @Override
      public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
         return lastResults;
      }
   }

   private class SearchParameters {
      private final IOseeBranch branch;
      private final boolean nameOnly;
      private final String searchPhrase;

      public SearchParameters(IOseeBranch branch, boolean nameOnly, String searchPhrase) {
         this.branch = branch;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
      }

      public IOseeBranch getBranch() {
         return branch;
      }

      public boolean isNameOnly() {
         return nameOnly;
      }

      public String getSearchPhrase() {
         return searchPhrase;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof SearchParameters) {
            SearchParameters sObj = (SearchParameters) obj;
            return sObj.getBranch().getGuid().equals(branch.getGuid()) && sObj.isNameOnly() == nameOnly && sObj.getSearchPhrase().equals(
               searchPhrase);
         }
         return false;
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

   }

}
