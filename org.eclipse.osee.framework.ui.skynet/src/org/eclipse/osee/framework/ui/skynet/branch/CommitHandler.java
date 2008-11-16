/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
public class CommitHandler extends AbstractSelectionEnabledHandler {
   private final boolean useParentBranch;
   private final boolean archiveSourceBranch;
   private TreeViewer branchTable;

   public CommitHandler(MenuManager menuManager, boolean useParentBranch, boolean archiveSourceBranch, TreeViewer branchTable) {
      super(menuManager);
      this.useParentBranch = useParentBranch;
      this.archiveSourceBranch = archiveSourceBranch;
      this.branchTable = branchTable;
   }

   public static boolean commitBranch(ConflictManagerExternal conflictManager, boolean archiveSourceBranch) throws OseeCoreException {
      Branch sourceBranch = conflictManager.getFromBranch();
      Branch destinationBranch = conflictManager.getToBranch();
      boolean branchCommitted = false;

      int numRemainingConflicts = conflictManager.getRemainingConflicts().size();
      if (numRemainingConflicts > 0) {
         String message =
               "Commit stopped due to unresolved conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts";
         String[] choices;
         if (AccessControlManager.isOseeAdmin()) {
            message += "\n  Force the commit";
            choices = new String[] {"Cancel", "Launch Merge Manager", "Force Commit"};
         } else {
            choices = new String[] {"Cancel", "Launch Merge Manager"};
         }

         MessageDialog dialog =
               new MessageDialog(Display.getCurrent().getActiveShell(), "Unresolved Conflicts", null, message,
                     MessageDialog.QUESTION, choices, 0);

         int result = dialog.open();
         if (result == 1) {
            MergeView.openView(sourceBranch, destinationBranch,
                  TransactionIdManager.getStartEndPoint(sourceBranch).getKey());
         } else if (result == 2) {
            BranchManager.commitBranch(conflictManager, archiveSourceBranch);
            branchCommitted = true;
         }
      } else {
         StringBuilder message =
               new StringBuilder(
                     "Commit branch\n\n\"" + sourceBranch + "\"\n\n onto destination branch\n\n\"" + destinationBranch + "\"\n");
         int numOriginalConfilcts = conflictManager.getOriginalConflicts().size();
         if (numOriginalConfilcts > 0) {
            message.append("\nwith " + numOriginalConfilcts + " conflicts resolved.\n");
         } else {
            message.append("\n(no conflicts found)\n");
         }
         message.append("\nCommit?");
         MessageDialog dialog =
               new MessageDialog(Display.getCurrent().getActiveShell(), "Commit Branch", null, message.toString(),
                     MessageDialog.QUESTION, new String[] {"Ok", "Launch Merge Manager", "Cancel"}, 0);
         int result = dialog.open();
         if (result == 0) {
            BranchManager.commitBranch(conflictManager, archiveSourceBranch);
            branchCommitted = true;
         } else if (result == 1) {
            MergeView.openView(sourceBranch, sourceBranch.getParentBranch(), TransactionIdManager.getStartEndPoint(
                  sourceBranch).getKey());
         }
      }
      return branchCommitted;
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

      Branch sourceBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
      try {
         Branch destinationBranch = null;
         if (useParentBranch) {
            destinationBranch = sourceBranch.getParentBranch();
         } else {
            destinationBranch = BranchManager.getBranch(Integer.parseInt(event.getParameter(BranchView.BRANCH_ID)));
         }
         commitBranch(new ConflictManagerExternal(destinationBranch, sourceBranch), archiveSourceBranch);
      } catch (OseeCoreException ex) {
         throw new ExecutionException(ex.getLocalizedMessage(), ex);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      boolean validBranchSelected = SkynetSelections.oneDescendantBranchSelected(selection) && useParentBranch;

      if (validBranchSelected) {
         validBranchSelected &=
               !((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())).isChangeManaged();
      }
      return (validBranchSelected) || (!useParentBranch && AccessControlManager.isOseeAdmin() && SkynetSelections.oneBranchSelected(selection));
   }
}