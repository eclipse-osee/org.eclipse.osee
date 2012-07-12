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
package org.eclipse.osee.display.api.data;

/**
 * @author John R. Misinco
 */
public class ViewSearchParameters {

   private final String searchString;
   private final Boolean nameOnly;

   public ViewSearchParameters(String searchString, Boolean nameOnly) {
      this.searchString = searchString;
      this.nameOnly = nameOnly;
   }

   public String getSearchString() {
      return searchString;
   }

   public Boolean isNameOnly() {
      return nameOnly;
   }

}
