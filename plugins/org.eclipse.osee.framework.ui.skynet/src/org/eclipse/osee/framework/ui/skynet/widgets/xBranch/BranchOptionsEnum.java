/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

public enum BranchOptionsEnum {
   //TODO: get rid of the cap constants and replace with their origKeyNames
   // then there will be no need for fromInitValue
   FLAT_KEY("flat"),
   HIERARCHY_KEY("hierarchy"),
   FAVORITE_KEY("favorites_first"),
   SHOW_MERGE_BRANCHES("show_merge_branches"),
   SHOW_ARCHIVED_BRANCHES("show_archived_branches"),
   BRANCH_ID("branchUuid"),
   SHOW_WORKING_BRANCHES_ONLY("show_working_branches");

   public String origKeyName;

   private BranchOptionsEnum(String keyName) {
      origKeyName = keyName;
   }

   public static BranchOptionsEnum fromInitValue(String initKey) {
      //at worst O(n^2), but n will always remain small.
      for (BranchOptionsEnum optEnum : values()) {
         if (optEnum.origKeyName.equals(initKey)) {
            return optEnum;
         }
      }
      throw new IllegalArgumentException(String.format("Incorrect BranchOptionEnum value: " + initKey));
   }
}
