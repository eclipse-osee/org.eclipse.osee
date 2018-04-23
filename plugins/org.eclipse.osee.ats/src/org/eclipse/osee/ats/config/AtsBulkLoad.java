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
package org.eclipse.osee.ats.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * @author Donald G. Dunne
 */
public class AtsBulkLoad {

   private static AtomicBoolean atsTypeDataLoadedStarted = new AtomicBoolean(false);

   public static List<IOperation> getConfigLoadingOperations() {
      List<IOperation> ops = new ArrayList<>();
      if (atsTypeDataLoadedStarted.compareAndSet(false, true) && DbConnectionUtility.isVersionSupported()) {
         IOperation op = new AbstractOperation("Re-load ATS Config", Activator.PLUGIN_ID) {
            @Override
            protected void doWork(IProgressMonitor monitor) throws Exception {
               AtsClientService.get().clearCaches();
               AtsClientService.get().getConfigService().getConfigurations();
            }
         };
         ops.add(op);
      } else {
         ops.add(Operations.createNoOpOperation("ATS Bulk Loading"));
      }
      return ops;
   }

   public static Set<Artifact> bulkLoadArtifacts(Collection<? extends Artifact> artifacts) {
      List<Artifact> actions = new ArrayList<>();
      List<Artifact> teams = new ArrayList<>();
      List<Artifact> tasks = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (IAtsAction.isOfType(art)) {
            actions.add(art);
         } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            teams.add(art);
         } else if (art.isOfType(AtsArtifactTypes.Task)) {
            tasks.add(art);
         }
      }
      Set<Artifact> arts = new HashSet<Artifact>();
      if (!actions.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(actions, 3, AtsRelationTypes.TeamWfToTask_Task,
            AtsRelationTypes.TeamWorkflowToReview_Review, AtsRelationTypes.ActionToWorkflow_Action));
      }
      if (!teams.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(teams, 4, AtsRelationTypes.TeamWfToTask_Task,
            AtsRelationTypes.ActionToWorkflow_WorkFlow, AtsRelationTypes.TeamWorkflowToReview_Review));
      }
      if (!tasks.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(tasks, 2, AtsRelationTypes.TeamWfToTask_TeamWf,
            AtsRelationTypes.ActionToWorkflow_WorkFlow));
      }
      arts.addAll(artifacts);
      return arts;
   }
}
