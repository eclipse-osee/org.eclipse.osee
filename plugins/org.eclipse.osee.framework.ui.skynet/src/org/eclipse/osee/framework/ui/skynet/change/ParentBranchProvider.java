/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;

public final class ParentBranchProvider implements IBranchProvider {
   private final ChangeUiData uiData;

   public ParentBranchProvider(ChangeUiData uiData) {
      this.uiData = uiData;
   }

   @Override
   public BranchId getBranch() {
      BranchId childBranch = uiData.getTxDelta().getStartTx().getBranch();
      return BranchManager.getParentBranch(childBranch);
   }
}