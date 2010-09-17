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
package org.eclipse.osee.framework.core.message.internal.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.message.SearchResponse.ArtifactMatchMetaData;
import org.eclipse.osee.framework.core.message.SearchResponse.AttributeMatchMetaData;
import org.eclipse.osee.framework.core.message.TranslationUtil;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class SearchResponseTranslator implements ITranslator<SearchResponse> {

   private static final int WITH_MATCH_LOCATION_ROW_SIZE = 5;
   private static final int NO_MATCH_LOCATION_ROW_SIZE = 3;

   private static enum Key {
      ERROR_MSG,
      SEARCH_TAGS_COUNT,
      SEARCH_TAG,
      MATCH_DATA,
      MATCH_LOCATION,
      MATCH_COUNT
   }

   @Override
   public SearchResponse convert(PropertyStore propertyStore) {
      SearchResponse response = new SearchResponse();
      response.setErrorMessage(propertyStore.get(Key.ERROR_MSG.name()));

      int count = propertyStore.getInt(Key.MATCH_COUNT.name());
      for (int index = 0; index < count; index++) {
         String key = TranslationUtil.createKey(Key.MATCH_DATA, index);
         int[] data = asIntArray(propertyStore.getArray(key));
         int branchId = data[0];
         int artId = data[1];
         int gammaId = data[2];
         if (data.length == WITH_MATCH_LOCATION_ROW_SIZE) {
            response.add(branchId, artId, gammaId, data[3], data[4]);
         } else {
            response.add(branchId, artId, gammaId);
         }
      }

      count = propertyStore.getInt(Key.SEARCH_TAGS_COUNT.name());
      Map<String, Long> codedWords = response.getSearchTags();
      for (int index = 0; index < count; index++) {
         String key = TranslationUtil.createKey(Key.SEARCH_TAG, index);
         String[] data = propertyStore.getArray(key);
         codedWords.put(data[0], Long.parseLong(data[1]));
      }
      return response;
   }

   @Override
   public PropertyStore convert(SearchResponse object) {
      PropertyStore store = new PropertyStore();
      store.put(Key.ERROR_MSG.name(), object.getErrorMessage());
      Collection<String[]> data = toArray(object);
      int count = 0;
      for (String[] row : data) {
         String key = TranslationUtil.createKey(Key.MATCH_DATA, count);
         store.put(key, row);
         count++;
      }
      store.put(Key.MATCH_COUNT.name(), data.size());

      Map<String, Long> codedWords = object.getSearchTags();
      count = 0;
      for (Entry<String, Long> entry : codedWords.entrySet()) {
         String key = TranslationUtil.createKey(Key.SEARCH_TAG, count);
         store.put(key, new String[] {entry.getKey(), String.valueOf(entry.getValue())});
         count++;
      }
      store.put(Key.SEARCH_TAGS_COUNT.name(), codedWords.size());
      return store;
   }

   private int[] asIntArray(String[] data) {
      int[] toReturn = new int[data.length];
      for (int index = 0; index < data.length; index++) {
         toReturn[index] = Integer.valueOf(data[index]);
      }
      return toReturn;
   }

   private Collection<String[]> toArray(SearchResponse object) {
      Collection<String[]> toReturn = new ArrayList<String[]>();
      for (ArtifactMatchMetaData artMeta : object.getAll()) {
         int branchId = artMeta.getBranchId();
         for (AttributeMatchMetaData attrMeta : artMeta.getAll()) {
            Collection<MatchLocation> locs = attrMeta.getLocations();
            if (!locs.isEmpty()) {
               for (MatchLocation location : locs) {
                  String[] row = new String[WITH_MATCH_LOCATION_ROW_SIZE];
                  row[0] = String.valueOf(branchId);
                  row[1] = String.valueOf(attrMeta.getArtId());
                  row[2] = String.valueOf(attrMeta.getGammaId());
                  row[3] = String.valueOf(location.getStartPosition());
                  row[4] = String.valueOf(location.getEndPosition());
                  toReturn.add(row);
               }
            } else {
               String[] row = new String[NO_MATCH_LOCATION_ROW_SIZE];
               row[0] = String.valueOf(branchId);
               row[1] = String.valueOf(attrMeta.getArtId());
               row[2] = String.valueOf(attrMeta.getGammaId());
               toReturn.add(row);
            }
         }
      }
      return toReturn;
   }
}
