/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author John R. Misinco
 */
public class SearchParameters {
   private final IOseeBranch branch;
   private final boolean nameOnly;
   private final String searchPhrase;

   public SearchParameters(IOseeBranch branch, boolean nameOnly, String searchPhrase) {
      this.branch = branch;
      this.nameOnly = nameOnly;
      this.searchPhrase = searchPhrase;
   }

   public IOseeBranch getBranch() {
      return branch;
   }

   public boolean isNameOnly() {
      return nameOnly;
   }

   public String getSearchPhrase() {
      return searchPhrase;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof SearchParameters) {
         SearchParameters sObj = (SearchParameters) obj;
         return sObj.getBranch().getGuid().equals(branch.getUuid()) && sObj.isNameOnly() == nameOnly && sObj.getSearchPhrase().equals(
            searchPhrase);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}