/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchData {
   private final String searchStr;
   private boolean includeCompleteCancelled = false;
   private final String name;
   private boolean includeDeleted = false;
   private boolean caseSensitive = false;
   private boolean useEclipseSearchView = false;

   public AtsQuickSearchData(String name, String searchStr, boolean includeCompleteCancelled) {
      this.name = name;
      this.searchStr = searchStr;
      this.includeCompleteCancelled = includeCompleteCancelled;
   }

   public boolean isIncludeCompleteCancelled() {
      return includeCompleteCancelled;
   }

   public String getSearchStr() {
      return searchStr;
   }

   public boolean isIncludeDeleted() {
      return includeDeleted;
   }

   public void setIncludeDeleted(boolean includeDeleted) {
      this.includeDeleted = includeDeleted;
   }

   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   public boolean isUseEclipseSearchView() {
      return useEclipseSearchView;
   }

   public void setUseEclipseSearchView(boolean useEclipseSearchView) {
      this.useEclipseSearchView = useEclipseSearchView;
   }

   public BranchId getBranch() {
      return AtsApiService.get().getAtsBranch();
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return String.format("%s - [%s]%s", name, searchStr,
         includeCompleteCancelled ? " - Include Completed/Cancelled" : "");
   }
}
