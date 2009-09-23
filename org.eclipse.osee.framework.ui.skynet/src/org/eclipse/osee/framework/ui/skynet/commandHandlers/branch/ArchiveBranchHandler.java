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

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Jeff C. Phillips
 */
public class ArchiveBranchHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) {
         return false;
      }
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      return !branches.isEmpty() && AccessControlManager.isOseeAdmin();
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {

      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      Iterator<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection).iterator();

      Branch branch;

      while ((branch = branches.next()) != null) {

         try {
            BranchArchivedState state = branch.getArchiveState();
            branch.setArchived(!state.isArchived());
            branch.persist();

            OseeEventManager.kickBranchEvent(this, BranchEventType.Committed, branch.getBranchId());
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return null;
   }
}
