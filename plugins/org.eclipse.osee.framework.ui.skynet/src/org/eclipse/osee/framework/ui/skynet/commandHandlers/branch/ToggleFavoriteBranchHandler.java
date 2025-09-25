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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Jeff C. Phillips
 */
public class ToggleFavoriteBranchHandler extends CommandHandler {
   public static final String COMMAND_ID =
      "org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ToggleFavoriteBranchHandler";

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      BranchId selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      Artifact user = OseeApiService.userArt();
      // Make sure we have latest artifact
      user.reloadAttributesAndRelations();
      OseeApiService.branchSvc().toggleFavoriteBranch(selectedBranch);
      OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.FavoritesUpdated, selectedBranch));

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1;
   }
}