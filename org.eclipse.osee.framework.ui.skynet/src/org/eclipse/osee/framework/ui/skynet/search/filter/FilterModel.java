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
package org.eclipse.osee.framework.ui.skynet.search.filter;

import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;

public class FilterModel {

   private String searchType;
   private String type;
   private String value;
   private ISearchPrimitive searchPrimitive;

   public FilterModel(ISearchPrimitive searchPrimitive, String search, String type, String value) {
      this.searchPrimitive = searchPrimitive;
      this.searchType = search;
      this.type = type;
      this.value = value;
   }

   /**
    * @return Returns the name.
    */
   public String getType() {
      return type;
   }

   /**
    * @param type - The type to set.
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * @return Returns the function.
    */
   public String getValue() {
      return value;
   }

   /**
    * @param function The function to set.
    */
   public void setvalue(String function) {
      this.value = function;
   }

   /**
    * @return Returns the searchType.
    */
   public ISearchPrimitive getSearchPrimitive() {
      return searchPrimitive;
   }

   /**
    * @return Returns the searchType.
    */
   public String getSearch() {
      return searchType;
   }

   /**
    * @param searchType The searchType to set.
    */
   public void setSearch(String searchType) {
      this.searchType = searchType;
   }

   public void setSearchPrimitive(ISearchPrimitive searchPrimitive) {
      this.searchPrimitive = searchPrimitive;
   }

   @Override
   public String toString() {
      String toReturn = searchPrimitive.toString();
      if (searchPrimitive instanceof InRelationSearch) {
         toReturn += " " + value;
      }
      return toReturn;
   }
}
