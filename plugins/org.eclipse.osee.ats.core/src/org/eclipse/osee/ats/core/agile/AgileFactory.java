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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileProgram;
import org.eclipse.osee.ats.api.agile.IAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AgileFactory {

   private AgileFactory() {
      // Utilitiy class
   }

   public static IAgileTeam createAgileTeam(Log logger, AtsApi atsApi, JaxNewAgileTeam newTeam) {
      org.eclipse.osee.framework.core.data.ArtifactId userArt =
         atsApi.getQueryService().getArtifact((IAtsObject) atsApi.getUserService().getCurrentUser());

      ArtifactId agileTeamArt = atsApi.getQueryService().getArtifact(newTeam.getId());
      if (agileTeamArt == null) {

         IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Team");

         agileTeamArt = changes.createArtifact(AtsArtifactTypes.AgileTeam, newTeam.getName(), newTeam.getId());
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, true);
         ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsApi, userArt, changes);

         if (Strings.isNumeric(newTeam.getProgramId())) {
            ArtifactId programArt = atsApi.getQueryService().getArtifact(Long.valueOf(newTeam.getProgramId()));
            changes.addChild(programArt, agileTeamArt);
         } else {
            changes.addChild(topAgileFolder, agileTeamArt);
         }

         Set<ArtifactId> atsTeamArts = new HashSet<>();
         changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

         changes.execute();
      }
      return getAgileTeam(logger, atsApi, agileTeamArt);
   }

   public static IAgileTeam updateAgileTeam(Log logger, AtsApi atsApi, JaxAgileTeam team) {
      ArtifactId userArt = atsApi.getQueryService().getArtifact((IAtsObject) atsApi.getUserService().getCurrentUser());

      IAtsChangeSet changes = atsApi.createChangeSet("Update new Agile Team");

      ArtifactToken agileTeamArt = atsApi.getQueryService().getArtifact(team.getId());
      if (agileTeamArt == null) {
         throw new OseeStateException("Agile Team not found with Id [%s]", team.getIdString());
      }
      if (Strings.isValid(team.getName()) && !team.getName().equals(agileTeamArt.getName())) {
         changes.setName(agileTeamArt, team.getName());
      }
      if (Strings.isValid(team.getDescription()) && !team.getDescription().equals(
         atsApi.getAttributeResolver().getSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, ""))) {
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, team.getDescription());
      }
      changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, team.isActive());
      ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsApi, userArt, changes);
      if (topAgileFolder.notEqual(atsApi.getRelationResolver().getParent(agileTeamArt))) {
         changes.unrelateFromAll(CoreRelationTypes.DefaultHierarchical_Parent, agileTeamArt);
         changes.addChild(topAgileFolder, agileTeamArt);
      }

      Set<ArtifactId> atsTeamArts = new HashSet<>();
      for (long atsTeamId : team.getAtsTeamIds()) {
         ArtifactToken atsTeamArt = atsApi.getQueryService().getArtifact(atsTeamId);
         if (atsTeamArt != null && atsTeamArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            atsTeamArts.add(atsTeamArt);
         } else {
            throw new OseeArgumentException("ID %d is not a valid Ats Team Definition", atsTeamId);
         }
      }
      changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

      changes.execute();
      return getAgileTeam(logger, atsApi, agileTeamArt);
   }

   public static IAgileTeam getAgileTeam(Log logger, AtsApi atsApi, Object artifact) {
      IAgileTeam team = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         team = new AgileTeam(logger, atsApi, art);
      }
      return team;
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, AtsApi atsApi, long teamId, String name, String guid, Long id) {
      JaxAgileFeatureGroup feature = new JaxAgileFeatureGroup();
      feature.setName(name);
      feature.setId(id);
      feature.setTeamId(teamId);
      feature.setActive(true);
      return createAgileFeatureGroup(logger, atsApi, feature);
   }

   public static IAgileProgramFeature createAgileProgramFeature(Log logger, AtsApi atsApi, long programBacklogItemId, String name, String guid, Long id) {
      JaxAgileProgramFeature feature = new JaxAgileProgramFeature();
      feature.setName(name);
      feature.setId(id);
      feature.setProgramBacklogItemId(programBacklogItemId);
      feature.setActive(true);
      return createAgileProgramFeature(logger, atsApi, feature);
   }

   public static IAgileProgramFeature createAgileProgramFeature(Log logger, AtsApi atsApi, JaxAgileProgramFeature newProgramFeature) {
      IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Program Feature");

      ArtifactId programFeature = changes.createArtifact(AtsArtifactTypes.AgileProgramFeature,
         newProgramFeature.getName(), newProgramFeature.getId());
      changes.setSoleAttributeValue(programFeature, AtsAttributeTypes.Active, newProgramFeature.isActive());

      ArtifactId programBacklogItemArt =
         atsApi.getQueryService().getArtifact(newProgramFeature.getProgramBacklogItemId());
      changes.addChild(programBacklogItemArt, programFeature);

      changes.execute();
      return getAgileProgramFeature(logger, atsApi, programFeature);
   }

   private static IAgileProgramFeature getAgileProgramFeature(Log logger, AtsApi atsApi, ArtifactId artifact) {
      return new AgileProgramFeature(logger, atsApi, atsApi.getQueryService().getArtifact(artifact));
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, AtsApi atsApi, JaxAgileFeatureGroup newFeatureGroup) {
      ArtifactId userArt = atsApi.getQueryService().getArtifact((IAtsObject) atsApi.getUserService().getCurrentUser());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Feature Group");

      ArtifactId featureGroupArt =
         changes.createArtifact(AtsArtifactTypes.AgileFeatureGroup, newFeatureGroup.getName(), newFeatureGroup.getId());
      changes.setSoleAttributeValue(featureGroupArt, AtsAttributeTypes.Active, newFeatureGroup.isActive());

      ArtifactId featureGroupFolder =
         AgileFolders.getOrCreateTopFeatureGroupFolder(atsApi, newFeatureGroup.getTeamId(), userArt, changes);
      changes.addChild(featureGroupFolder, featureGroupArt);

      ArtifactId team = AgileFolders.getTeamFolder(atsApi, newFeatureGroup.getTeamId());
      changes.relate(team, AtsRelationTypes.AgileTeamToFeatureGroup_AgileFeatureGroup, featureGroupArt);

      changes.execute();
      return getAgileFeatureGroup(logger, atsApi, featureGroupArt);
   }

   public static IAgileFeatureGroup getAgileFeatureGroup(Log logger, AtsApi atsApi, ArtifactId artifact) {
      return new AgileFeatureGroup(logger, atsApi, atsApi.getQueryService().getArtifact(artifact));
   }

   public static IAgileSprint createAgileSprint(Log logger, AtsApi atsApi, long teamId, String name, Long id) {

      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create new Agile Sprint", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken sprintArt = changes.createArtifact(AtsArtifactTypes.AgileSprint, name, id);
      IAgileSprint sprint = atsApi.getWorkItemService().getAgileSprint(sprintArt);

      atsApi.getActionService().setAtsId(sprint, AtsApiService.get().getTeamDefinitionService().getTopTeamDefinition(),
         null, changes);

      WorkDefinition workDefinition =
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Sprint);
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(sprint, workDefinition, changes);

      // Initialize state machine
      atsApi.getActionService().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsApi.getUserService().getCurrentUser(), workDefinition, changes);

      changes.add(sprintArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, teamId);
      ArtifactId agileSprintFolderArt = AgileFolders.getOrCreateTopSprintFolder(atsApi, teamId, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.DefaultHierarchical_Child, sprintArt);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToSprint_Sprint, sprintArt);

      changes.execute();
      return getAgileSprint(logger, atsApi, sprintArt);
   }

   public static IAgileSprint getAgileSprint(Log logger, AtsApi atsApi, ArtifactId artifact) {
      ArtifactToken artifact2 = atsApi.getQueryService().getArtifact(artifact);
      return new AgileSprint(logger, atsApi, artifact2);
   }

   public static IAgileBacklog createAgileBacklog(Log logger, AtsApi atsApi, long teamId, String name, Long id) {

      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create new Agile Backlog", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken backlogArt = changes.createArtifact(AtsArtifactTypes.AgileBacklog, name, id);
      IAgileBacklog backlog = atsApi.getWorkItemService().getAgileBacklog(backlogArt);

      atsApi.getActionService().setAtsId(backlog, AtsApiService.get().getTeamDefinitionService().getTopTeamDefinition(),
         null, changes);

      WorkDefinition workDefinition =
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(backlog, workDefinition, changes);

      // Initialize state machine
      atsApi.getActionService().initializeNewStateMachine(backlog, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsApi.getUserService().getCurrentUser(), workDefinition, changes);

      changes.add(backlogArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, teamId);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog, backlogArt);
      changes.relate(teamFolder, CoreRelationTypes.DefaultHierarchical_Child, backlogArt);

      changes.execute();
      return getAgileBacklog(logger, atsApi, backlogArt);
   }

   public static IAgileBacklog getAgileBacklog(Log logger, AtsApi atsApi, Object artifact) {
      return new AgileBacklog(logger, atsApi, (ArtifactToken) artifact);
   }

   public static IAgileProgram getAgileProgram(ArtifactId progArt) {
      return null;
   }

   public static JaxAgileTeam createJaxTeam(IAgileTeam agileTeam) {
      JaxAgileTeam created = new JaxAgileTeam();
      created.setName(agileTeam.getName());
      created.setId(agileTeam.getId());
      created.setActive(agileTeam.isActive());
      created.getAtsTeamIds().addAll(agileTeam.getAtsTeamIds());
      created.setBacklogId(agileTeam.getBacklogId());
      created.setSprintId(agileTeam.getSprintId());
      created.setDescription(agileTeam.getDescription());
      return created;
   }

   public static JaxAgileFeatureGroup createJaxAgileFeatureGroup(IAgileFeatureGroup agileFeatureGroup) {
      JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
      newGroup.setName(agileFeatureGroup.getName());
      newGroup.setId(agileFeatureGroup.getId());
      newGroup.setActive(agileFeatureGroup.isActive());
      newGroup.setTeamId(agileFeatureGroup.getTeamId());
      return newGroup;
   }

}
