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
package org.eclipse.osee.framework.core.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class SearchResponse {

   private final Map<String, Long> searchTags = new LinkedHashMap<String, Long>();
   private final CompositeKeyHashMap<Integer, Integer, ArtifactMatchMetaData> data =
      new CompositeKeyHashMap<Integer, Integer, ArtifactMatchMetaData>();

   private String errorMessage;

   public SearchResponse() {
      this.errorMessage = Strings.emptyString();
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage != null ? errorMessage : Strings.emptyString();
   }

   public Map<String, Long> getSearchTags() {
      return searchTags;
   }

   public AttributeMatchMetaData add(int branchId, int artId, long gammaId) {
      ArtifactMatchMetaData artifact = getOrCreateArtifactMatch(branchId, artId);
      return artifact.getOrCreate(artId, gammaId);
   }

   public void add(int branchId, int artId, long gammaId, int startPosition, int endPosition) {
      AttributeMatchMetaData attribute = add(branchId, artId, gammaId);
      attribute.addLocation(startPosition, endPosition);
   }

   public void add(int branchId, int artId, long gammaId, Collection<MatchLocation> matches) {
      AttributeMatchMetaData attribute = add(branchId, artId, gammaId);
      attribute.addLocations(matches);
   }

   private ArtifactMatchMetaData getOrCreateArtifactMatch(int branchId, int artId) {
      ArtifactMatchMetaData artifact = getArtifactMatch(branchId, artId);
      if (artifact == null) {
         artifact = new ArtifactMatchMetaData(branchId, artId);
         data.put(branchId, artId, artifact);
      }
      return artifact;
   }

   public Collection<ArtifactMatchMetaData> getAll() {
      return data.values();
   }

   public int matches() {
      int count = 0;
      for (ArtifactMatchMetaData meta : data.values()) {
         count += meta.matches();
      }
      return count;
   }

   public Set<Integer> getBranchIds() {
      Set<Integer> branchIds = new HashSet<Integer>();
      for (Pair<Integer, Integer> entry : data.getEnumeratedKeys()) {
         branchIds.add(entry.getFirst());
      }
      return branchIds;
   }

   public Collection<Integer> getArtifactIds(int branchId) {
      return data.getKeyedValues(branchId).keySet();
   }

   public Collection<ArtifactMatchMetaData> getArtifacts(int branchId) {
      return data.getValues(branchId);
   }

   public ArtifactMatchMetaData getArtifactMatch(int branchId, int artId) {
      return data.get(branchId, artId);
   }

   public boolean isEmpty() {
      return data.isEmpty();
   }

   public static final class ArtifactMatchMetaData {
      private final int artId;
      private final int branchId;
      private final Map<Long, AttributeMatchMetaData> attributeMatch = new HashMap<Long, AttributeMatchMetaData>();

      public ArtifactMatchMetaData(int branchId, int artId) {
         this.branchId = branchId;
         this.artId = artId;
      }

      public int getArtId() {
         return artId;
      }

      public int getBranchId() {
         return branchId;
      }

      public Collection<AttributeMatchMetaData> getAll() {
         return attributeMatch.values();
      }

      AttributeMatchMetaData getOrCreate(int artId, Long gammaId) {
         AttributeMatchMetaData attribute = getAttributeMatch(gammaId);
         if (attribute == null) {
            attribute = new AttributeMatchMetaData(artId, gammaId);
            attributeMatch.put(gammaId, attribute);
         }
         return attribute;
      }

      public AttributeMatchMetaData getAttributeMatch(Long gammaId) {
         return attributeMatch.get(gammaId);
      }

      public int size() {
         return attributeMatch.size();
      }

      public int matches() {
         int count = 0;
         for (AttributeMatchMetaData match : attributeMatch.values()) {
            count += match.matches();
         }
         return count;
      }

      @Override
      public int hashCode() {
         final int prime = 11;
         int result = 1;
         result = prime * result + (artId * 53);
         result = prime * result + (branchId * 11);
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         ArtifactMatchMetaData other = (ArtifactMatchMetaData) obj;
         if (artId != other.artId) {
            return false;
         }
         if (branchId != other.branchId) {
            return false;
         }
         return true;
      }

   }

   public static final class AttributeMatchMetaData {
      private final int artId;
      private final long gammaId;
      private final Set<MatchLocation> matches = new HashSet<MatchLocation>(0);

      public AttributeMatchMetaData(int artId, long gammaId) {
         this.artId = artId;
         this.gammaId = gammaId;
      }

      public int getArtId() {
         return artId;
      }

      public long getGammaId() {
         return gammaId;
      }

      public void addLocation(int start, int stop) {
         matches.add(new MatchLocation(start, stop));
      }

      public void addLocations(Collection<MatchLocation> locations) {
         matches.addAll(locations);
      }

      public Collection<MatchLocation> getLocations() {
         return matches;
      }

      public int matches() {
         int locData = matches.size();
         return locData == 0 ? 1 : locData;
      }

      @Override
      public int hashCode() {
         final int prime = 17;
         int result = 1;
         result = prime * result + (int) (gammaId ^ (gammaId >>> 32));
         result = prime * result + (artId * 53);
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         AttributeMatchMetaData other = (AttributeMatchMetaData) obj;
         if (gammaId != other.gammaId) {
            return false;
         }
         if (artId != other.artId) {
            return false;
         }
         return true;
      }
   }

}
