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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;

/**
 * @author Roberto E. Escobar
 */
public class OpenArtifactExplorerHandler extends CommandHandler {

   private List<Branch> getSelectedBranches(IStructuredSelection selection) {
      return Handlers.getBranchesFromStructuredSelection(selection);
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<Branch> selectedBranches = getSelectedBranches(structuredSelection);
      boolean isEnabled = !selectedBranches.isEmpty();
      for (Branch branch : selectedBranches) {
         if (BranchManager.getType(branch).isMergeBranch() || !branch.getBranchState().matches(BranchState.CREATED,
            BranchState.MODIFIED)) {
            isEnabled = false;
            break;
         }
      }
      return isEnabled;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      List<? extends BranchId> branches = getSelectedBranches(selection);
      if (!branches.isEmpty()) {
         ArtifactExplorer.exploreBranch(branches.iterator().next());
      }
      return null;
   }
}