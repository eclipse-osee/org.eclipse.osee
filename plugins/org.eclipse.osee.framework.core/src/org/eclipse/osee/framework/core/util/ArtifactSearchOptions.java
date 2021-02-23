/*********************************************************************
 * Copyright (c) 2021 Boeing
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
/**
 * @author Audrey Denk
 */
package org.eclipse.osee.framework.core.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

public class ArtifactSearchOptions {
   private List<ArtifactId> artIds = new ArrayList<>();
   private List<ArtifactTypeToken> artTypeIds = new ArrayList<>();
   private List<AttributeTypeId> attrTypeIds = new ArrayList<>();
   private String searchString;
   private ApplicabilityId applic = ApplicabilityId.SENTINEL;
   private ArtifactId view = ArtifactId.SENTINEL;
   private boolean caseSensitive = false;
   private boolean matchWordOrder = false;
   private boolean exactMatch = false;
   private DeletionFlag includeDeleted = DeletionFlag.EXCLUDE_DELETED;

   public ArtifactSearchOptions() {
   };

   public ArtifactSearchOptions(ArtifactId view, ApplicabilityId applic, List<ArtifactId> artIds, List<ArtifactTypeToken> artTypeIds, List<AttributeTypeId> attrTypeIds, String searchString, boolean caseSensitive, boolean matchWordOrder, boolean exactMatch, DeletionFlag includeDeleted) {
      super();
      this.setView(view);
      this.setApplic(applic);
      this.setArtIds(artIds);
      this.setArtTypeIds(artTypeIds);
      this.setAttrTypeIds(attrTypeIds);
      this.setSearchString(searchString);
      this.setCaseSensitive(caseSensitive);
      this.setExactMatch(exactMatch);
      this.setMatchWordOrder(matchWordOrder);
      this.setIncludeDeleted(includeDeleted);
   }

   public List<ArtifactId> getArtIds() {
      return artIds;
   }

   public List<ArtifactTypeToken> getArtTypeIds() {
      return artTypeIds;
   }

   public List<AttributeTypeId> getAttrTypeIds() {
      return attrTypeIds;
   }

   public String getSearchString() {
      return searchString;
   }

   public ArtifactId getView() {
      return view;
   }

   public void setArtIds(List<ArtifactId> artIds) {
      this.artIds = artIds;
   }

   public void setArtTypeIds(List<ArtifactTypeToken> artTypeIds) {
      this.artTypeIds = artTypeIds;
   }

   public void setAttrTypeIds(List<AttributeTypeId> attrTypeIds) {
      this.attrTypeIds = attrTypeIds;
   }

   public void setSearchString(String searchString) {
      this.searchString = searchString;
   }

   public void setView(ArtifactId view) {
      this.view = view;
   }

   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   public boolean isMatchWordOrder() {
      return matchWordOrder;
   }

   public void setMatchWordOrder(boolean matchWordOrder) {
      this.matchWordOrder = matchWordOrder;
   }

   public boolean isExactMatch() {
      return exactMatch;
   }

   public void setExactMatch(boolean exactMatch) {
      this.exactMatch = exactMatch;
   }

   public ApplicabilityId getApplic() {
      return applic;
   }

   public void setApplic(ApplicabilityId applic) {
      this.applic = applic;
   }

   public DeletionFlag getIncludeDeleted() {
      return includeDeleted;
   }

   public void setIncludeDeleted(DeletionFlag includeDeleted) {
      this.includeDeleted = includeDeleted;
   }

}
