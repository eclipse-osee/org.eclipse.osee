/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.MembersManager;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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
         AWorkbench.popupf("No Sprint set for artifact %s", artifact.toStringWithId());
         return null;
      }
      Collection<Artifact> sprints = getCollectors(artifact, false);
      SprintArtifact sprint = null;
      if (sprints.size() == 1) {
         sprint = (SprintArtifact) sprints.iterator().next();
      } else if (sprints.size() > 1) {
         AWorkbench.popup(String.format("Error Item [%s] belongs to %d Sprints and should only belong to one.",
            artifact.toStringWithId(), sprints.size()));
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
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileSprint;
   }

   @Override
   public String getMemberOrder(SprintArtifact sprintArt, Artifact member) {
      return AtsApiService.get().getSprintItemsCache().getMemberOrder(sprintArt, member);
   }

}
