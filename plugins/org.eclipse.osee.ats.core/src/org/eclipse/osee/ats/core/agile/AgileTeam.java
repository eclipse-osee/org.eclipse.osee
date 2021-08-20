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

package org.eclipse.osee.ats.core.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileTeam extends AtsConfigObject implements IAgileTeam {

   public AgileTeam(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.AgileTeam);
   }

   @Override
   public List<Long> getAtsTeamIds() {
      List<Long> ids = new ArrayList<>();
      for (ArtifactId atsTeam : atsApi.getRelationResolver().getRelated(artifact,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         ids.add(atsTeam.getId());
      }
      return ids;
   }

   @Override
   public ArtifactId getBacklogId() {
      ArtifactId backlogId = ArtifactId.SENTINEL;
      try {
         backlogId =
            atsApi.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.AgileTeamToBacklog_Backlog);
      } catch (Exception ex) {
         // do nothing
      }
      return backlogId;
   }

   @Override
   public ArtifactId getSprintId() {
      ArtifactId sprintId = ArtifactId.SENTINEL;
      try {
         for (ArtifactToken sprint : atsApi.getRelationResolver().getRelated(artifact,
            AtsRelationTypes.AgileTeamToSprint_Sprint)) {
            if (atsApi.getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.CurrentStateType,
               "").equals(StateType.Working.name())) {
               sprintId = sprint;
               break;
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return sprintId;
   }
}