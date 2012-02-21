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
package org.eclipse.osee.orcs.rest.internal.search;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class SearchResult {

   // All else from out message

   private int total;
   private long searchTime;
   private String version;

   private SearchParameters searchParameters;

   @XmlTransient
   private List<Predicate> predicates;

   public SearchParameters getSearchParams() {
      return searchParameters;
   }

   public void setSearchParams(SearchParameters searchParams) {
      this.searchParameters = searchParams;
   }

   public void setPredicates(List<Predicate> predicates) {
      this.predicates = predicates;
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

   public int getTotal() {
      return total;
   }

   public long getSearchTime() {
      return searchTime;
   }

   public String getVersion() {
      return version;
   }

   @XmlElementWrapper(name = "predicates")
   @XmlElement(name = "predicate")
   public List<Predicate> getPredicates() {
      return predicates;
   }

   public SearchParameters getSearchParameters() {
      return searchParameters;
   }

}
