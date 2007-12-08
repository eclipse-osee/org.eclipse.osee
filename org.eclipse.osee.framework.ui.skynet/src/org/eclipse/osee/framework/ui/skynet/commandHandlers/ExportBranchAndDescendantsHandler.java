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
public class ExportBranchAndDescendantsHandler extends AbstractSelectionHandler {
   // private static final Logger logger =
   // ConfigUtil.getConfigFactory().getLogger(ImportOntoBranchHandler.class);
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   // private static final BranchPersistenceManager branchManager =
   // BranchPersistenceManager.getInstance();
   // private static final TransactionIdManager transactionIdManager =
   // TransactionIdManager.getInstance();
   // private TreeViewer branchTable;
   // private boolean selective;

   /**
    * @param branchTable
    */
   public ExportBranchAndDescendantsHandler() {
      super(new String[] {"Branch"});
      // this.branchTable = branchTable;
      // this.selective = selective;
   }

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      // IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      // Object backingData = ((JobbedNode) selection.getFirstElement()).getBackingData();
      List<Branch> mySelectedBranchList = super.getBranchList();
      IWorkbenchPartSite myIWorkbenchPartSite = super.getIWorkbenchPartSite();
      File file = Files.selectFile(myIWorkbenchPartSite.getShell(), SWT.SAVE, "*.xml");

      if (file != null && mySelectedBranchList.size() == 1) {
         Jobs.startJob(new ExportBranchJob(file, mySelectedBranchList.get(0), false));

      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      // IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      IStructuredSelection selection = super.getIStructuredSelection();
      return SkynetSelections.oneBranchSelected(selection) && myAccessControlManager.checkObjectPermission(
            SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.READ);
   }

}