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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public enum BranchType {
   WORKING(0),
   BASELINE(2),
   MERGE(3),
   SYSTEM_ROOT(4),
   PORT(5);

   private final int value;

   BranchType(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public boolean isBaselineBranch() {
      return this == BranchType.BASELINE;
   }

   public boolean isSystemRootBranch() {
      return this == BranchType.SYSTEM_ROOT;
   }

   public boolean isMergeBranch() {
      return this == BranchType.MERGE;
   }

   public boolean isWorkingBranch() {
      return this == BranchType.WORKING;
   }

   public boolean isPortBranch() {
      return this == BranchType.PORT;
   }

   public boolean isOfType(BranchType... branchTypes) {
      for (BranchType branchType : branchTypes) {
         if (this == branchType) {
            return true;
         }
      }
      return false;
   }

   public static BranchType valueOf(int value) {
      for (BranchType type : values()) {
         if (type.getValue() == value) {
            return type;
         }
      }
      throw new OseeArgumentException("No branch type with value [%s]", value);
   }
}
