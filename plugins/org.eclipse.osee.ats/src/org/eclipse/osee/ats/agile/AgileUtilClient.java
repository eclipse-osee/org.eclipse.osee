/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.agile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AgileUtilClient {

   private AgileUtilClient() {
      // Utility Class
   }

   public static Artifact getRelatedBacklogArt(AbstractWorkflowArtifact awa) {
      Artifact relatedBacklogArt = null;
      for (Artifact goalArt : awa.getRelatedArtifacts(AtsRelationTypes.Goal_Goal)) {
         if (isBacklog(goalArt)) {
            relatedBacklogArt = goalArt;
         }
      }
      return relatedBacklogArt;
   }

   public static boolean isBacklog(Artifact backlogArt) {
      return backlogArt.getRelatedArtifactsCount(AtsRelationTypes.AgileTeamToBacklog_AgileTeam) == 1;
   }

   public static boolean isSprint(Artifact artifact) {
      return artifact.isOfType(AtsArtifactTypes.AgileSprint);
   }

   public static Collection<Artifact> getRelatedSprints(Artifact awa) {
      Set<Artifact> sprints = new HashSet<>();
      for (Artifact sprintArt : awa.getRelatedArtifacts(AtsRelationTypes.AgileSprintToItem_Sprint)) {
         sprints.add(sprintArt);
      }
      return sprints;
   }

}
