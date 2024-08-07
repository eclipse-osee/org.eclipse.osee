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

package org.eclipse.osee.ats.ide.workflow.task.related;

import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Megumi Telles
 */
public class ShowAllRelatedTasksAction extends Action {

   private final static String SHOW_ALL = "Show All Related Tasks";
   private final TaskArtifact task;

   public ShowAllRelatedTasksAction(TaskArtifact task) {
      super(SHOW_ALL);
      this.task = task;
      setToolTipText("Show all related tasks from all programs.");
   }

   @Override
   public void run() {
      IAutoGenTaskData data = AtsApiService.get().getTaskRelatedService().getAutoGenTaskData(task);
      final String srchStr = data.hasRelatedArt() ? data.getRelatedArtName() : task.getName();
      if (!Strings.isValid(srchStr)) {
         AWorkbench.popup("ERROR", "Unable to extract requirement from task name");
         return;
      }
      Job job = new Job("Show All Related Tasks") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               TaskEditor.open(
                  new TaskEditorRelatedTasksProvider(Arrays.asList(srchStr), Arrays.asList(task), true, false));
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

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TASK);
   }

}
