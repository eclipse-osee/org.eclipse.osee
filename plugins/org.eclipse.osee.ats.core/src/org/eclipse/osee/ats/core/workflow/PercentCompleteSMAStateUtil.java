/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.WorkflowManagerCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteSMAStateUtil {

   /**
    * Return Percent Complete working ONLY the current state (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         if (ActionManagerCore.getTeams(artifact).size() == 1) {
            return getPercentCompleteSMAState(ActionManagerCore.getFirstTeam(artifact));
         } else {
            double percent = 0;
            int items = 0;
            for (TeamWorkFlowArtifact team : ActionManagerCore.getTeams(artifact)) {
               if (!team.isCancelled()) {
                  percent += getPercentCompleteSMAState(team);
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
         return getPercentCompleteSMAState(artifact, WorkflowManagerCore.getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact, IWorkPage state) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return WorkflowManagerCore.getStateManager(artifact).getPercentComplete(state);
      }
      return 0;
   }

}
