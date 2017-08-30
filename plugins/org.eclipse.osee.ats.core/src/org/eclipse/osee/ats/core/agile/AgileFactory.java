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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AgileFactory {

   private AgileFactory() {
      // Utilitiy class
   }

   public static IAgileTeam createAgileTeam(Log logger, IAtsServices services, JaxNewAgileTeam newTeam) {
      org.eclipse.osee.framework.core.data.ArtifactId userArt =
         services.getArtifact(services.getUserService().getCurrentUser());

      ArtifactId agileTeamArt = services.getArtifact(newTeam.getUuid());
      if (agileTeamArt == null) {

         IAtsChangeSet changes = services.createChangeSet("Create new Agile Team");

         agileTeamArt =
            changes.createArtifact(AtsArtifactTypes.AgileTeam, newTeam.getName(), GUID.create(), newTeam.getUuid());
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, true);
         ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(services, userArt, changes);
         if (topAgileFolder.notEqual(services.getRelationResolver().getParent(agileTeamArt))) {
            changes.unrelateFromAll(CoreRelationTypes.Default_Hierarchical__Parent, agileTeamArt);
            changes.addChild(topAgileFolder, agileTeamArt);
         }

         Set<ArtifactId> atsTeamArts = new HashSet<>();
         changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

         changes.execute();
      }
      return getAgileTeam(logger, services, agileTeamArt);
   }

   public static IAgileTeam updateAgileTeam(Log logger, IAtsServices services, JaxAgileTeam team) {
      ArtifactId userArt = services.getArtifact(services.getUserService().getCurrentUser());

      IAtsChangeSet changes = services.createChangeSet("Update new Agile Team");

      ArtifactToken agileTeamArt = services.getArtifact(team.getUuid());
      if (agileTeamArt == null) {
         throw new OseeStateException("Agile Team not found with Uuid [%d]", team.getUuid());
      }
      if (Strings.isValid(team.getName()) && !team.getName().equals(agileTeamArt.getName())) {
         changes.setName(agileTeamArt, team.getName());
      }
      if (Strings.isValid(team.getDescription()) && !team.getDescription().equals(
         services.getAttributeResolver().getSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, ""))) {
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, team.getDescription());
      }
      changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, team.isActive());
      ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(services, userArt, changes);
      if (topAgileFolder.notEqual(services.getRelationResolver().getParent(agileTeamArt))) {
         changes.unrelateFromAll(CoreRelationTypes.Default_Hierarchical__Parent, agileTeamArt);
         changes.addChild(topAgileFolder, agileTeamArt);
      }

      Set<ArtifactId> atsTeamArts = new HashSet<>();
      for (long atsTeamUuid : team.getAtsTeamUuids()) {
         ArtifactId atsTeamArt = services.getArtifact(atsTeamUuid);
         if (atsTeamArt != null && services.getStoreService().isOfType(atsTeamArt, AtsArtifactTypes.TeamDefinition)) {
            atsTeamArts.add(atsTeamArt);
         } else {
            throw new OseeArgumentException("UUID %d is not a valid Ats Team Definition", atsTeamUuid);
         }
      }
      changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

      changes.execute();
      return getAgileTeam(logger, services, agileTeamArt);
   }

   public static IAgileTeam getAgileTeam(Log logger, IAtsServices services, Object artifact) {
      return new AgileTeam(logger, services, (ArtifactToken) artifact);
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, IAtsServices services, long teamUuid, String name, String guid, Long uuid) {
      JaxAgileFeatureGroup feature = new JaxAgileFeatureGroup();
      feature.setName(name);
      feature.setUuid(uuid);
      feature.setTeamUuid(teamUuid);
      feature.setActive(true);
      return createAgileFeatureGroup(logger, services, feature);
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, IAtsServices services, JaxAgileFeatureGroup newFeatureGroup) {
      ArtifactId userArt = services.getArtifact(services.getUserService().getCurrentUser());

      IAtsChangeSet changes = services.createChangeSet("Create new Agile Feature Group");

      ArtifactId featureGroupArt = changes.createArtifact(AtsArtifactTypes.AgileFeatureGroup, newFeatureGroup.getName(),
         GUID.create(), newFeatureGroup.getUuid());
      changes.setSoleAttributeValue(featureGroupArt, AtsAttributeTypes.Active, newFeatureGroup.isActive());

      ArtifactId featureGroupFolder =
         AgileFolders.getOrCreateTopFeatureGroupFolder(services, newFeatureGroup.getTeamUuid(), userArt, changes);
      changes.addChild(featureGroupFolder, featureGroupArt);

      ArtifactId team = AgileFolders.getTeamFolder(services, newFeatureGroup.getTeamUuid());
      changes.relate(team, AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup, featureGroupArt);

      changes.execute();
      return getAgileFeatureGroup(logger, services, featureGroupArt);
   }

   public static IAgileFeatureGroup getAgileFeatureGroup(Log logger, IAtsServices services, Object artifact) {
      return new AgileFeatureGroup(logger, services, (ArtifactToken) artifact);
   }

   public static IAgileSprint createAgileSprint(Log logger, IAtsServices services, long teamUuid, String name, String guid, Long uuid) {

      IAtsChangeSet changes =
         services.getStoreService().createAtsChangeSet("Create new Agile Sprint", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken sprintArt = changes.createArtifact(AtsArtifactTypes.AgileSprint, name, guid, uuid);
      IAgileSprint sprint = services.getWorkItemFactory().getAgileSprint(sprintArt);

      services.getActionFactory().setAtsId(sprint, TeamDefinitions.getTopTeamDefinition(services.getQueryService()),
         changes);

      // Initialize state machine
      services.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), services.getUserService().getCurrentUser(), changes);

      changes.add(sprintArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(services, teamUuid);
      ArtifactId agileSprintFolderArt = AgileFolders.getOrCreateTopSprintFolder(services, teamUuid, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.Default_Hierarchical__Child, sprintArt);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToSprint_Sprint, sprintArt);

      changes.execute();
      return getAgileSprint(logger, services, sprintArt);
   }

   public static IAgileSprint getAgileSprint(Log logger, IAtsServices services, Object artifact) {
      return new AgileSprint(logger, services, (ArtifactToken) artifact);
   }

   public static IAgileBacklog createAgileBacklog(Log logger, IAtsServices services, long teamUuid, String name, String guid, Long uuid) {

      IAtsChangeSet changes =
         services.getStoreService().createAtsChangeSet("Create new Agile Backlog", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken backlogArt = changes.createArtifact(AtsArtifactTypes.Goal, name, guid, uuid);
      IAgileBacklog sprint = services.getWorkItemFactory().getAgileBacklog(backlogArt);

      services.getActionFactory().setAtsId(sprint, TeamDefinitions.getTopTeamDefinition(services.getQueryService()),
         changes);

      // Initialize state machine
      services.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), services.getUserService().getCurrentUser(), changes);

      changes.add(backlogArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(services, teamUuid);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog, backlogArt);
      changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, backlogArt);

      changes.execute();
      return getAgileBacklog(logger, services, backlogArt);
   }

   public static IAgileBacklog getAgileBacklog(Log logger, IAtsServices services, Object artifact) {
      return new AgileBacklog(logger, services, (ArtifactToken) artifact);
   }

}
