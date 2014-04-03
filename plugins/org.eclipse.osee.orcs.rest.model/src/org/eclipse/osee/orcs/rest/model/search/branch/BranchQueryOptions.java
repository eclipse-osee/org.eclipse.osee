/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.search.branch;

import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author John Misinco
 */
public class BranchQueryOptions {

   private List<Integer> branchIds;
   private List<BranchType> branchTypes;
   private List<BranchState> branchStates;
   private boolean includeDeleted;
   private boolean includeArchived;
   private String nameEquals;
   private String namePattern;
   private Long isChildOf = -1L;
   private Long isAncestorOf = -1L;

   public List<Integer> getBranchIds() {
      return branchIds;
   }

   public void setBranchIds(List<Integer> branchIds) {
      this.branchIds = branchIds;
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
      return isChildOf;
   }

   public void setIsAncestorOf(Long isAncestorOf) {
      this.isAncestorOf = isAncestorOf;
   }

   public Long getIsAncestorOf() {
      return isAncestorOf;
   }

}
