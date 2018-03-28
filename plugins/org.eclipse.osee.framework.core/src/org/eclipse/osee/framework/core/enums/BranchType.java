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

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class BranchType extends NamedIdBase {
   public static final BranchType WORKING = new BranchType(0, "WORKING");
   public static final BranchType BASELINE = new BranchType(2, "BASELINE");
   public static final BranchType MERGE = new BranchType(3, "MERGE");
   public static final BranchType SYSTEM_ROOT = new BranchType(4, "SYSTEM_ROOT");
   public static final BranchType PORT = new BranchType(5, "PORT");
   private static final BranchType[] values = new BranchType[] {WORKING, BASELINE, MERGE, SYSTEM_ROOT, PORT};

   public BranchType(int id, String name) {
      super(Long.valueOf(id), name);
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

   public static BranchType valueOf(int id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchType valueOf(String id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchType[] values() {
      return values;
   }

   public static BranchType fromName(String name) {
      return NamedIdBase.fromName(name, values);
   }
}