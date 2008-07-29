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
package org.eclipse.osee.framework.ui.skynet.branch;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class BranchCreationHandler extends AbstractSelectionEnabledHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchCreationHandler.class);
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private TreeViewer branchTable;
   private boolean selective;

   /**
    * @param branchTable
    */
   public BranchCreationHandler(MenuManager menuManager, TreeViewer branchTable, boolean selective) {
      super(menuManager);
      this.branchTable = branchTable;
      this.selective = selective;
   }

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      Object backingData = ((JobbedNode) selection.getFirstElement()).getBackingData();

      final TransactionId parentTransactionId;
      try {
         if (backingData instanceof Branch) {
            Branch branch = (Branch) backingData;
            parentTransactionId = transactionIdManager.getEditableTransactionId(branch);
         } else if (backingData instanceof TransactionData) {

            parentTransactionId = ((TransactionData) backingData).getTransactionId();

         } else {
            throw new IllegalStateException(
                  "Backing data for the jobbed node in the branchview was not of the expected type");
         }
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return null;
      } catch (OseeCoreException ex) {
         OSEELog.logException(getClass(), ex, true);
         return null;
      }
      final EntryDialog dialog =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Branch", null, "Enter the name of the new Branch",
                  MessageDialog.INFORMATION, new String[] {"OK", "Cancel"}, 0);
      int result = dialog.open();

      if (result == 0 && dialog.getEntry() != null) {

         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            public void run(IProgressMonitor monitor) throws Exception {
               if (selective) {
                  Set<String> allArtifactTypes = new HashSet<String>();
                  for (ArtifactType artifactType : ConfigurationPersistenceManager.getValidArtifactTypes(parentTransactionId.getBranch())) {
                     allArtifactTypes.add(artifactType.getName());
                  }

                  // Compress all but software requirements
                  Set<String> compressTypes = new HashSet<String>();
                  compressTypes.addAll(allArtifactTypes);
                  compressTypes.remove(Requirements.SOFTWARE_REQUIREMENT);

                  // Preserve software reqts
                  String[] preserveTypes = new String[] {Requirements.SOFTWARE_REQUIREMENT};
                  BranchPersistenceManager.createBranchWithFiltering(parentTransactionId, null, dialog.getEntry(),
                        null, compressTypes.toArray(new String[compressTypes.size()]), preserveTypes);
               } else {
                  BranchPersistenceManager.createWorkingBranch(parentTransactionId, null, dialog.getEntry(), null);
               }
            }
         };

         Jobs.run("Create Branch", runnable, logger, SkynetGuiPlugin.PLUGIN_ID);
      }

      return null;
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

      try {
         return (!selective || OseeProperties.isDeveloper()) && ((SkynetSelections.oneBranchSelected(selection) && AccessControlManager.checkObjectPermission(
               SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.READ)) || (SkynetSelections.oneTransactionSelected(selection) && AccessControlManager.checkObjectPermission(
               ((TransactionData) SkynetSelections.boilDownObject(selection.getFirstElement())).getTransactionId().getBranch(),
               PermissionEnum.READ)));
      } catch (SQLException ex) {
         return false;
      } catch (OseeCoreException ex) {
         return false;
      }
   }
}