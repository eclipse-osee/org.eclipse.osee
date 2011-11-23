/*
 * Created on Nov 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ExportMetricsOperation extends AbstractOperation {

   private final List<? extends Artifact> artifacts;

   public ExportMetricsOperation(List<? extends Artifact> artifacts) {
      super("Export Metrics", Activator.PLUGIN_ID);
      this.artifacts = artifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<TaskArtifact> taskList = asTask(artifacts);
      for (TaskArtifact task : taskList) {
         System.out.println(task.getName());
      }
   }

   private List<TaskArtifact> asTask(List<? extends Artifact> artifacts) {
      List<TaskArtifact> tasks = new ArrayList<TaskArtifact>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            tasks.add((TaskArtifact) artifact);
         }
      }
      return tasks;
   }

}
