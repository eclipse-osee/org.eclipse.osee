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
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.task.ChangeReportTasksUtil;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractShowRelatedAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public AbstractShowRelatedAction(String name, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(name, IAction.AS_PUSH_BUTTON);
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      boolean shouldEnable = false;
      try {
         shouldEnable = !selectedAtsArtifacts.getSelectedTaskArtifacts().isEmpty();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      setEnabled(shouldEnable);
   }

   protected boolean isAutoGenRelatedArtTasks(final Collection<IAtsTask> tasks) {
      try {
         if (!AtsApiService.get().getTaskRelatedService().isAutoGenChangeReportRelatedTasks(tasks)) {
            AWorkbench.popup(ChangeReportTasksUtil.TASKS_MUST_BE_AUTOGEN_CHANGE_REPORT_RELATED_TASKS);
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   protected Collection<IAtsTask> getSelectedTasks() {
      return Collections.castAll(getSelectedTaskArtifacts(selectedAtsArtifacts));
   }

   protected static Collection<TaskArtifact> getSelectedTaskArtifacts(ISelectedAtsArtifacts selectedAtsArtifacts) {
      try {
         return selectedAtsArtifacts.getSelectedTaskArtifacts();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public static boolean isValid(ISelectedAtsArtifacts selectedAtsArtifacts) {
      for (TaskArtifact task : getSelectedTaskArtifacts(selectedAtsArtifacts)) {
         if (!isValid(task)) {
            return false;
         }
      }
      return true;
   }

   public static boolean isValid(TaskArtifact task) {
      try {
         if (!AtsApiService.get().getTaskRelatedService().isAutoGenChangeReportRelatedTask(task)) {
            return false;
         }
         TeamWorkFlowArtifact teamArt = task.getParentTeamWorkflow();
         if (AtsApiService.get().getProgramService().getProgram(teamArt) == null) {
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

}
