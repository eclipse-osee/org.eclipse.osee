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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import java.util.ArrayList;
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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.CompoundContributionProvider;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.merge.BranchIdParameter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * @author Jeff C. Phillips
 */
public class CommitIntoParentCompoundContributionItem extends CompoundContributionProvider {
   private static final IParameter[] BRANCH_COMMIT_PARAMETER_DEF =
      new IParameter[] {new BranchIdParameter(), new CommitBranchParameter()};

   private ICommandService commandService;

   public CommitIntoParentCompoundContributionItem() {
      this.commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
   }

   public CommitIntoParentCompoundContributionItem(String id) {
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
            IOseeBranch selectedBranch = branches.iterator().next();

            if (selectedBranch != null && selectedBranch.notEqual(CoreBranches.SYSTEM_ROOT)) {
               try {
                  String commandId = "org.eclipse.osee.framework.ui.skynet.branch.BranchView.commitIntoParent";
                  Command command = configCommandParameter(commandId);
                  CommandContributionItem contributionItem = null;

                  contributionItem = createCommand(selectedBranch, commandId);

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

   private CommandContributionItem createCommand(IOseeBranch branch, String commandId) throws OseeCoreException {

      Map<String, String> parameters = new HashMap<>();
      parameters.put(BranchView.BRANCH_ID, branch.getIdString());
      parameters.put(CommitBranchParameter.ARCHIVE_PARENT_BRANCH, "true");
      CommandContributionItem contributionItem;
      String label = "Commit into Parent Branch: " + BranchManager.getBranchName(BranchManager.getParentBranch(branch));

      contributionItem = new CommandContributionItem(
         new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), label, commandId,
            parameters, null, null, null, label, null, null, SWT.NONE, null, false));

      return contributionItem;
   }

   private Command configCommandParameter(String commandId) {
      Command command = commandService.getCommand(commandId);

      try {
         command.define(command.getName(), "", commandService.getCategory("org.eclipse.debug.ui.category.run"),
            BRANCH_COMMIT_PARAMETER_DEF);
      } catch (NotDefinedException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return command;
   }
}
