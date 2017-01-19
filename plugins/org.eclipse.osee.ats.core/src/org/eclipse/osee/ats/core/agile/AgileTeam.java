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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileTeam extends AtsConfigObject implements IAgileTeam {

   public AgileTeam(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(logger, services, artifact);
   }

   @Override
   public String getTypeName() {
      return "Agile Team";
   }

   @Override
   public List<Long> getAtsTeamUuids() {
      List<Long> uuids = new ArrayList<>();
      for (ArtifactId atsTeam : services.getRelationResolver().getRelated(artifact,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         uuids.add(new Long(atsTeam.getId()));
      }
      return uuids;
   }

   @Override
   public String getDescription() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
   }

   @Override
   public long getBacklogUuid() {
      long backlogUuid = -1;
      try {
         ArtifactId backlogArt =
            services.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.AgileTeamToBacklog_Backlog);
         if (backlogArt != null) {
            backlogUuid = backlogArt.getId();
         }
      } catch (Exception ex) {
         // do nothing
      }
      return backlogUuid;
   }

}
