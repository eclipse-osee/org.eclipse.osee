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

import java.util.Collection;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class ArchiveBranchHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return !branches.isEmpty() && ServiceUtil.accessControlService().isOseeAdmin();
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (MessageDialog.openConfirm(Displays.getActiveShell(), "Archive Branches",
               "Archive Selected Branches?")) {
               archiveSelectedBranches(selection);
            }
         }

      });
      return null;
   }

   public static void archiveSelectedBranches(IStructuredSelection selection) {
      {
         Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
               Collection<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(selection);

               for (BranchId branch : branches) {
                  BranchArchivedState state = BranchArchivedState.fromBoolean(!BranchManager.isArchived(branch));
                  BranchManager.archiveUnArchiveBranch(branch, state);
                  OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Committed, branch));
               }
            }
         }, "Archive Branch(es)");
         thread.start();
      }
   }
}
