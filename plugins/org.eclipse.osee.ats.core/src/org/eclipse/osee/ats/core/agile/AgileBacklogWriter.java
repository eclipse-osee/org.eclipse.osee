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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AgileBacklogWriter {

   private final AtsApi atsApi;
   private final IAgileService agileService;
   private final JaxAgileBacklog updatedBacklog;

   public AgileBacklogWriter(AtsApi atsApi, IAgileService agileService, JaxAgileBacklog updatedBacklog) {
      this.atsApi = atsApi;
      this.agileService = agileService;
      this.updatedBacklog = updatedBacklog;
   }

   public IAgileBacklog write() {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Update Agile Backlog", AtsCoreUsers.SYSTEM_USER);

      // Validate backlog exists
      IAgileBacklog currentBacklog = agileService.getAgileBacklog(updatedBacklog.getId());
      if (currentBacklog == null) {
         throw new OseeArgumentException("No Backlog found with ID %d", updatedBacklog.getId());
      }
      if (currentBacklog.getTeamId() != updatedBacklog.getTeamId()) {

         // If teamId is empty, unrelate form backlog
         if (currentBacklog.getTeamId() == IAgileService.EMPTY_VALUE) {
            IAgileTeam team = agileService.getAgileTeam(currentBacklog.getTeamId());
            if (team != null) {
               changes.unrelateAll(team, AtsRelationTypes.AgileTeamToBacklog_Backlog);
               changes.add(team);
            }
         }

         // Else validate and relate new team
         else {
            ArtifactToken updateBacklogArt = atsApi.getQueryService().getArtifact(updatedBacklog.getId());
            IAgileTeam updatedTeam = agileService.getAgileTeam(updatedBacklog.getTeamId());
            ArtifactToken updatedTeamArt = updatedTeam.getStoreObject();
            if (!atsApi.getStoreService().isOfType(updateBacklogArt, AtsArtifactTypes.Goal)) {
               throw new OseeArgumentException("Backlog ID %d not valid type", updatedBacklog.getId());
            } else if (atsApi.getRelationResolver().getRelatedCount(updateBacklogArt,
               AtsRelationTypes.AgileTeamToBacklog_AgileTeam) > 0) {
               ArtifactToken currentTeamArt = atsApi.getRelationResolver().getRelatedOrNull(updateBacklogArt,
                  AtsRelationTypes.AgileTeamToBacklog_AgileTeam);
               if (updatedTeamArt.notEqual(currentTeamArt)) {
                  changes.unrelate(currentTeamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog, updateBacklogArt);
                  changes.add(currentTeamArt);
               }
            }
            changes.relate(updatedTeamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog, updateBacklogArt);
            if (!atsApi.getRelationResolver().areRelated(updatedTeamArt, CoreRelationTypes.Default_Hierarchical__Child,
               updateBacklogArt)) {
               if (atsApi.getRelationResolver().getParent(updateBacklogArt) != null) {
                  changes.unrelate(atsApi.getRelationResolver().getParent(updateBacklogArt),
                     CoreRelationTypes.Default_Hierarchical__Child, updateBacklogArt);
               }
               changes.relate(updatedTeamArt, CoreRelationTypes.Default_Hierarchical__Child, updateBacklogArt);
            }
            changes.add(updatedTeamArt);
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return agileService.getAgileBacklog(updatedBacklog.getId());
   }
}
