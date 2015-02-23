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
package org.eclipse.osee.ats.impl.internal.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.workitem.AtsConfigObject;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class AgileTeam extends AtsConfigObject implements IAgileTeam {

   public AgileTeam(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public String getTypeName() {
      return "Agile Team";
   }

   @Override
   public List<Long> getAtsTeamUuids() {
      List<Long> uuids = new ArrayList<Long>();
      for (ArtifactReadable atsTeam : artifact.getRelated(AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         uuids.add(new Long(atsTeam.getLocalId()));
      }
      return uuids;
   }

   @Override
   public String getDescription() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Description, "");
   }

   @Override
   public long getBacklogUuid() {
      long backlogUuid = -1;
      try {
         ArtifactReadable backlogArt =
            artifact.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getAtMostOneOrNull();
         backlogUuid = backlogArt.getLocalId();
      } catch (Exception ex) {
         // do nothing
      }
      return backlogUuid;
   }
}
