/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.branch;

import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.ConflictDetectionException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

      Branch fromBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
      Branch toBranch = null;

      try {
         if (useParentBranch) {
            toBranch = fromBranch.getParentBranch();
         } else {
            toBranch = BranchPersistenceManager.getBranch(Integer.parseInt(event.getParameter(BranchView.BRANCH_ID)));
         }
         BranchPersistenceManager.commitBranch(fromBranch, toBranch, archiveSourceBranch, false);
      } catch (ConflictDetectionException ex) {
         try {
            handleConflicts(fromBranch, toBranch);
         } catch (Exception ex1) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Commit Branch Failed", ex);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Commit Branch Failed", ex);
      }

      return null;
   }

   public void handleConflicts(Branch fromBranch, Branch toBranch) throws SQLException, OseeCoreException {
      MessageDialog dialog;
      if (OseeProperties.isDeveloper()) {
         dialog =
               new MessageDialog(
                     Display.getCurrent().getActiveShell(),
                     "Commit Failed",
                     null,
                     "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts\n  Force the commit",
                     MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager", "Force Commit"}, 0);
      } else {
         dialog =
               new MessageDialog(
                     Display.getCurrent().getActiveShell(),
                     "Commit Failed",
                     null,
                     "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts",
                     MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager"}, 0);

      }

      int result = dialog.open();
      if (result == 1) {
         MergeView.openView(fromBranch, toBranch, TransactionIdManager.getStartEndPoint(fromBranch).getKey());
      } else if (result == 2) {
         BranchPersistenceManager.commitBranch(fromBranch, toBranch, true, true);
      }
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      boolean validBranchSelected = SkynetSelections.oneDescendantBranchSelected(selection) && useParentBranch;

      if (validBranchSelected) {
         validBranchSelected &=
               !((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())).isChangeManaged();
      }
      return (validBranchSelected) || (!useParentBranch && OseeProperties.isDeveloper() && SkynetSelections.oneBranchSelected(selection));
   }
}
