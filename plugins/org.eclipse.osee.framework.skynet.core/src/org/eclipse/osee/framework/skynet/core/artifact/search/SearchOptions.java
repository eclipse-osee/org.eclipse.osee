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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Roberto E. Escobar
 */
public class SearchOptions {

   private final Collection<AttributeTypeId> attributeTypes = new HashSet<>();
   private final Collection<ArtifactTypeToken> artifactTypeGuids = new HashSet<>();
   private DeletionFlag deletionFlag;
   private boolean isMatchWordOrder;
   private boolean isCaseSensive;
   private boolean isSearchAll;
   private boolean isExactMatch;

   public SearchOptions() {
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

   public boolean isSearchAll() {
      return isSearchAll;
   }

   public boolean isExactMatch() {
      return isExactMatch;
   }

   public Collection<AttributeTypeId> getAttributeTypeFilter() {
      return attributeTypes;
   }

   public void setAttributeTypeFilter(AttributeTypeId[] typeFilter) {
      attributeTypes.addAll(Arrays.asList(typeFilter));
   }

   public void clearTypeFilter() {
      attributeTypes.clear();
   }

   public void addAttributeTypeFilter(AttributeTypeId type) {
      attributeTypes.add(type);
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

   public void setIsSearchAll(boolean isSearchAll) {
      this.isSearchAll = isSearchAll;
   }

   public void setExactMatch(boolean exactMatchEnabled) {
      this.isExactMatch = exactMatchEnabled;
   }

   @Override
   public String toString() {
      return "SearchOptions [attributeTypeGuids=" + attributeTypes + ", artifactTypeGuids=" + artifactTypeGuids + ", isIncludeDeleted=" + getDeletionFlag() + ", isMatchWordOrder=" + isMatchWordOrder + ", isCaseSensive=" + isCaseSensive + ", isExactMatch=" + isExactMatch + "]";
   }

   public void setArtifactTypeFilter(ArtifactTypeToken[] artifactTypeFilter) {
      for (ArtifactTypeToken type : artifactTypeFilter) {
         artifactTypeGuids.add(type);
      }
   }

   public Collection<ArtifactTypeToken> getArtifactTypeFilter() {
      return artifactTypeGuids;
   }

}
