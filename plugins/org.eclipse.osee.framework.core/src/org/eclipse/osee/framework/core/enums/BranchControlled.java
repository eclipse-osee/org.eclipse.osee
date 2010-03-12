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

/**
 * @author Ryan D. Brooks
 */
public enum BranchControlled {
   CHANGE_MANAGED, NOT_CHANGE_MANAGED, ALL;

   public boolean matches(BranchControlled branchControlled) {
      return branchControlled == BranchControlled.ALL || this == branchControlled;
   }

   public boolean isChangeManaged() {
      return this == BranchControlled.CHANGE_MANAGED;
   }

   public static BranchControlled fromBoolean(boolean isChangeManaged) {
      return isChangeManaged ? CHANGE_MANAGED : NOT_CHANGE_MANAGED;
   }
}
