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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.CompoundContributionProvider;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * @author Jeff C. Phillips
 */
public class ToggleFavoriteBranchContributionItem extends CompoundContributionProvider {
   private final ICommandService commandService;

   public ToggleFavoriteBranchContributionItem() {
      this.commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
   }

   @Override
   protected IContributionItem[] getContributionItems() {
      ISelectionProvider selectionProvider = getSelectionProvider();
      ArrayList<IContributionItem> contributionItems = new ArrayList<>(40);

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<? extends IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);

         if (!branches.isEmpty()) {
            IOseeBranch selectedBranch = branches.iterator().next();
            if (selectedBranch != null) {
               try {
                  String commandId = ToggleFavoriteBranchHandler.COMMAND_ID;
                  Command command = commandService.getCommand(commandId);
                  CommandContributionItem contributionItem = null;
                  String label =
                     UserManager.getUser().isFavoriteBranch(selectedBranch) ? "Unmark as Favorite" : "Mark as Favorite";
                  contributionItem = createCommand(label, selectedBranch, commandId);

                  if (command != null && command.isEnabled()) {
                     contributionItems.add(contributionItem);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
      return contributionItems.toArray(new IContributionItem[0]);
   }

   private CommandContributionItem createCommand(String label, IOseeBranch branch, String commandId) {
      CommandContributionItem contributionItem;

      contributionItem =
         new CommandContributionItem(new CommandContributionItemParameter(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow(), label, commandId, Collections.EMPTY_MAP, null, null,
            null, label, null, null, SWT.NONE, null, false));

      return contributionItem;
   }

}
