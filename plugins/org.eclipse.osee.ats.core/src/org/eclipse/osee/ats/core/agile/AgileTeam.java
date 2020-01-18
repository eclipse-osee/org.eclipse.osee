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
package org.eclipse.osee.ats.core.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
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
         for (IAgileSprint sprint : atsApi.getAgileService().getSprintsForTeam(artifact.getId())) {
            if (sprint.isInWork()) {
               sprintId = sprint.getStoreObject();
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return sprintId;
   }
}