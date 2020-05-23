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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;

/**
 * @author Roberto E. Escobar
 */
public class SearchRequest implements HasBranch {

   private final BranchId branch;
   private final String rawSearch;
   private final SearchOptions options;

   public SearchRequest(BranchId branch, String rawSearch, SearchOptions options) {
      this.branch = branch;
      this.rawSearch = rawSearch;
      this.options = options != null ? options : new SearchOptions();
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public String getRawSearch() {
      return rawSearch;
   }

   public SearchOptions getOptions() {
      return options;
   }

   @Override
   public String toString() {
      return "SearchRequest [branch=" + branch + ", rawSearch=" + rawSearch + "," + options + "]";
   }
}
