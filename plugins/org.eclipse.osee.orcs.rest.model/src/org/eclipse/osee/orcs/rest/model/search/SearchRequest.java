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
package org.eclipse.osee.orcs.rest.model.search;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class SearchRequest implements SearchParameters {

   private String branchUuid;
   private String alt;
   private String fields;
   private int fromTx;
   private boolean includeCache;
   private boolean includeDeleted;

   @XmlTransient
   private List<Predicate> predicates;

   public SearchRequest() {
      super();
   }

   public SearchRequest(String branchUuid, List<Predicate> predicates, String alt, String fields, int fromTx, boolean includeCache, boolean includeDeleted) {
      super();
      this.branchUuid = branchUuid;
      this.predicates = predicates;
      this.alt = alt;
      this.fields = fields;
      this.fromTx = fromTx;
      this.includeCache = includeCache;
      this.includeDeleted = includeDeleted;
   }

   @Override
   public String getBranchUuid() {
      return branchUuid;
   }

   @Override
   @XmlElementWrapper(name = "predicates")
   @XmlElement(name = "predicate")
   public List<Predicate> getPredicates() {
      return predicates;
   }

   @Override
   public String getAlt() {
      return alt;
   }

   @Override
   public String getFields() {
      return fields;
   }

   public void setBranchUuid(String uuid) {
      this.branchUuid = uuid;
   }

   public void setPredicates(List<Predicate> predicates) {
      this.predicates = predicates;
   }

   public void setAlt(String alt) {
      this.alt = alt;
   }

   public void setFields(String fields) {
      this.fields = fields;
   }

   @Override
   public boolean isIncludeCache() {
      return includeCache;
   }

   @Override
   public boolean isIncludeDeleted() {
      return includeDeleted;
   }

   @Override
   public int getFromTx() {
      return fromTx;
   }

   public void setFromTx(int fromTx) {
      this.fromTx = fromTx;
   }

   public void setIncludeCache(boolean includeCache) {
      this.includeCache = includeCache;
   }

   public void setIncludeDeleted(boolean includeDeleted) {
      this.includeDeleted = includeDeleted;
   }

}
