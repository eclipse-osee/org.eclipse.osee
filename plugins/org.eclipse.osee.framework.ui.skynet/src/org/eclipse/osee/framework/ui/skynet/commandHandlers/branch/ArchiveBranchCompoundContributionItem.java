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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.CompoundContributionProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * @author Jeff C. Phillips
 */
public class ArchiveBranchCompoundContributionItem extends CompoundContributionProvider {
   private ICommandService commandService;

   public ArchiveBranchCompoundContributionItem() {
      this.commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
   }

   public ArchiveBranchCompoundContributionItem(String id) {
      super(id);
   }

   @Override
   protected IContributionItem[] getContributionItems() {
      ISelectionProvider selectionProvider = getSelectionProvider();
      ArrayList<IContributionItem> contributionItems = new ArrayList<>(40);

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<BranchToken> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);

         if (!branches.isEmpty()) {
            BranchToken selectedBranch = branches.iterator().next();
            if (selectedBranch != null) {
               String commandId = "org.eclipse.osee.framework.ui.skynet.branch.BranchView.archiveBranch";
               Command command = commandService.getCommand(commandId);
               CommandContributionItem contributionItem = null;
               boolean archivedState = BranchManager.isArchived(selectedBranch);
               String label = (archivedState ? "Unarchive" : "Archive") + " Branch(s)";
               ImageDescriptor descriptor = archivedState ? ImageManager.getImageDescriptor(
                  FrameworkImage.UN_ARCHIVE) : ImageManager.getImageDescriptor(FrameworkImage.ARCHIVE);
               contributionItem = createCommand(label, commandId, descriptor);

               if (command != null && command.isEnabled()) {
                  contributionItems.add(contributionItem);
               }
            }
         }
      }
      return contributionItems.toArray(new IContributionItem[0]);
   }

   private CommandContributionItem createCommand(String label, String commandId, ImageDescriptor descriptor) {
      CommandContributionItem contributionItem;

      contributionItem = new CommandContributionItem(
         new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), label, commandId,
            Collections.EMPTY_MAP, descriptor, null, null, label, null, null, SWT.NONE, null, false));

      return contributionItem;
   }
}
