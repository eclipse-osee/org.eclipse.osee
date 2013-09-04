/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

/**
 * @author Angel Avila
 */

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

public class MergeInProgressHandler {
   private static boolean archiveBranch;
   private final static String TITLE = "Commit In Progress";
   private static String Messages;
   private static String[] Choices;
   private static final int COMMIT = 0;
   private static final int LAUNCH_MERGE_VIEW = 1;
   private static final int DELETE_MERGE = 2;
   private static final int CANCEL = 3;
   private static final int FORCE_COMMIT = 4;

   public static boolean handleMergeInProgress(final ConflictManagerExternal conflictManager, boolean archive, boolean isSkipPrompts) throws OseeCoreException {
      archiveBranch = archive;
      int userOption;
      if (!isSkipPrompts) {
         userOption = promptUserMutlipleChoices(conflictManager);
      } else {
         userOption = CANCEL;
      }

      return handleCommitInProgressPostPrompt(conflictManager, userOption, isSkipPrompts);
   }

   public static boolean handleCommitInProgressPostPrompt(final ConflictManagerExternal conflictManager, int userOption, boolean skipPrompts) throws OseeCoreException {
      boolean toReturn = false;
      Branch sourceBranch = conflictManager.getSourceBranch();
      Branch destinationBranch = conflictManager.getDestinationBranch();

      if (userOption == COMMIT) { // Commit
         BranchManager.commitBranch(null, conflictManager, archiveBranch, false);
         toReturn = true;
      } else if (userOption == LAUNCH_MERGE_VIEW) { // Launch Merge
         MergeView.openView(sourceBranch, destinationBranch, sourceBranch.getBaseTransaction());
      } else if (userOption == DELETE_MERGE) { // Delete Merge
         deleteSingleMergeBranches(sourceBranch, destinationBranch, skipPrompts);
      } else if (userOption == FORCE_COMMIT) { // Force Commit, admin only
         BranchManager.commitBranch(null, conflictManager, archiveBranch, true);
         toReturn = true;
      } else if (userOption == CANCEL) {
         // do nothing
      }
      return toReturn;
   }

   public static void deleteMultipleMergeBranches(Branch sourceBranch, List<Branch> destBranches, boolean skipPrompts) throws OseeCoreException {
      if (skipPrompts || promptUser(sourceBranch, destBranches)) {
         for (Branch branch : destBranches) {
            doDelete(sourceBranch, branch);
         }
      }
   }

   public static void deleteSingleMergeBranches(Branch sourceBranch, Branch destBranch, boolean skipPrompts) throws OseeCoreException {
      if (skipPrompts || promptUser(sourceBranch, Arrays.asList(destBranch))) {
         doDelete(sourceBranch, destBranch);
      }
   }

   private static void doDelete(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      if (BranchManager.hasMergeBranches(sourceBranch)) {
         MergeBranch mergeBranch = BranchManager.getMergeBranch(sourceBranch, destBranch);
         BranchManager.purgeBranch(mergeBranch);
      }
   }

   private static boolean promptUser(Branch sourceBranch, List<Branch> destinationBranches) throws OseeCoreException {
      final MutableBoolean isUserSure = new MutableBoolean(false);
      final String message = constructConfirmMessage(sourceBranch, destinationBranches);

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            isUserSure.setValue(MessageDialog.openConfirm(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Merge Branch", message));
         }
      });

      return isUserSure.booleanValue();
   }

   private static int promptUserMutlipleChoices(ConflictManagerExternal conflictManager) throws OseeCoreException {
      boolean isAllConflictsResolved = !conflictManager.remainingConflictsExist();
      Messages = constructMessage(conflictManager, isAllConflictsResolved);
      Choices = constructChoices(conflictManager, isAllConflictsResolved);
      final MutableInteger result = new MutableInteger(CANCEL);

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            MessageDialog dialog =
               new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), TITLE, null,
                  Messages, MessageDialog.QUESTION, Choices, CANCEL);
            result.setValue(dialog.open());
         }
      });

      if (!isAllConflictsResolved) { // Since all conflicts were not resolved, options start with Launch Merge Manager(1) instead of Commit(0)
         result.getValueAndInc();
      }

      return result.getValue();
   }

   private static String constructMessage(final ConflictManagerExternal conflictManager, boolean allConflictsResolved) throws OseeCoreException {
      StringBuilder message = new StringBuilder();
      Branch sourceBranch = conflictManager.getSourceBranch();
      Branch destinationBranch = conflictManager.getDestinationBranch();

      message.append(String.format(
         "Couldn't commit branch because of unresolved conflicts\n\n\"[%s]\"\n\n onto destination branch \n\n\"[%s]\"\n\n",
         sourceBranch, destinationBranch));

      int numOriginalConflicts = conflictManager.getOriginalConflicts().size();
      if (allConflictsResolved) {
         message.append(String.format("with all (%d) conflicts resolved\n\nWould you like to Commit?",
            numOriginalConflicts));
      } else {
         message.append("with " + conflictManager.getRemainingConflicts().size() + " / " + numOriginalConflicts + " conflicts still unresolved\n");
      }

      return message.toString();
   }

   private static String[] constructChoices(final ConflictManagerExternal conflictManager, boolean allConflictsResolved) {
      String[] choices;
      boolean isAdmin = false;
      try {
         if (AccessControlManager.isOseeAdmin()) {
            isAdmin = true;
         }
      } catch (OseeCoreException ex) {
         isAdmin = false;
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }

      if (allConflictsResolved) {
         choices = new String[] {"Commit", "Launch Merge Manager", "Delete Merge", "Cancel"};
      } else {
         if (isAdmin) {
            choices = new String[] {"Launch Merge Manager", "Delete Merge", "Cancel", "Force Commit (Admin Only)"};
         } else {
            choices = new String[] {"Launch Merge Manager", "Delete Merge", "Cancel"};
         }
      }
      return choices;
   }

   private static String constructConfirmMessage(Branch sourceBranch, List<Branch> branches) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      String ending = "";
      String beginning = "";

      sb.append("Are you sure you want to delete the merge ");
      if (branches.size() > 1) {
         beginning = "branches:\n";
         ending = "\n\nAll merged conflicts for these branches will be lost.";
      } else {
         beginning = "branch:\n";
         ending = "\n\nAll merged conflicts for this branch will be lost.";
      }
      sb.append(beginning);
      for (Branch branch : branches) {
         MergeBranch mergeBranch = BranchManager.getMergeBranch(sourceBranch, branch);
         sb.append(mergeBranch);
         sb.append("\n");
      }
      sb.append(ending);

      return sb.toString();
   }

}
