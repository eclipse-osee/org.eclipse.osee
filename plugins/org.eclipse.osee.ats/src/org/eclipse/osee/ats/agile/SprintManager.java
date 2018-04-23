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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.goal.MembersManager;
import org.eclipse.osee.ats.workflow.sprint.SprintArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class SprintManager extends MembersManager<SprintArtifact> {

   /**
    * change sprint, error if member of two sprints
    */
   public SprintArtifact promptChangeSprintOrder(Artifact artifact) {
      if (!isHasCollector(artifact)) {
         AWorkbench.popup(String.format("No Sprint set for artifact [%s]", artifact));
         return null;
      }
      Collection<Artifact> sprints = getCollectors(artifact, false);
      SprintArtifact sprint = null;
      if (sprints.size() == 1) {
         sprint = (SprintArtifact) sprints.iterator().next();
      } else if (sprints.size() > 1) {
         AWorkbench.popup(
            String.format("Error Item [%s] belongs to %d Sprints and should only belong to one.", artifact));
         return null;
      }
      return promptChangeMemberOrder(sprint, artifact);
   }

   @Override
   public RelationTypeSide getMembersRelationTypeSide() {
      return AtsRelationTypes.AgileSprintToItem_AtsItem;
   }

   @Override
   public String getItemName() {
      return "Sprint";
   }

   @Override
   public IArtifactType getArtifactType() {
      return AtsArtifactTypes.AgileSprint;
   }

   @Override
   public String getMemberOrder(SprintArtifact sprintArt, Artifact member) {
      return AtsClientService.get().getSprintItemsCache().getMemberOrder(sprintArt, member);
   }

}
