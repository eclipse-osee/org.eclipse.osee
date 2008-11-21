/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
      final Branch sourceBranch = conflictManager.getFromBranch();
      final Branch destinationBranch = conflictManager.getToBranch();
      final TransactionId transactionId = TransactionIdManager.getStartEndPoint(sourceBranch).getKey();
      boolean branchCommitted = false;

      int numRemainingConflicts = conflictManager.getRemainingConflicts().size();
      if (numRemainingConflicts > 0) {
         String message =
               "Commit stopped due to unresolved conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts";
         final String fMessage;
         final String[] choices;
         if (AccessControlManager.isOseeAdmin()) {
            fMessage = message + "\n  Force the commit";
            choices = new String[] {"Cancel", "Launch Merge Manager", "Force Commit"};
         } else {
            fMessage = message;
            choices = new String[] {"Cancel", "Launch Merge Manager"};
         }

         final MutableInteger dialogResult = new MutableInteger(0);
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               MessageDialog dialog =
                     new MessageDialog(Display.getCurrent().getActiveShell(), "Unresolved Conflicts", null, fMessage,
                           MessageDialog.QUESTION, choices, 0);
               dialogResult.setValue(dialog.open());
               if (dialogResult.getValue() == 1) {
                  MergeView.openView(sourceBranch, destinationBranch, transactionId);
               }
            }
         });

         if (dialogResult.getValue() == 2) {
            BranchManager.commitBranch(conflictManager, archiveSourceBranch, true);
            branchCommitted = true;
         }
      } else {
         final StringBuilder message =
               new StringBuilder(
                     "Commit branch\n\n\"" + sourceBranch + "\"\n\n onto destination branch\n\n\"" + destinationBranch + "\"\n");
         int numOriginalConfilcts = conflictManager.getOriginalConflicts().size();
         if (numOriginalConfilcts > 0) {
            message.append("\nwith " + numOriginalConfilcts + " conflicts resolved.\n");
         } else {
            message.append("\n(no conflicts found)\n");
         }
         message.append("\nCommit?");

         final MutableInteger dialogResult = new MutableInteger(0);
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               MessageDialog dialog =
                     new MessageDialog(Display.getCurrent().getActiveShell(), "Commit Branch", null,
                           message.toString(), MessageDialog.QUESTION, new String[] {"Ok", "Launch Merge Manager",
                                 "Cancel"}, 0);
               dialogResult.setValue(dialog.open());
               if (dialogResult.getValue() == 1) {
                  MergeView.openView(sourceBranch, destinationBranch, transactionId);
               }
            }
         });

         if (dialogResult.getValue() == 0) {
            BranchManager.commitBranch(conflictManager, archiveSourceBranch, false);
            branchCommitted = true;
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
         Jobs.startJob(new CommitJob(sourceBranch, destinationBranch));
      } catch (OseeCoreException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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

   private class CommitJob extends Job {
      private Branch sourceBranch;
      private Branch destinationBranch;

      /**
       * @param name
       * @param destinationBranch
       * @param sourceBranch
       */
      public CommitJob(Branch sourceBranch, Branch destinationBranch) {
         super("Commit Branch");
         this.destinationBranch = destinationBranch;
         this.sourceBranch = sourceBranch;
      }

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            commitBranch(new ConflictManagerExternal(destinationBranch, sourceBranch), archiveSourceBranch);
         } catch (OseeCoreException ex) {
            return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
         }
         return Status.OK_STATUS;
      }
   }
}