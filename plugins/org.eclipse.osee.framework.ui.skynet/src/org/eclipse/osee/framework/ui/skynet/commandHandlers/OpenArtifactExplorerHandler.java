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

import java.util.Collections;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;

/**
 * @author Roberto E. Escobar
 */
public class OpenArtifactExplorerHandler extends AbstractHandler {

   private List<Branch> getSelectedBranches() {
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         return Handlers.getBranchesFromStructuredSelection(structuredSelection);
      }
      return Collections.emptyList();
   }

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      List<Branch> branches = getSelectedBranches();
      if (!branches.isEmpty()) {
         ArtifactExplorer.exploreBranch(branches.iterator().next());
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      return !getSelectedBranches().isEmpty();
   }
}