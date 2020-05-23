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
import org.eclipse.osee.ats.api.task.TaskNameData;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
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
public class ShowBuildRelatedTasksAction extends Action {

   private final static String SHOW_BUILD = "Show Build Related Tasks";
   private final TaskArtifact task;

   public ShowBuildRelatedTasksAction(TaskArtifact task) {
      super(SHOW_BUILD);
      this.task = task;
      setToolTipText("Show build related tasks.");
   }

   @Override
   public void run() {
      TaskNameData data = new TaskNameData(task);
      final String srchStr = data.isRequirement() ? data.getReqName() : task.getName();
      if (!Strings.isValid(srchStr)) {
         AWorkbench.popup("ERROR", "Unable to extract requirement from task name");
         return;
      }
      Job job = new Job("Show All Related Tasks") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               TaskEditor.open(
                  new TaskEditorRelatedTasksProvider(Arrays.asList(srchStr), Arrays.asList(task), false, true));
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
