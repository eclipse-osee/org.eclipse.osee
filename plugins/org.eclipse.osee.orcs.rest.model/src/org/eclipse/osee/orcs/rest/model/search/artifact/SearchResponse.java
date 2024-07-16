/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.model.search.artifact;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
@XmlRootElement(name = "SearchResponse")
public class SearchResponse implements SearchResult {

   private int total;
   private long searchTime;
   private String version;
   private SearchRequest searchRequest;

   @XmlTransient
   private List<ArtifactId> ids = new LinkedList<>();

   @XmlTransient
   private List<SearchMatch> searchMatches = new LinkedList<>();

   public void setSearchRequest(SearchRequest searchRequest) {
      this.searchRequest = searchRequest;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void setTotal(int total) {
      this.total = total;
   }

   public void setSearchTime(long searchTime) {
      this.searchTime = searchTime;
   }

   @Override
   public int getTotal() {
      return total;
   }

   @Override
   public long getSearchTime() {
      return searchTime;
   }

   @Override
   public String getVersion() {
      return version;
   }

   public SearchRequest getSearchRequest() {
      return searchRequest;
   }

   @Override
   @XmlElementWrapper(name = "ids")
   @XmlElement(name = "id")
   public List<ArtifactId> getIds() {
      if (ids == null) {
         ids = new LinkedList<>();
      }
      return ids;
   }

   public void setIds(List<ArtifactId> ids) {
      this.ids = ids;
   }

   @Override
   public SearchParameters getSearchParameters() {
      return getSearchRequest();
   }

   public void setMatches(List<SearchMatch> searchMatches) {
      this.searchMatches = searchMatches;
   }

   @Override
   @XmlElementWrapper(name = "matches")
   @XmlElement(name = "match")
   public List<SearchMatch> getSearchMatches() {
      return searchMatches;
   }

}
