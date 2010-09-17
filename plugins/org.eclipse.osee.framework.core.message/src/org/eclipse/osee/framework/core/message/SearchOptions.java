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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Roberto E. Escobar
 */
public class SearchOptions {

   private final Collection<IAttributeType> attributeTypeGuids = new HashSet<IAttributeType>();
   private DeletionFlag deletionFlag;
   private boolean isMatchWordOrder;
   private boolean isCaseSensive;
   private boolean isFindAllLocationsEnabled;

   public SearchOptions() {
      super();
      deletionFlag = DeletionFlag.EXCLUDE_DELETED;
   }

   public DeletionFlag getDeletionFlag() {
      return deletionFlag;
   }

   public boolean isMatchWordOrder() {
      return isMatchWordOrder;
   }

   public boolean isCaseSensitive() {
      return isCaseSensive;
   }

   public boolean isFindAllLocationsEnabled() {
      return isFindAllLocationsEnabled;
   }

   public Collection<IAttributeType> getAttributeTypeFilter() {
      return attributeTypeGuids;
   }

   public void setAttributeTypeFilter(IAttributeType... typeFilter) {
      for (IAttributeType attributeType : typeFilter) {
         addAttributeTypeFilter(attributeType);
      }
   }

   public void clearTypeFilter() {
      attributeTypeGuids.clear();
   }

   public void addAttributeTypeFilter(IAttributeType type) {
      attributeTypeGuids.add(type);
   }

   public boolean isAttributeTypeFiltered() {
      return !attributeTypeGuids.isEmpty();
   }

   public void setDeletedIncluded(DeletionFlag deletionFlag) {
      this.deletionFlag = deletionFlag;
   }

   public void setMatchWordOrder(boolean isMatchWordOrder) {
      this.isMatchWordOrder = isMatchWordOrder;
   }

   public void setCaseSensive(boolean isCaseSensive) {
      this.isCaseSensive = isCaseSensive;
   }

   public void setFindAllLocationsEnabled(boolean isFindAllLocationsEnabled) {
      this.isFindAllLocationsEnabled = isFindAllLocationsEnabled;
   }

   @Override
   public String toString() {
      return "SearchOptions [attributeTypeGuids=" + attributeTypeGuids + ", isIncludeDeleted=" + getDeletionFlag() + ", isMatchWordOrder=" + isMatchWordOrder + ", isCaseSensive=" + isCaseSensive + ", isFindAllLocationsEnabled=" + isFindAllLocationsEnabled + "]";
   }

}
