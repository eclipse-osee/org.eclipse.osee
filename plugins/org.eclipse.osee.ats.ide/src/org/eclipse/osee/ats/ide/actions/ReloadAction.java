/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor.WfeSaveListener;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
 * @author Donald G. Dunne
 */
public class ReloadAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;
   private final WorkflowEditor editor;

   public ReloadAction(AbstractWorkflowArtifact sma, WorkflowEditor editor) {
      super();
      this.editor = editor;
      String title = "Reload \"" + sma.getArtifactTypeName() + "\"";
      setText(title);
      setToolTipText(getText());
      this.sma = sma;
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
                        // Can't close the editor until save or it will do it's own dirty editor save dialog
                        editor.close(false);
                        try {
                           Thread reloadThread = getReloadThread();
                           reloadThread.start();
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               } else {
                  editor.close(false);
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
                  relatedArts.add(sma);
                  if (sma.isTeamWorkflow()) {
                     relatedArts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) sma));
                  }
                  if (sma instanceof TeamWorkFlowArtifact) {
                     Collection<IAtsTask> tasks =
                        AtsClientService.get().getTaskService().getTasks((TeamWorkFlowArtifact) sma);
                     relatedArts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(tasks));
                  }

                  ArtifactQuery.reloadArtifacts(relatedArts);
                  WorkflowEditor.edit(sma);
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
