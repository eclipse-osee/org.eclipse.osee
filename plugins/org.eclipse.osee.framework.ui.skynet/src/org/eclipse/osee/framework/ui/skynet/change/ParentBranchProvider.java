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
package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;

public final class ParentBranchProvider implements IBranchProvider {
   private final ChangeUiData uiData;

   public ParentBranchProvider(ChangeUiData uiData) {
      this.uiData = uiData;
   }

   @Override
   public Branch getBranch(IProgressMonitor monitor) throws OseeCoreException {
      Branch selectedBranch = null;
      TransactionDelta txDelta = uiData.getTxDelta();
      Branch childBranch = txDelta.getStartTx().getBranch();
      selectedBranch = childBranch.getParentBranch();
      Conditions.checkNotNull(selectedBranch, "parent branch");
      return selectedBranch;
   }
}