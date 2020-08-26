/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.related;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.task.related.DerivedFromTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ShowRelatedRequirementAction extends AbstractShowRelatedAction {

   public ShowRelatedRequirementAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Show Related Requirement", selectedAtsArtifacts);
   }

   @Override
   public void run() {
      final Collection<IAtsTask> tasks = getSelectedTasks();
      if (!isAutoGenRelatedArtTasks(tasks)) {
         return;
      }

      final MutableInteger answer = new MutableInteger(-1);

      AbstractOperation openOp = new AbstractOperation("Show Related Requirement", Activator.PLUGIN_ID) {

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {

                  String taskStr =
                     tasks.size() > 1 ? "these " + tasks.size() + " tasks" : tasks.iterator().next().getName();
                  String dialogMessage = String.format(
                     "Are you sure you want to open the latest requirement(s) for %s?\n\nNote: Deleted requirements will default to historical version.",
                     taskStr);

                  MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Show Related Requirement", null,
                     dialogMessage, MessageDialog.NONE, new String[] {"OK", "Historical", "Cancel"}, 0);

                  answer.setValue(dialog.open());

               }
            }, true);

            boolean loadLatest = true;
            if (answer.getValue() == 1) {
               loadLatest = false;
            } else if (answer.getValue() == 2) {
               return;
            }
            showRequirements(tasks, loadLatest);
         }
      };

      Operations.executeAsJob(openOp, true);

   }

   private void showRequirements(final Collection<? extends IAtsTask> tasks, final boolean loadLatest) {

      for (final IAtsTask task : tasks) {
         IOperation loadReqs = new AbstractOperation("Show Related Requirement", Activator.PLUGIN_ID) {

            @Override
            protected void doWork(IProgressMonitor monitor) throws Exception {
               DerivedFromTaskData trd = AtsApiService.get().getTaskRelatedService().getDerivedFromTaskData(task);
               trd.getResults().logf(getName() + "\n\n");
               trd.getResults().logf("Task %s \n\n", task.toStringWithId());
               AtsApiService.get().getTaskRelatedService().populateDerivedFromTaskData(trd);
               if (trd.getResults().isErrors()) {
                  XResultDataUI.report(trd.getResults(), getName());
               } else {
                  dialog(trd, loadLatest);
               }
            }
         };

         Operations.executeAsJob(loadReqs, true);
      }
   }

   private void dialog(final DerivedFromTaskData reqData, final boolean loadLatest) {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            Artifact toOpen = null;
            if (reqData.isDeleted()) {
               toOpen = AtsApiService.get().getQueryServiceIde().getArtifact(reqData.getHeadArtifact());
            } else {
               if (loadLatest) {
                  toOpen = AtsApiService.get().getQueryServiceIde().getArtifact(reqData.getLatestArt());
               } else {
                  toOpen = AtsApiService.get().getQueryServiceIde().getArtifact(reqData.getHeadArtifact());
               }
            }
            try {
               RendererManager.open(toOpen, PresentationType.DEFAULT_OPEN);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   LabelProvider relatedArtifactLabelProvider = new LabelProvider() {
      @Override
      public String getText(Object element) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(element);
         if (art != null) {
            if (art.isHistorical()) {
               return "[Rev:" + art.getTransaction() + "] - " + art.getName();
            }
            return "[Rev:Head] - " + art.getName();
         }
         return "";
      }
   };

}
