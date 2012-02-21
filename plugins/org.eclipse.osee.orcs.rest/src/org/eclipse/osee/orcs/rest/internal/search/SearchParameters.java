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

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class SearchParameters {

   private String branchUuid;
   private String query;
   private String alt;
   private String fields;

   public SearchParameters() {

   }

   public SearchParameters(String uuid, String query, String alt, String fields) {
      this.branchUuid = uuid;
      this.query = query;
      this.alt = alt;
      this.fields = fields;
   }

   public String getBranchUuid() {
      return branchUuid;
   }

   public String getQuery() {
      return query;
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

   public void setQuery(String query) {
      this.query = query;
   }

   public void setAlt(String alt) {
      this.alt = alt;
   }

   public void setFields(String fields) {
      this.fields = fields;
   }

}
