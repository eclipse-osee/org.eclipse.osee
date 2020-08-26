/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.internal;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteSMAStateUtil {

   /**
    * Return Percent Complete working ONLY the current state (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         if (AtsApiService.get().getWorkItemService().getTeams(artifact).size() == 1) {
            return getPercentCompleteSMAState(AtsApiService.get().getQueryServiceIde().getArtifact(
               AtsApiService.get().getWorkItemService().getFirstTeam(artifact)));
         } else {
            double percent = 0;
            int items = 0;
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
               if (!team.isCancelled()) {
                  percent +=
                     getPercentCompleteSMAState(AtsApiService.get().getQueryServiceIde().getArtifact(team));
                  items++;
               }
            }
            if (items > 0) {
               Double rollPercent = percent / items;
               return rollPercent.intValue();
            }
         }
         return 0;
      }
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getPercentCompleteSMAState(artifact, getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact, IStateToken state) {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getStateManager(artifact).getPercentComplete(state.getName());
      }
      return 0;
   }

   private static IAtsStateManager getStateManager(Artifact artifact) {
      return cast(artifact).getStateMgr();
   }

   private static AbstractWorkflowArtifact cast(Artifact artifact) {
      AbstractWorkflowArtifact art = null;
      if (artifact instanceof AbstractWorkflowArtifact) {
         art = (AbstractWorkflowArtifact) artifact;
      }
      return art;
   }

}
