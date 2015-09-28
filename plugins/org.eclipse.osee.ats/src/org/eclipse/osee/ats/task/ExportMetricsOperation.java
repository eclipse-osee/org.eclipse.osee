/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.task;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
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
      List<TaskArtifact> tasks = new ArrayList<>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            tasks.add((TaskArtifact) artifact);
         }
      }
      return tasks;
   }

}
