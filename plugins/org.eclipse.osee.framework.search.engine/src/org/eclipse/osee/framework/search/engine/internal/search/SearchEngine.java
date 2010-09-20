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
package org.eclipse.osee.framework.search.engine.internal.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.internal.Activator;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngine implements ISearchEngine {

   private final SearchStatistics statistics;
   private final TagProcessor tagProcessor;
   private final IAttributeTaggerProviderManager taggingManager;
   private final AttributeTypeCache attributeTypeCache;
   private final BranchCache branchCache;

   public SearchEngine(SearchStatistics statistics, TagProcessor tagProcessor, IAttributeTaggerProviderManager taggingManager, AttributeTypeCache attributeTypeCache, BranchCache branchCache) {
      this.statistics = statistics;
      this.tagProcessor = tagProcessor;
      this.taggingManager = taggingManager;
      this.attributeTypeCache = attributeTypeCache;
      this.branchCache = branchCache;
   }

   private Collection<AttributeType> getAttributeTypes(Collection<IAttributeType> tokens) throws OseeCoreException {
      Collection<AttributeType> attributeTypes = new HashSet<AttributeType>();
      for (IAttributeType identity : tokens) {
         AttributeType type = attributeTypeCache.get(identity);
         if (type != null) {
            attributeTypes.add(type);
         } else {
            throw new OseeStateException(String.format("Search Attribute Type Filter - attribute type [%s] not found",
               identity));
         }
      }
      return attributeTypes;
   }

   @Override
   public void search(SearchRequest searchRequest, final SearchResponse searchResponse) throws Exception {
      String searchString = searchRequest.getRawSearch();

      long startTime = System.currentTimeMillis();

      final Map<String, Long> searchTags = searchResponse.getSearchTags();
      tagProcessor.collectFromString(searchString, new ITagCollector() {

         @Override
         public void addTag(String word, Long codedTag) {
            searchTags.put(word, codedTag);
         }
      });

      if (searchTags.isEmpty()) {
         searchResponse.setErrorMessage("No words found in search string. Please try again.");
      } else {
         long startDataStoreSearch = System.currentTimeMillis();

         SearchOptions options = searchRequest.getOptions();
         Collection<IAttributeType> attributeTypeTokens = options.getAttributeTypeFilter();
         Collection<AttributeType> attributeTypes = getAttributeTypes(attributeTypeTokens);

         int branchId = branchCache.get(searchRequest.getBranch()).getId();
         Collection<AttributeData> tagMatches =
            AttributeDataStore.getAttributesByTags(branchId, options.getDeletionFlag(), searchTags.values(),
               attributeTypes);
         String message =
            String.format("Attribute Search Query found [%d] in [%d] ms", tagMatches.size(),
               System.currentTimeMillis() - startDataStoreSearch);
         OseeLog.log(SearchEngine.class, Level.INFO, message);

         long timeAfterPass1 = System.currentTimeMillis() - startTime;
         long secondPass = System.currentTimeMillis();

         boolean bypassSecondPass = !options.isMatchWordOrder();
         if (bypassSecondPass) {
            for (AttributeData attributeData : tagMatches) {
               searchResponse.add(attributeData.getBranchId(), attributeData.getArtId(), attributeData.getGammaId());
            }
         } else {
            for (AttributeData attributeData : tagMatches) {
               try {
                  List<MatchLocation> locations = taggingManager.find(attributeData, searchString, options);
                  if (!locations.isEmpty()) {
                     searchResponse.add(attributeData.getBranchId(), attributeData.getArtId(),
                        attributeData.getGammaId(), locations);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Error processing: [%s]", attributeData));
               }
            }
         }
         secondPass = System.currentTimeMillis() - secondPass;

         String firstPassMsg =
            String.format("Pass 1: [%d items in %d ms] -",
               bypassSecondPass ? searchResponse.matches() : tagMatches.size(), timeAfterPass1);

         String secondPassMsg = String.format(" Pass 2: [%d items in %d ms]", searchResponse.matches(), secondPass);

         System.out.println(String.format("Search for [%s] - %s%s", searchString, firstPassMsg,
            bypassSecondPass ? "" : secondPassMsg));
         statistics.addEntry(searchRequest, searchResponse.matches(), System.currentTimeMillis() - startTime);
      }
   }

   @Override
   public void clearStatistics() {
      this.statistics.clear();
   }

   @Override
   public SearchStatistics getStatistics() {
      SearchStatistics toReturn = null;
      try {
         toReturn = this.statistics.clone();
      } catch (CloneNotSupportedException ex) {
         toReturn = SearchStatistics.EMPTY_STATS;
      }
      return toReturn;
   }
}
