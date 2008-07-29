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
package org.eclipse.osee.framework.ui.skynet.dialogs;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public class BranchSelectionDialog extends ElementTreeSelectionDialog {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchSelectionDialog.class);
   private static final String TITLE_MESSAGE = "Select a %sBranch";
   private static final String BRANCH_ERROR_MESSAGE = "Must select a %sbranch.";

   private boolean allowOnlyWorkingBranches;

   private BranchSelectionDialog(Shell parent, boolean allowOnlyWorkingBranches) {
      super(parent, new SimpleBranchLabelProvider(), new SimpleBranchContentProvider());
      this.allowOnlyWorkingBranches = allowOnlyWorkingBranches;
      initialize();
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle(getTitleAndMessage());
      setMessage(getTitleAndMessage());
      setAllowMultiple(false);
      setValidator(new ISelectionStatusValidator() {

         public IStatus validate(Object[] selection) {
            String message = "";
            int status = Status.ERROR;
            if (selection.length == 0) {
               message = "Must make selection.";
            } else {
               Branch branch = (Branch) selection[0];
               Branch toCheck = branch;
               // Check that it is a working branch
               if (areOnlyWorkingBranchesAllowed() != false) {
                  Branch parentBranch = null;
                  try {
                     parentBranch = branch.getParentBranch();
                  } catch (SQLException ex) {
                     logger.log(Level.SEVERE, "Unable to get parent branch", ex);
                  }
                  toCheck = parentBranch;
               }

               if (toCheck == null) {
                  message = getBranchErrorMessage();
               } else {
                  status = OK;
                  message = String.format("Selected: [%s]", branch.getBranchName());
               }
            }
            return new Status(status, SkynetGuiPlugin.PLUGIN_ID, message);
         }
      });
   }

   private boolean areOnlyWorkingBranchesAllowed() {
      return allowOnlyWorkingBranches;
   }

   private String getTitleAndMessage() {
      return String.format(TITLE_MESSAGE, areOnlyWorkingBranchesAllowed() ? "Working " : "");
   }

   private String getBranchErrorMessage() {
      return String.format(BRANCH_ERROR_MESSAGE, areOnlyWorkingBranchesAllowed() ? "working " : "");
   }

   private void initialize() {
      try {
         List<Branch> branchList = BranchPersistenceManager.getRootBranches();
         setInput(branchList);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Unable to get root branches.", ex);
      }
   }

   public Branch getSelection() {
      return (Branch) getResult()[0];
   }

   private static Branch createDialog(boolean allowOnlyWorkingBranches) {
      Branch toReturn = null;
      BranchSelectionDialog branchSelection =
            new BranchSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), allowOnlyWorkingBranches);
      int result = branchSelection.open();
      if (result == Window.OK) {
         toReturn = branchSelection.getSelection();
      }
      return toReturn;
   }

   public static Branch getBranchFromUser() {
      return createDialog(false);
   }

   public static Branch getWorkingBranchFromUser() {
      return createDialog(true);
   }
}
