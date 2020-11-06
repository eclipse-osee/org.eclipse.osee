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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author John Misinco
 */
@XmlRootElement
public class BranchQueryData {

   private List<BranchId> branchIds = new ArrayList<>();
   private List<BranchType> branchTypes = new ArrayList<>();
   private List<BranchState> branchStates = new ArrayList<>();
   private boolean includeDeleted;
   private boolean includeArchived;
   private boolean asIds;
   private String nameEquals;
   private String namePattern;
   private String namePatternIgnoreCase;
   private Long isChildOf = -1L;
   private Long isAncestorOf = -1L;

   public List<BranchId> getBranchIds() {
      return branchIds;
   }

   public void setBranchIds(List<BranchId> branchUuids) {
      this.branchIds = branchUuids;
   }

   public List<BranchType> getBranchTypes() {
      return branchTypes;
   }

   public void setBranchTypes(List<BranchType> branchTypes) {
      this.branchTypes = branchTypes;
   }

   public List<BranchState> getBranchStates() {
      return branchStates;
   }

   public void setBranchStates(List<BranchState> branchStates) {
      this.branchStates = branchStates;
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

}
