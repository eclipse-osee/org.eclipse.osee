/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author John Misinco
 */
@XmlRootElement
public class BranchQueryData {

   private final Set<BranchId> branchIds = new HashSet<>();
   private final Set<BranchType> branchTypes = new HashSet<>();
   private final Set<BranchState> branchStates = new HashSet<>();
   private boolean includeDeleted;
   private boolean includeArchived;
   private boolean asIds;
   private String nameEquals;
   private String namePattern;
   private String namePatternIgnoreCase;
   private Long isChildOf = -1L;
   private Long isAncestorOf = -1L;
   private BranchCategoryToken category = BranchCategoryToken.SENTINEL;

   public Collection<BranchId> getBranchIds() {
      return branchIds;
   }

   public void setBranchIds(Collection<BranchId> branchUuids) {
      this.branchIds.clear();
      this.branchIds.addAll(branchUuids);
   }

   public Collection<BranchType> getBranchTypes() {
      return branchTypes;
   }

   public void setBranchTypes(Collection<BranchType> branchTypes) {
      this.branchTypes.clear();
      this.branchTypes.addAll(branchTypes);
   }

   public Collection<BranchState> getBranchStates() {
      return branchStates;
   }

   public void setBranchStates(Collection<BranchState> branchStates) {
      this.branchStates.clear();
      this.branchStates.addAll(branchStates);
   }

   public boolean isIncludeDeleted() {
      return includeDeleted;
   }

   public void setIncludeDeleted(boolean includeDeleted) {
      this.includeDeleted = includeDeleted;
   }

   public boolean isIncludeArchived() {
      return includeArchived;
   }

   public void setIncludeArchived(boolean includeArchived) {
      this.includeArchived = includeArchived;
   }

   public String getNameEquals() {
      return nameEquals;
   }

   public void setNameEquals(String nameEquals) {
      this.nameEquals = nameEquals;
   }

   public String getNamePattern() {
      return namePattern;
   }

   public void setNamePattern(String namePattern) {
      this.namePattern = namePattern;
   }

   public void setIsChildOf(Long isChildOf) {
      this.isChildOf = isChildOf;
   }

   public Long getIsChildOf() {
      return isChildOf != null ? isChildOf : -1L;
   }

   public void setIsAncestorOf(Long isAncestorOf) {
      this.isAncestorOf = isAncestorOf;
   }

   public Long getIsAncestorOf() {
      return isAncestorOf != null ? isAncestorOf : -1L;
   }

   public String getNamePatternIgnoreCase() {
      return namePatternIgnoreCase;
   }

   public void setNamePatternIgnoreCase(String namePatternIgnoreCase) {
      this.namePatternIgnoreCase = namePatternIgnoreCase;
   }

   public boolean isAsIds() {
      return asIds;
   }

   public void setAsIds(boolean asIds) {
      this.asIds = asIds;
   }

   public String getHtml() {
      return AHTML.simplePage(
         "BranchQueryData <br/>branchIds=" + branchIds + "<br/>branchTypes=" + branchTypes + "<br/>branchStates=" //
            + branchStates + "<br/>includeDeleted=" + includeDeleted + "<br/>includeArchived=" //
            + includeArchived + "<br/>asIds=" + asIds + "<br/>nameEquals=" + nameEquals + //
            "<br/>namePattern=" + namePattern + "<br/>namePatternIgnoreCase=" + namePatternIgnoreCase //
            + "<br/>isChildOf=" + isChildOf + "<br/>isAncestorOf=" + isAncestorOf + "<br/>category=" + category);
   }

   public BranchCategoryToken getCategory() {
      return category.isValid() ? category : BranchCategoryToken.SENTINEL;
   }

   public void setCategory(BranchCategoryToken category) {
      this.category = category;
   }

}
