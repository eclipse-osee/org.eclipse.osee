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

package org.eclipse.osee.ats.ide.workflow.sprint;

import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * @author Donald G. Dunne
 */
public class SprintArtifact extends CollectorArtifact implements IAgileSprint {

   public SprintArtifact(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType, AtsRelationTypes.AgileSprintToItem_AtsItem);
   }

   @Override
   public IAtsAction getParentAction() {
      return null;
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() {
      return null;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      return null;
   }

   @Override
   public boolean isActive() {
      return getCurrentStateType().isWorking();
   }

   @Override
   public long getTeamId() {
      long result = 0;
      try {
         ArtifactId agileTeam = AtsApiService.get().getRelationResolver().getRelatedOrSentinel((ArtifactId) this,
            AtsRelationTypes.AgileTeamToSprint_AgileTeam);
         if (agileTeam.isValid()) {
            result = agileTeam.getId();
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public List<Artifact> getMembers() {
      return AtsApiService.get().getSprintItemsCache().getMembers(this);
   }

   public RelationTypeSide getMembersRelationType() {
      return AtsRelationTypes.AgileSprintToItem_AtsItem;
   }

   @Override
   public StateType getCurrentStateType() {
      return StateType.valueOf(getSoleAttributeValue(AtsAttributeTypes.CurrentStateType));
   }

   @Override
   public BooleanState isParentAtsArtifactLoaded() {
      return BooleanState.UnSet;
   }

}
