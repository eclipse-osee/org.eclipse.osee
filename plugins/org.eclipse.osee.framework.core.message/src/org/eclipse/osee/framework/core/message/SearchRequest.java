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
import java.util.HashSet;

/**
 * @author Roberto E. Escobar
 */
public class SearchRequest {

   private final int branchId;
   private final String rawSearch;
   private boolean isIncludeDeleted;
   private boolean isMatchWordOrder;
   private boolean isCaseSensive;
   private boolean isFindAllLocationsEnabled;
   private final Collection<String> attributeTypeGuids;

   public SearchRequest(int branchId, String rawSearch) {
      super();
      this.attributeTypeGuids = new HashSet<String>();
      this.branchId = branchId;
      this.rawSearch = rawSearch;
   }

   public int getBranchId() {
      return branchId;
   }

   public String getRawSearch() {
      return rawSearch;
   }

   public boolean isIncludeDeleted() {
      return isIncludeDeleted;
   }

   public boolean isMatchWordOrder() {
      return isMatchWordOrder;
   }

   public boolean isCaseSensive() {
      return isCaseSensive;
   }

   public boolean isFindAllLocationsEnabled() {
      return isFindAllLocationsEnabled;
   }

   public Collection<String> getAttributeTypeFilter() {
      return attributeTypeGuids;
   }

   public boolean isAttributeTypeFiltered() {
      return !attributeTypeGuids.isEmpty();
   }
}
