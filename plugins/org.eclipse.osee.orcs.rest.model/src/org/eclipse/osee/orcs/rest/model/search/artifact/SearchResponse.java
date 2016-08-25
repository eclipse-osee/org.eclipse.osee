/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.search.artifact;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
