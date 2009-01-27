/*
 * Created on Jan 26, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
class DeleteArtifactAction extends Action {
   private final Artifact artifact;

   public DeleteArtifactAction(Artifact artifact) {
      super("&Delete Artifact\tDelete", Action.AS_PUSH_BUTTON);
      this.artifact = artifact;
   }

   @Override
   public void run() {
      try {
         MessageDialog dialog =
               new MessageDialog(Display.getCurrent().getActiveShell(), "Confirm Artifact Deletion", null,
                     " Are you sure you want to delete this artifact and all of the default hierarchy children?",
                     MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
         if (dialog.open() == Window.OK) {
            artifact.delete();
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}
