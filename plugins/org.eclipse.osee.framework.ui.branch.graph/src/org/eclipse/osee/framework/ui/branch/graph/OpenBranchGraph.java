/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.branch.graph;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditor;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditorInput;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class OpenBranchGraph extends CommandHandler {

   public OpenBranchGraph() {
      super();
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      return true;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      try {
         List<BranchToken> branches = Handlers.getBranchesFromStructuredSelection(selection);
         if (!branches.isEmpty()) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
               new BranchGraphEditorInput(branches.iterator().next()), BranchGraphEditor.EDITOR_ID);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return null;
   }
}
