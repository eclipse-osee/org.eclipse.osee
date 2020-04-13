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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeAndDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class ShowRelatedRequirementDiffsAction extends AbstractShowRelatedAction {

   public ShowRelatedRequirementDiffsAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Show Requirement Diffs", selectedAtsArtifacts);
   }

   @Override
   public void run() {
      final Collection<IAtsTask> tasks = getSelectedTasks();
      if (!isAutoGenRelatedArtTasks(tasks)) {
         return;
      }
      Job job = new Job("Show Requirement Diffs") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            IOperation operation = new ShowRequirementDifferencesOperation(new UISelectArtifactsToDiff(), tasks, false);
            Operations.executeAsJob(operation, true, Job.SHORT, null);
            return Status.OK_STATUS;
         }
      };
      job.setPriority(Job.SHORT);
      job.schedule();
   }

   private static final class UISelectArtifactsToDiff implements ShowRequirementDifferencesOperation.Display {

      @Override
      public Artifact getArtifactSelection(IProgressMonitor monitor, final Collection<? extends Artifact> selectableArtifacts) throws Exception {
         final Artifact[] selectedArtifact = new Artifact[1];
         Job job = new UIJob("Select artifacts to diff") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog("Select Artifact",
                  "Multiple artifacts of same name.  Select Artifact to Diff", selectableArtifacts,
                  new ArtifactTypeAndDescriptiveLabelProvider());
               dialog.setMultiSelect(false);
               int result = dialog.open();
               if (result == Window.OK) {
                  selectedArtifact[0] = dialog.getSelectedFirst();
                  return Status.OK_STATUS;
               } else {
                  return Status.CANCEL_STATUS;
               }
            }

         };
         Jobs.startJob(job);
         job.join();
         Operations.checkForErrorStatus(job.getResult());
         return selectedArtifact[0];
      }

      @Override
      public void showDifferences(Collection<ArtifactDelta> artifactDeltas) {
         Conditions.checkNotNull(artifactDeltas, "selected artifact change from change report");
         String pathPrefix = RenderingUtil.getAssociatedArtifactName(artifactDeltas);
         RendererManager.diffInJob(artifactDeltas, pathPrefix);
      }

   }

}
