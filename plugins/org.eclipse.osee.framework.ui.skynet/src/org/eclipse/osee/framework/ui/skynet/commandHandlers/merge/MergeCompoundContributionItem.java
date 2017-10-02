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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.ui.plugin.util.CompoundContributionProvider;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * Dynamically provides a list of merge branches based on a selected branch
 *
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class MergeCompoundContributionItem extends CompoundContributionProvider {
   private static final IParameter[] BRANCH_PARAMETER_DEF = new IParameter[] {new BranchIdParameter()};
   private ICommandService commandService;

   public MergeCompoundContributionItem() {
      this.commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
   }

   public MergeCompoundContributionItem(String id) {
      super(id);
   }

   @Override
   protected IContributionItem[] getContributionItems() {
      ISelectionProvider selectionProvider = getSelectionProvider();
      ArrayList<IContributionItem> contributionItems = new ArrayList<>(40);

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);

         if (!branches.isEmpty()) {
            BranchId selectedBranch = branches.iterator().next();
            if (selectedBranch != null) {
               try {
                  Collection<Long> destBranches = ConflictManagerInternal.getDestinationBranchesMerged(selectedBranch);
                  BranchId parentBranch = BranchManager.getParentBranch(selectedBranch);
                  if (parentBranch != null && !BranchManager.getType(
                     selectedBranch).isMergeBranch() && !destBranches.contains(parentBranch.getUuid())) {
                     destBranches.add(parentBranch.getUuid());
                  }

                  String commandId = "org.eclipse.osee.framework.ui.skynet.branch.BranchView.mergeManager";
                  Command command = configCommandParameter(commandId);
                  CommandContributionItem contributionItem = null;

                  for (Long branchUuid : destBranches) {
                     contributionItem = createCommand(branchUuid, commandId);

                     if (command != null && command.isEnabled()) {
                        contributionItems.add(contributionItem);
                     }
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
      return contributionItems.toArray(new IContributionItem[0]);
   }

   private CommandContributionItem createCommand(Long branchUuid, String commandId)  {
      Map<String, String> parameters = new HashMap<>();
      parameters.put(BranchView.BRANCH_ID, Long.toString(branchUuid));
      CommandContributionItem contributionItem;
      String label =
         branchUuid.equals(0L) ? "Can't Merge a Root Branch" : BranchManager.getBranchToken(branchUuid).getName();

      contributionItem = new CommandContributionItem(
         new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), label, commandId,
            parameters, null, null, null, label, null, null, SWT.NONE, null, false));

      return contributionItem;
   }

   private Command configCommandParameter(String commandId) {
      Command command = commandService.getCommand(commandId);

      try {
         command.define(command.getName(), "", commandService.getCategory("org.eclipse.debug.ui.category.run"),
            BRANCH_PARAMETER_DEF);
      } catch (NotDefinedException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return command;
   }
}
