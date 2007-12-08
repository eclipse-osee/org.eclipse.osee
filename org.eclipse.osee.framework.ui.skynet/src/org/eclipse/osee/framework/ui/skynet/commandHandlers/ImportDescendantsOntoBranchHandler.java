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
import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.Files;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.export.ImportBranchJob;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Paul K. Waldfogel
 */
public class ImportDescendantsOntoBranchHandler extends AbstractSelectionHandler {
   // private static final Logger logger =
   // ConfigUtil.getConfigFactory().getLogger(ImportOntoBranchHandler.class);
   private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   // private static final BranchPersistenceManager branchManager =
   // BranchPersistenceManager.getInstance();
   // private static final TransactionIdManager transactionIdManager =
   // TransactionIdManager.getInstance();
   private TreeViewer branchTable;
   private boolean selective;

   /**
    * @param branchTable
    */
   public ImportDescendantsOntoBranchHandler() {
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
      File file = Files.selectFile(myIWorkbenchPartSite.getShell(), SWT.OPEN, "*.xml");
      if (file != null && mySelectedBranchList.size() == 1) {
         Jobs.startJob(new ImportBranchJob(file, mySelectedBranchList.get(0), true, true));
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

      try {
         return (!selective || OseeProperties.getInstance().isDeveloper()) && ((SkynetSelections.oneBranchSelected(selection) && accessManager.checkObjectPermission(
               SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.READ)) || (SkynetSelections.oneTransactionSelected(selection) && accessManager.checkObjectPermission(
               ((TransactionData) SkynetSelections.boilDownObject(selection.getFirstElement())).getTransactionId().getBranch(),
               PermissionEnum.READ)));
      } catch (SQLException ex) {
         return false;
      }
   }
}