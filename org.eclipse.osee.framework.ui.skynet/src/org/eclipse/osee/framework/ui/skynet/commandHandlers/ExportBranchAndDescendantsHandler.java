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

import java.io.File;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Files;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.export.ExportBranchJob;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Paul K. Waldfogel
 */
public class ExportBranchAndDescendantsHandler extends AbstractSelectionChangedHandler {
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   /**
    * @param branchTable
    */
   public ExportBranchAndDescendantsHandler() {
   }

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      List<Branch> mySelectedBranchList = Handlers.getBranchListFromStructuredSelection(myIStructuredSelection);
      IWorkbenchPartSite myIWorkbenchPartSite = Handlers.getIWorkbenchPartSite();
      File file = Files.selectFile(myIWorkbenchPartSite.getShell(), SWT.SAVE, "*.xml");

      if (file != null && mySelectedBranchList.size() == 1) {
         Jobs.startJob(new ExportBranchJob(file, mySelectedBranchList.get(0), false));

      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      return SkynetSelections.oneBranchSelected(myIStructuredSelection) && myAccessControlManager.checkObjectPermission(
            SkynetSelections.boilDownObject(myIStructuredSelection.getFirstElement()), PermissionEnum.READ);
   }

}