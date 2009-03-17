/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class RenameShortBranchNameHandler extends CommandHandler {

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider instanceof Viewer) {
         Viewer viewer = (Viewer) selectionProvider;
         Branch selectedBranch =
               Handlers.getBranchesFromStructuredSelection((IStructuredSelection) selectionProvider.getSelection()).iterator().next();

         IInputValidator inputValidator = new IInputValidator() {
            public String isValid(String newText) {
               if (newText == null || newText.length() == 0) {
                  return "The new branch name must not be blank"; // return error message
               }
               if (newText.length() > SkynetDatabase.BRANCH_SHORT_NAME_SIZE) {
                  // return error message
                  return "The new branch name must not be longer than " + SkynetDatabase.BRANCH_SHORT_NAME_SIZE + " characters"; 
               }
               return null; // to indicate the input is valid
            }
         };
         InputDialog dialog =
               new InputDialog(Display.getCurrent().getActiveShell(), "Rename Branch Short Name",
                     "Enter new branch short name",
                     selectedBranch.getBranchShortName() != null ? selectedBranch.getBranchShortName() : "",
                     inputValidator);

         if (dialog.open() != Window.CANCEL) {
            try {
               selectedBranch.setBranchShortName(dialog.getValue(), true);
            } catch (Exception ex) {
               MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Renaming Branch short name",
                     ex.getMessage());
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            viewer.refresh();
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.CommandHandler#isEnabledWithException()
    */
   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);

      return branches.size() == 1;
   }

}
