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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerDecorator;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatchDataHandler extends LoadDataHandlerDecorator {

   private final ArtifactBuilder handler;

   private final Map<Integer, ArtifactMatch> matches = new HashMap<Integer, ArtifactMatch>();
   private List<Match<ArtifactReadable, AttributeReadable<?>>> results;

   public ArtifactMatchDataHandler(ArtifactBuilder handler) {
      super(handler);
      this.handler = handler;
   }

   @Override
   public void onData(AttributeData data, MatchLocation match) throws OseeCoreException {
      super.onData(data, match);
      Integer artId = data.getArtifactId();
      synchronized (matches) {
         ArtifactMatch artifactMatch = matches.get(artId);
         if (artifactMatch == null) {
            artifactMatch = new ArtifactMatch();
            artifactMatch.addLocation(data.getLocalId(), match);
         }
         matches.put(artId, artifactMatch);
      }
   }

   @Override
   public void onLoadEnd() {
      super.onLoadEnd();
      buildResults();
   }

   private void buildResults() {
      List<ArtifactReadable> loaded = handler.getArtifacts();

      if (loaded.isEmpty()) {
         results = Collections.emptyList();
      } else {
         results = Lists.newLinkedList();
         for (ArtifactReadable item : loaded) {
            ArtifactMatch artifactMatch = matches.get(item.getLocalId());
            if (artifactMatch != null) {
               artifactMatch.setArtifactReadable(item);
            }
            results.add(artifactMatch);
         }
      }
      matches.clear();
   }

   public List<Match<ArtifactReadable, AttributeReadable<?>>> getResults() {
      return results;
   }

   private static <K, V> ListMultimap<K, V> newLinkedHashListMultimap() {
      Map<K, Collection<V>> map = new LinkedHashMap<K, Collection<V>>();
      return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
         @Override
         public List<V> get() {
            return Lists.newArrayList();
         }
      });
   }

   private static final class ArtifactMatch implements Match<ArtifactReadable, AttributeReadable<?>> {

      private final ListMultimap<Integer, MatchLocation> matches = newLinkedHashListMultimap();
      private ArtifactReadable item;

      public ArtifactMatch() {
         super();
      }

      public void setArtifactReadable(ArtifactReadable item) {
         this.item = item;
      }

      public void addLocation(Integer attrId, MatchLocation location) {
         matches.put(attrId, location);
      }

      @Override
      public boolean hasLocationData() {
         return !matches.isEmpty();
      }

      @Override
      public ArtifactReadable getItem() {
         return item;
      }

      @Override
      public Collection<AttributeReadable<?>> getElements() throws OseeCoreException {
         Collection<AttributeReadable<?>> filtered = Lists.newLinkedList();
         List<AttributeReadable<Object>> attributes = item.getAttributes();
         for (AttributeReadable<?> attribute : attributes) {
            if (matches.containsKey(attribute.getId())) {
               filtered.add(attribute);
            }
         }
         return filtered;
      }

      @Override
      public List<MatchLocation> getLocation(AttributeReadable<?> element) {
         List<MatchLocation> toReturn = matches.get(element.getId());
         return toReturn != null ? toReturn : Collections.<MatchLocation> emptyList();
      }

      @Override
      public String toString() {
         return "ArtifactMatch [item=" + item + ", matches=" + matches + "]";
      }
   }

}