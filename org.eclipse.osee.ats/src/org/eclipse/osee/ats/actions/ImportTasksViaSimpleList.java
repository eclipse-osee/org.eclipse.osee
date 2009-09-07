/*
 * Created on Sep 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.operation.ImportTasksFromSimpleList;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksViaSimpleList extends Action {

   private final TaskableStateMachineArtifact taskableArt;
   private final Listener listener;

   public ImportTasksViaSimpleList(TaskableStateMachineArtifact taskableArt, Listener listener) {
      this.taskableArt = taskableArt;
      this.listener = listener;
      setText("Import Tasks via simple list");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.IMPORT));
   }

   @Override
   public void run() {
      try {
         ImportTasksFromSimpleList blamOperation = new ImportTasksFromSimpleList();
         blamOperation.setTaskableStateMachineArtifact(taskableArt);
         BlamEditor.edit(blamOperation);
         if (listener != null) {
            listener.notify();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
