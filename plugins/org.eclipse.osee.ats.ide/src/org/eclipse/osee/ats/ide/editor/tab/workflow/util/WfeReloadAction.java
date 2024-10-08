/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor.WfeSaveListener;
import org.eclipse.osee.ats.ide.editor.tab.reload.WfeReloadTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Reload workitem and reset currently selected tab
 *
 * @author Donald G. Dunne
 */
public class WfeReloadAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact workItem;
   private final WorkflowEditor editor;

   public WfeReloadAction(AbstractWorkflowArtifact workItem, WorkflowEditor editor) {
      super();
      this.editor = editor;
      String title = "Reload \"" + workItem.getArtifactTypeName() + "\"";
      setText(title);
      setToolTipText(getText());
      this.workItem = workItem;
   }

   @Override
   public void runWithException() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (editor != null) {
               if (editor.isDirty()) {
                  MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Reload confirmation", null,
                     "The current editor has unsaved changes.  Do you want to save changes and reload?",
                     MessageDialog.NONE, new String[] {"Save and Reload", "Cancel"}, 0);
                  int result = dialog.open();
                  if (result == 1) {
                     return;
                  }
                  editor.doSave(null, new WfeSaveListener() {

                     @Override
                     public void saved(IAtsWorkItem workItem, IAtsChangeSet changes) {
                        try {
                           Thread reloadThread = getReloadThread();
                           reloadThread.start();
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               } else {
                  try {
                     Thread reloadThread = getReloadThread();
                     reloadThread.start();
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         }

         private Thread getReloadThread() {
            Thread reloadThread = new Thread(new Runnable() {

               @Override
               public void run() {
                  Set<Artifact> relatedArts = new HashSet<>();
                  relatedArts.add(workItem);
                  if (workItem.isTeamWorkflow()) {
                     relatedArts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) workItem));
                  }
                  if (workItem instanceof TeamWorkFlowArtifact) {
                     Collection<IAtsTask> tasks =
                        AtsApiService.get().getTaskService().getTasks((TeamWorkFlowArtifact) workItem);
                     relatedArts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(tasks));
                  }

                  ArtifactQuery.reloadArtifacts(relatedArts);
                  WfeReloadTab reload = new WfeReloadTab(editor);
                  reload.reloadEditor("Reload WFE Editor");
               }
            });
            return reloadThread;
         }
      });

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(PluginUiImage.REFRESH);
   }

}
