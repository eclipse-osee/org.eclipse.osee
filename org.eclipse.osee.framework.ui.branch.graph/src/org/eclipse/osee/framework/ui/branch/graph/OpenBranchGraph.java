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
package org.eclipse.osee.framework.ui.branch.graph;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditor;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditorInput;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractSelectionChangedHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class OpenBranchGraph extends AbstractSelectionChangedHandler {

   public OpenBranchGraph() {
      super();
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
         if (selection instanceof IStructuredSelection) {
            List<Branch> branches = Handlers.getBranchesFromStructuredSelection((IStructuredSelection) selection);
            if (!branches.isEmpty()) {
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                     new BranchGraphEditorInput(branches.iterator().next()), BranchGraphEditor.EDITOR_ID);
            }
         }
      } catch (Exception ex) {
         throw new ExecutionException("Error opening Branch Graph Editor", ex);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}
