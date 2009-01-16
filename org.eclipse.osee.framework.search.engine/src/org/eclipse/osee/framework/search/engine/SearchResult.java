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
package org.eclipse.osee.framework.search.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public class SearchResult {

   private Map<Integer, Map<Integer, ArtifactMatch>> entries;
   private int size;

   public SearchResult() {
      this.entries = new HashMap<Integer, Map<Integer, ArtifactMatch>>();
      size = 0;
   }

   public void add(int branchId, int artId, long gammaId) {
      add(branchId, artId, gammaId, null);
   }

   public void add(int branchId, int artId, long gammaId, List<MatchLocation> matches) {
      Map<Integer, ArtifactMatch> match = entries.get(branchId);
      ArtifactMatch artifact = null;
      if (match == null) {
         match = new HashMap<Integer, ArtifactMatch>();
         entries.put(branchId, match);
      } else {
         artifact = match.get(artId);
      }

      if (artifact == null) {
         artifact = new ArtifactMatch(branchId, artId);
         match.put(artId, artifact);
         size++;
      }

      artifact.addAttribute(gammaId, matches);
   }

   public Set<Integer> getBranchIds() {
      return entries.keySet();
   }

   public Set<Integer> getArtifactIds(int branch) {
      Map<Integer, ArtifactMatch> toReturn = entries.get(branch);
      if (toReturn == null) {
         return Collections.emptySet();
      }
      return toReturn.keySet();
   }

   public Collection<ArtifactMatch> getArtifacts(int branch) {
      Map<Integer, ArtifactMatch> toReturn = entries.get(branch);
      if (toReturn == null) {
         return Collections.emptyList();
      }
      return toReturn.values();
   }

   public int size() {
      return size;
   }

   public boolean isEmpty() {
      return size() == 0;
   }

   public class ArtifactMatch {
      private int artId;
      private int branchId;
      private boolean hasMatchLocations;
      private HashCollection<Long, MatchLocation> attributes;

      public ArtifactMatch(int branchId, int artId) {
         this.artId = artId;
         this.branchId = branchId;
         attributes = new HashCollection<Long, MatchLocation>();
         hasMatchLocations = false;
      }

      private void addAttribute(long gammaId, Collection<MatchLocation> matches) {
         if (matches != null && !matches.isEmpty()) {
            hasMatchLocations = true;
         }
         if (matches == null) {
            matches = Collections.emptyList();
         }
         attributes.put(gammaId, matches);
      }

      public Set<Long> getAttributes() {
         return attributes.keySet();
      }

      public Collection<MatchLocation> getMatchLocations(long gammaId) {
         return attributes.getValues(gammaId);
      }

      public boolean hasMatchLocations() {
         return hasMatchLocations;
      }

      public int getArtId() {
         return artId;
      }

      public int getBranchId() {
         return branchId;
      }
   }

}
