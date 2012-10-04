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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;

/**
 * @author Roberto E. Escobar
 */
public class OpenArtifactExplorerHandler extends CommandHandler {

   private List<? extends IOseeBranch> getSelectedBranches(IStructuredSelection selection) {
      return Handlers.getBranchesFromStructuredSelection(selection);
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      return !getSelectedBranches(structuredSelection).isEmpty();
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      List<? extends IOseeBranch> branches = getSelectedBranches(selection);
      if (!branches.isEmpty()) {
         ArtifactExplorer.exploreBranch(branches.iterator().next());
      }
      return null;
   }
}