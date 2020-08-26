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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class ShowRelatedTasksAction extends AbstractShowRelatedAction {

   private Collection<String> srchStrs = new ArrayList<>();

   public ShowRelatedTasksAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Show Related Tasks", selectedAtsArtifacts);
   }

   @Override
   public void run() {
      srchStrs = new ArrayList<>();
      final Collection<IAtsTask> tasks = getSelectedTasks();
      if (!isAutoGenRelatedArtTasks(tasks)) {
         return;
      }
      for (IAtsTask task : tasks) {
         IAutoGenTaskData data = AtsApiService.get().getTaskRelatedService().getAutoGenTaskData(task);
         final String srchStr = data.hasRelatedArt() ? data.getRelatedArtName() : task.getName();
         if (!Strings.isValid(srchStr)) {
            AWorkbench.popup("ERROR", "Unable to extract requirement from task name");
            return;
         }
         srchStrs.add(srchStr);
      }

      Job job = new Job("Show All Related Tasks") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               TaskEditor.open(new TaskEditorRelatedTasksProvider(srchStrs, Collections.castAll(tasks), false, false));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            } finally {
               monitor.done();
            }
            return Status.OK_STATUS;
         }
      };
      job.setPriority(Job.SHORT);
      job.schedule();
   }

}
