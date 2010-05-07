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

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

   public synchronized static IOperation getConfigLoadingOperation() throws OseeCoreException {
      if (atsTypeDataLoadedStarted == false) {
         atsTypeDataLoadedStarted = true;
         return new AtsLoadConfigArtifactsOperation();
      }
      return new EmptyOperation("ATS Bulk Loading", AtsPlugin.PLUGIN_ID);
   }

   public static void loadConfig(boolean pend) {
      if (AtsLoadConfigArtifactsOperation.isLoaded()) return;
      if (pend) {
         Operations.executeAndPend(new AtsLoadConfigArtifactsOperation(), false);
      } else {
         Operations.executeAsJob(new AtsLoadConfigArtifactsOperation(), false);
      }
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
