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
package org.eclipse.osee.ats.core.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBulkLoad {

   private static boolean atsTypeDataLoadedStarted = false;

   public synchronized static List<IOperation> getConfigLoadingOperations() {
      List<IOperation> ops = new ArrayList<IOperation>();
      if (atsTypeDataLoadedStarted == false) {
         atsTypeDataLoadedStarted = true;
         ops.add(new AtsLoadConfigArtifactsOperation());
         ops.add(new AtsLoadWorkDefinitionsOperation());
         ops.add(new AtsLoadDictionaryOperation());
      } else {
         ops.add(new EmptyOperation("ATS Bulk Loading", Activator.PLUGIN_ID));
      }
      return ops;
   }

   public static void reloadConfig(boolean pend) {
      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new AtsLoadConfigArtifactsOperation(true));
      ops.add(new AtsLoadWorkDefinitionsOperation(true));
      IOperation operation = new CompositeOperation("Re-load ATS Config", Activator.PLUGIN_ID, ops);
      if (pend) {
         Operations.executeWork(operation);
      } else {
         Operations.executeAsJob(operation, false, Job.LONG, null);
      }
   }

   public static void loadConfig(boolean pend) {
      if (AtsLoadConfigArtifactsOperation.isLoaded()) {
         return;
      }
      reloadConfig(pend);
   }

   public static Set<Artifact> loadFromActions(Collection<? extends Artifact> actions) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(actions, 4, AtsRelationTypes.SmaToTask_Task,
         AtsRelationTypes.ActionToWorkflow_WorkFlow, AtsRelationTypes.TeamWorkflowToReview_Review);
   }

   public static Set<Artifact> loadFromTeamWorkflows(Collection<? extends Artifact> teams) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(teams, 3, AtsRelationTypes.SmaToTask_Task,
         AtsRelationTypes.TeamWorkflowToReview_Team, AtsRelationTypes.ActionToWorkflow_Action);
   }

}
