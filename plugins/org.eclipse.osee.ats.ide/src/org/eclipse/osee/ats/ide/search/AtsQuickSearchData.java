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
package org.eclipse.osee.ats.ide.search;

/**
 * @author Donald G. Dunne
 */
class AtsQuickSearchData {
   private final String searchStr;
   private boolean includeCompleteCancelled = false;
   private final String name;

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

   @Override
   public String toString() {
      return String.format("%s - [%s]%s", name, searchStr,
         includeCompleteCancelled ? " - Include Completed/Cancelled" : "");
   }
}
