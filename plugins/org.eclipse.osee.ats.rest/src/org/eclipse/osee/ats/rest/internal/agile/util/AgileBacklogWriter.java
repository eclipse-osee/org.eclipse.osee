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
package org.eclipse.osee.ats.rest.internal.agile.util;

import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AgileBacklogWriter {

   private final IAtsServer atsServer;
   private final IAgileService agileService;
   private final JaxAgileBacklog updatedBacklog;

   public AgileBacklogWriter(IAtsServer atsServer, IAgileService agileService, JaxAgileBacklog updatedBacklog) {
      this.atsServer = atsServer;
      this.agileService = agileService;
      this.updatedBacklog = updatedBacklog;
   }

   public IAgileBacklog write() {
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet("Update Agile Backlog", AtsCoreUsers.SYSTEM_USER);

      // Validate backlog exists
      IAgileBacklog currentBacklog = agileService.getAgileBacklog(updatedBacklog.getUuid());
      if (currentBacklog == null) {
         throw new OseeArgumentException("No Backlog found with UUID %d", updatedBacklog.getUuid());
      }
      if (currentBacklog.getTeamUuid() != updatedBacklog.getTeamUuid()) {

         // If teamUuid is empty, unrelate form backlog
         if (currentBacklog.getTeamUuid() == AgileUtil.EMPTY_VALUE) {
            IAgileTeam team = agileService.getAgileTeam(currentBacklog.getTeamUuid());
            if (team != null) {
               changes.unrelateAll(team, AtsRelationTypes.AgileTeamToBacklog_Backlog);
               changes.add(team);
            }
         }

         // Else validate and relate new team
         else {
            ArtifactReadable updateBacklogArt = atsServer.getArtifact(updatedBacklog.getUuid());
            IAgileTeam updatedTeam = agileService.getAgileTeam(updatedBacklog.getTeamUuid());
            ArtifactReadable updatedTeamArt = (ArtifactReadable) updatedTeam.getStoreObject();
            if (!updateBacklogArt.isOfType(AtsArtifactTypes.Goal)) {
               throw new OseeArgumentException("Backlog UUID %d not valid type", updatedBacklog.getUuid());
            } else if (updateBacklogArt.getRelatedCount(AtsRelationTypes.AgileTeamToBacklog_AgileTeam) > 0) {
               ArtifactReadable currentTeamArt =
                  updateBacklogArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_AgileTeam).getExactlyOne();
               if (!updatedTeamArt.equals(currentTeamArt)) {
                  changes.unrelate(currentTeamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog, updateBacklogArt);
                  changes.add(currentTeamArt);
               }
            }
            changes.relate(updatedTeamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog, updateBacklogArt);
            if (!updatedTeamArt.areRelated(CoreRelationTypes.Default_Hierarchical__Child, updateBacklogArt)) {
               if (updateBacklogArt.getParent() != null) {
                  changes.unrelate(updateBacklogArt.getParent(), CoreRelationTypes.Default_Hierarchical__Child,
                     updateBacklogArt);
               }
               changes.relate(updatedTeamArt, CoreRelationTypes.Default_Hierarchical__Child, updateBacklogArt);
            }
            changes.add(updatedTeamArt);
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return agileService.getAgileBacklog(updatedBacklog.getUuid());
   }
}
