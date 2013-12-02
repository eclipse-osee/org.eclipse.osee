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
package org.eclipse.osee.display.presenter.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import com.google.common.collect.MapMaker;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactProviderCache {
   private static final ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> EMPTY_SET =
      ResultSets.emptyResultSet();
   private final ConcurrentMap<ArtifactReadable, ArtifactReadable> parentCache;
   private final Set<ArtifactReadable> artifactsWithNoParent = new HashSet<ArtifactReadable>();

   private ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> searchResults;
   private SearchParameters searchParameters;
   private Future<?> future;

   public ArtifactProviderCache() {
      this.parentCache = new MapMaker()//
      .initialCapacity(500)//
      //      .expiration(30, TimeUnit.MINUTES)//
      .makeMap();
      clearSearchCache();
   }

   public void cacheParent(ArtifactReadable art, ArtifactReadable parent) {
      if (parent != null) {
         parentCache.put(art, parent);
      } else {
         artifactsWithNoParent.add(art);
      }
   }

   public boolean isParentCached(ArtifactReadable artifact) {
      return parentCache.containsKey(artifact) || artifactsWithNoParent.contains(artifact);
   }

   public ArtifactReadable getParent(ArtifactReadable artifact) {
      return parentCache.get(artifact);
   }

   public void cacheResults(ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> searchResults) {
      this.searchResults = searchResults;
   }

   public void cacheSearch(SearchParameters searchParameters) {
      this.searchParameters = searchParameters;
   }

   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getSearchResults() {
      return searchResults;
   }

   public SearchParameters getSearchParameters() {
      return searchParameters;
   }

   public boolean isSearchCached(SearchParameters params) {
      return searchParameters != null && searchParameters.equals(params);
   }

   public void clearSearchCache() {
      cacheSearch(null);
      cacheResults(EMPTY_SET);
      cacheSearchFuture(null);
   }

   public void cacheSearchFuture(Future<?> future) {
      this.future = future;
   }

   public Future<?> getSearchFuture() {
      return future;
   }

   public boolean isSearchInProgress() {
      return future != null && !future.isDone() && !future.isCancelled();
   }
}
