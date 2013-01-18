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
public class SearchParameters {

   private String branchUuid;
   private String alt;
   private String fields;
   private int fromTx;
   private boolean includeTypeInh;
   private boolean includeCache;
   private boolean includeDeleted;

   @XmlTransient
   private List<Predicate> predicates;

   public SearchParameters() {
      super();
   }

   public SearchParameters(String branchUuid, List<Predicate> predicates, String alt, String fields, int fromTx, boolean includeTypeInheritance, boolean includeCache, boolean includeDeleted) {
      super();
      this.branchUuid = branchUuid;
      this.predicates = predicates;
      this.alt = alt;
      this.fields = fields;
      this.fromTx = fromTx;
      this.includeTypeInh = includeTypeInheritance;
      this.includeCache = includeCache;
      this.includeDeleted = includeDeleted;
   }

   public String getBranchUuid() {
      return branchUuid;
   }

   @XmlElementWrapper(name = "predicates")
   @XmlElement(name = "predicate")
   public List<Predicate> getPredicates() {
      return predicates;
   }

   public String getAlt() {
      return alt;
   }

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

   public boolean isIncludeTypeInheritance() {
      return includeTypeInh;
   }

   public boolean isIncludeCache() {
      return includeCache;
   }

   public boolean isIncludeDeleted() {
      return includeDeleted;
   }

   public int getFromTx() {
      return fromTx;
   }

   public void setFromTx(int fromTx) {
      this.fromTx = fromTx;
   }

   public boolean isIncludeTypeInh() {
      return includeTypeInh;
   }

   public void setIncludeTypeInh(boolean includeTypeInh) {
      this.includeTypeInh = includeTypeInh;
   }

   public void setIncludeCache(boolean includeCache) {
      this.includeCache = includeCache;
   }

   public void setIncludeDeleted(boolean includeDeleted) {
      this.includeDeleted = includeDeleted;
   }

}
