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

import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Roberto E. Escobar
 */
public class SearchRequest {

   private final IOseeBranch branch;
   private final String rawSearch;
   private final SearchOptions options;

   public SearchRequest(IOseeBranch branch, String rawSearch) {
      this(branch, rawSearch, null);
   }

   public SearchRequest(IOseeBranch branch, String rawSearch, SearchOptions options) {
      super();
      this.branch = branch;
      this.rawSearch = rawSearch;
      this.options = options != null ? options : new SearchOptions();
   }

   public IOseeBranch getBranch() {
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
