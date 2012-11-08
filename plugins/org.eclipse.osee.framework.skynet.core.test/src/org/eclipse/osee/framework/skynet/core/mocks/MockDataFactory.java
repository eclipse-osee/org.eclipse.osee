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
package org.eclipse.osee.framework.skynet.core.mocks;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {

   private MockDataFactory() {
      // Utility Class
   }

   public static Branch createBranch(int index) {
      BranchState branchState = BranchState.values()[Math.abs(index % BranchState.values().length)];
      BranchType branchType = BranchType.values()[Math.abs(index % BranchType.values().length)];
      boolean isArchived = index % 2 == 0 ? true : false;
      return new Branch(GUID.create(), "branch_" + index, branchType, branchState, isArchived);
   }

}
