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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class SearchResult implements ITagCollector {

   private final String rawSearch;
   private final Map<String, Long> searchTags = new LinkedHashMap<String, Long>();
   private final Map<Integer, Map<Integer, ArtifactMatch>> entries =
      new HashMap<Integer, Map<Integer, ArtifactMatch>>();
   private int size;
   private String errorMessage;

   public SearchResult(String rawSearch) {
      this.rawSearch = rawSearch;
      this.errorMessage = Strings.emptyString();
      this.size = 0;
   }

   public String getRawSearch() {
      return rawSearch;
   }

   public Map<String, Long> getSearchTags() {
      return searchTags;
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

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage != null ? errorMessage : Strings.emptyString();
   }

   @Override
   public void addTag(String word, Long codedTag) {
      this.searchTags.put(word, codedTag);
   }

}
