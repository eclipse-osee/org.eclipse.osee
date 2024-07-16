/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.MergeInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxBranchDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Angel Avila
 */
public class XWorkingBranchButtonDeleteMergeBranches extends XWorkingBranchButtonAbstract {

   public final static String WIDGET_NAME = "XWorkingBranchButtonDeleteMergeBranches";

   @Override
   protected void initButton(final Button button) {
      button.setToolTipText("Delete Merge Branch(es)");
      button.setImage(ImageManager.getImage(FrameworkImage.DELETE_MERGE_BRANCHES));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               BranchId workingBranch = getTeamArt().getWorkingBranch();
               if (isWorkingBranchCommitWithMergeInProgress()) {
                  List<BranchId> selectedBranches = new ArrayList<>();
                  Collection<BranchToken> branchesAlreadyCommitted =
                     AtsApiService.get().getBranchService().getBranchesCommittedTo(getTeamArt());
                  List<MergeBranch> mergeBranches = BranchManager.getMergeBranches(workingBranch);

                  Set<BranchId> destinationMinusAlreadyCommitted = new HashSet<>();
                  // Remove all the Merge branches having to do with a Destination branch that's already been committed, can't delete these merge branches
                  for (MergeBranch branch : mergeBranches) {
                     if (!branchesAlreadyCommitted.contains(branch.getDestinationBranch())) {
                        destinationMinusAlreadyCommitted.add(branch.getDestinationBranch());
                     }
                  }

                  if (destinationMinusAlreadyCommitted.size() > 1) {
                     FilteredCheckboxBranchDialog dialog =
                        new FilteredCheckboxBranchDialog("Select Destination Branch(es)",
                           "Select the Destination branch(es) for which you want to Delete the Merge Branch",
                           destinationMinusAlreadyCommitted);
                     if (dialog.open() == Window.OK) {
                        for (BranchId branchToken : dialog.getChecked()) {
                           selectedBranches.add(branchToken);
                        }
                     }
                  } else if (destinationMinusAlreadyCommitted.size() == 1) {
                     MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(workingBranch);
                     selectedBranches.add(mergeBranch.getDestinationBranch());
                  }

                  if (!selectedBranches.isEmpty()) {
                     MergeInProgressHandler.deleteMultipleMergeBranches(workingBranch, selectedBranches, false);
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(destinationBranchNotCommitted() && isWidgetAllowedInCurrentState());
   }

   private boolean destinationBranchNotCommitted() {
      boolean toReturn = false;
      try {
         if (isWorkingBranchCommitWithMergeInProgress()) {
            List<MergeBranch> mergeBranches = BranchManager.getMergeBranches(getWorkingBranch());
            Collection<BranchToken> committedBranches =
               AtsApiService.get().getBranchService().getBranchesCommittedTo(getTeamArt());
            List<MergeBranch> remainingMergeBranches = new ArrayList<>();

            for (MergeBranch mergeBranch : mergeBranches) {
               if (!committedBranches.contains(mergeBranch.getDestinationBranch())) {
                  remainingMergeBranches.add(mergeBranch);
               }
            }
            if (!remainingMergeBranches.isEmpty()) {
               toReturn = true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return toReturn;

   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return isWidgetInState(WIDGET_NAME);
   }
}
