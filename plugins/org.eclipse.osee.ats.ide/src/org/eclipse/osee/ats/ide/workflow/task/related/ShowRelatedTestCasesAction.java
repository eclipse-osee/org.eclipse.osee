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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.task.related.DerivedFromTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.trace.TraceabilityResultsEditor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ShowRelatedTestCasesAction extends AbstractShowRelatedAction {

   private final static String ACTION_TITLE = "Show Related Test Cases";

   public ShowRelatedTestCasesAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(ACTION_TITLE, selectedAtsArtifacts);
   }

   @Override
   public void run() {
      final Collection<IAtsTask> tasks = getSelectedTasks();
      if (!isAutoGenRelatedArtTasks(tasks)) {
         return;
      }
      final List<IAtsTask> taskArtsNotFound = new LinkedList<>();
      final List<Artifact> requirements = new LinkedList<>();
      Job job = new Job(ACTION_TITLE) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            for (IAtsTask task : tasks) {
               try {
                  DerivedFromTaskData reqData = AtsApiService.get().getTaskRelatedService().getDerivedFromTaskData(task);

                  BranchId workingBranch =
                     AtsApiService.get().getBranchService().getWorkingBranchInWork(task.getParentTeamWorkflow());
                  Artifact artRef = (Artifact) reqData.getHeadArtifact();
                  if (artRef == null) {
                     taskArtsNotFound.add(task);
                  } else {
                     Artifact onWorkingBranch = null;
                     if (workingBranch.isValid()) {
                        onWorkingBranch = ArtifactQuery.getArtifactFromId(artRef, workingBranch);
                     }
                     requirements.add(onWorkingBranch == null ? artRef : onWorkingBranch);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
            TraceabilityResultsEditor editor =
               new TraceabilityResultsEditor("Open Test Cases Editor", Activator.PLUGIN_ID, requirements);
            return Operations.executeWork(editor);
         }
      };
      job.setPriority(Job.SHORT);
      job.schedule();
   }

}
