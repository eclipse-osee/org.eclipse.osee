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
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

public class DeleteTasksAction extends AbstractAtsAction {

   public static interface TaskArtifactProvider {

      List<TaskArtifact> getSelectedArtifacts();

   }

   private final TaskArtifactProvider taskProvider;

   public DeleteTasksAction(TaskArtifactProvider taskProvider) {
      super("Delete Tasks", IAction.AS_PUSH_BUTTON);
      this.taskProvider = taskProvider;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.X_RED));
      setToolTipText(getText());
   }

   public void updateEnablement(Collection<Artifact> selected) {
      for (Artifact art : selected) {
         if (!(art instanceof TaskArtifact)) {
            setEnabled(false);
            return;
         }
      }
      setEnabled(true);
   }

   @Override
   public void run() {
      final List<TaskArtifact> items = taskProvider.getSelectedArtifacts();
      if (items.isEmpty()) {
         AWorkbench.popup("ERROR", "No Tasks Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      if (items.size() > 15) {
         builder.append("Are you sure you wish to delete " + items.size() + " Tasks?\n\n");
      } else {
         builder.append("Are you sure you wish to delete ");
         if (items.size() == 1) {
            builder.append("this Task?\n\n");
         } else {
            builder.append("these Tasks?\n\n");
         }
         for (TaskArtifact taskItem : items) {
            builder.append("\"" + taskItem.getName() + "\"\n");
         }

         builder.append("\n\nNote: Workflow will be saved.");

      }
      boolean delete = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
         "Delete Task", builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Delete Tasks");
            // Done for concurrent modification purposes
            ArrayList<TaskArtifact> delItems = new ArrayList<>();
            ArrayList<TaskArtifact> tasksNotInDb = new ArrayList<>();
            delItems.addAll(items);
            for (TaskArtifact taskArt : delItems) {
               WorkflowEditor.close(Collections.singleton(taskArt), false);
               if (taskArt.isInDb()) {
                  taskArt.deleteAndPersist(transaction);
               } else {
                  tasksNotInDb.add(taskArt);
               }
               AtsTaskCache.decache(taskArt.getParentAWA());
            }
            transaction.execute();

            if (tasksNotInDb.size() > 0) {
               Operations.executeWorkAndCheckStatus(new PurgeArtifacts(tasksNotInDb));
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
