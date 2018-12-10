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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileWriterResult;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileProgram;
import org.eclipse.osee.ats.api.agile.IAgileProgramBacklog;
import org.eclipse.osee.ats.api.agile.IAgileProgramBacklogItem;
import org.eclipse.osee.ats.api.agile.IAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.api.agile.IAgileStory;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramBacklogItem;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.JaxAgileStory;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.agile.operations.AgileProgramOperations;
import org.eclipse.osee.ats.core.agile.operations.SprintBurndownOperations;
import org.eclipse.osee.ats.core.agile.operations.SprintBurnupOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AgileService implements IAgileService {

   private final Log logger;
   private final AtsApi atsApi;

   public AgileService(Log logger, AtsApi atsApi) {
      this.logger = logger;
      this.atsApi = atsApi;
   }

   @Override
   public IAgileTeam getAgileTeamById(ArtifactId agileTeamId) {
      AgileTeam program = null;
      if (agileTeamId instanceof AgileTeam) {
         program = (AgileTeam) agileTeamId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(agileTeamId);
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AgileTeam)) {
            program = new AgileTeam(atsApi.getLogger(), atsApi, art);
         }
      }
      return program;
   }

   @Override
   public IAgileFeatureGroup getAgileFeatureGroupById(ArtifactId agileFeatureGroupId) {
      AgileFeatureGroup agileFeatureGroup = null;
      if (agileFeatureGroupId instanceof AgileFeatureGroup) {
         agileFeatureGroup = (AgileFeatureGroup) agileFeatureGroupId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(agileFeatureGroupId);
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AgileFeatureGroup)) {
            agileFeatureGroup = new AgileFeatureGroup(atsApi.getLogger(), atsApi, art);
         }
      }
      return agileFeatureGroup;
   }

   /********************************
    ** Agile Program
    ***********************************/
   @Override
   public IAgileProgram getAgileProgram(long programId) {
      IAgileProgram program = null;
      ArtifactId progArt = atsApi.getQueryService().getArtifact(programId);
      if (progArt != null) {
         program = AgileProgram.construct(progArt, atsApi);
      }
      return program;
   }

   /********************************
    ** Agile Program Feature
    ***********************************/
   @Override
   public IAgileProgramFeature getAgileProgramFeature(long programFeatureId) {
      IAgileProgramFeature programFeature = null;
      ArtifactId progArt = atsApi.getQueryService().getArtifact(programFeatureId);
      if (progArt != null) {
         programFeature = AgileProgramFeature.construct(progArt, atsApi);
      }
      return programFeature;
   }

   @Override
   public IAgileProgramFeature createAgileProgramFeature(long teamId, String name, String guid, Long id) {
      return AgileFactory.createAgileProgramFeature(logger, atsApi, teamId, name, guid, id);
   }

   /********************************
    ** Agile Program Backlog
    ***********************************/

   @Override
   public IAgileProgramBacklog getAgileProgramBacklog(IAgileProgram program) {
      IAgileProgramBacklog programBacklog = null;
      ArtifactId programBacklogArt = getAgileProgramBacklogArt(program);
      if (programBacklogArt != null) {
         programBacklog = AgileProgramBacklog.construct(programBacklogArt, atsApi);
      }
      return programBacklog;
   }

   @Override
   public ArtifactToken getAgileProgramBacklogArt(IAgileProgram program) {
      ArtifactToken programBacklogArt = null;
      ArtifactId programArt = atsApi.getQueryService().getArtifact(program.getId());
      if (programArt != null) {
         for (ArtifactToken child : atsApi.getRelationResolver().getChildren(programArt)) {
            if (atsApi.getStoreService().isOfType(child, AtsArtifactTypes.AgileProgramBacklog)) {
               programBacklogArt = child;
               break;
            }
         }
      }
      return programBacklogArt;
   }

   /********************************
    ** Agile Program Backlog Item
    ***********************************/
   @Override
   public IAgileProgramBacklogItem getAgileProgramBacklogItem(long programBacklogItemId) {
      IAgileProgramBacklogItem programBacklogItem = null;
      ArtifactId progBackItemArt = atsApi.getQueryService().getArtifact(programBacklogItemId);
      if (progBackItemArt != null) {
         programBacklogItem = AgileProgramBacklogItem.construct(progBackItemArt, atsApi);
      }
      return programBacklogItem;
   }

   /********************************
    ** Agile Team
    ***********************************/
   @Override
   public IAgileTeam getAgileTeam(ArtifactId artifact) {
      return AgileFactory.getAgileTeam(logger, atsApi, artifact);
   }

   @Override
   public IAgileTeam getAgileTeam(long id) {
      IAgileTeam team = null;
      ArtifactId teamArt = atsApi.getQueryService().getArtifact(id);
      if (teamArt != null) {
         team = getAgileTeam(teamArt);
      }
      return team;
   }

   @Override
   public IAgileTeam getAgileTeamById(long teamId) {
      IAgileTeam team = null;
      ArtifactId artifact = atsApi.getQueryService().getArtifact(teamId);
      if (artifact != null) {
         team = getAgileTeam(artifact);
      }
      return team;
   }

   @Override
   public IAgileTeam createAgileTeam(JaxNewAgileTeam newTeam) {
      return AgileFactory.createAgileTeam(logger, atsApi, newTeam);
   }

   @Override
   public IAgileTeam updateAgileTeam(JaxAgileTeam team) {
      return AgileFactory.updateAgileTeam(logger, atsApi, team);
   }

   @Override
   public void deleteAgileTeam(long id) {
      ArtifactToken team = atsApi.getQueryService().getArtifact(id);
      if (!atsApi.getStoreService().isOfType(team, AtsArtifactTypes.AgileTeam)) {
         throw new OseeArgumentException("ID %d is not a valid Agile Team", id);
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Delete Agile Team");
      deleteRecurse(atsApi.getRelationResolver().getChildren(team), changes);
      changes.deleteArtifact(team);
      changes.execute();
   }

   private void deleteRecurse(Collection<ArtifactToken> resultSet, IAtsChangeSet changes) {
      Iterator<ArtifactToken> iterator = resultSet.iterator();
      while (iterator.hasNext()) {
         ArtifactId art = iterator.next();
         deleteRecurse(atsApi.getRelationResolver().getChildren(art), changes);
         changes.deleteArtifact(art);
      }
   }

   @Override
   public Collection<IAgileTeam> getTeams() {
      List<IAgileTeam> teams = new ArrayList<>();
      for (ArtifactId teamArt : atsApi.getQueryService().createQuery(AtsArtifactTypes.AgileTeam).getArtifacts()) {
         teams.add(getAgileTeam(teamArt));
      }
      return teams;
   }

   @Override
   public AttributeTypeToken getAgileTeamPointsAttributeType(IAgileTeam team) {
      AttributeTypeToken type = AtsAttributeTypes.Points;
      String attrTypeName =
         atsApi.getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.PointsAttributeType, null);
      if (Strings.isValid(attrTypeName)) {
         type = getTypeFromName(attrTypeName);
      }
      return type;
   }

   private AttributeTypeToken getTypeFromName(String attrTypeName) {
      AttributeTypeToken type = null;
      for (AttributeTypeToken attrType : atsApi.getStoreService().getAttributeTypes()) {
         if (attrType.getName().equals(attrTypeName)) {
            type = attrType;
            break;
         }
      }
      if (type == null) {
         throw new OseeCoreException("Invalid attribute type name provided: %s", attrTypeName);
      }
      return type;
   }

   @Override
   public IAgileTeam getAgileTeam(IAtsTeamDefinition teamDef) {
      IAgileTeam aTeam = null;
      ArtifactId aTeamArt =
         atsApi.getRelationResolver().getRelatedOrNull(teamDef, AtsRelationTypes.AgileTeamToAtsTeam_AgileTeam);
      if (aTeamArt != null) {
         aTeam = atsApi.getAgileService().getAgileTeam(aTeamArt);
      }
      return aTeam;
   }

   @Override
   public IAgileTeam getAgileTeam(IAgileSprint sprint) {
      IAgileTeam aTeam = null;
      ArtifactId aTeamArt =
         atsApi.getRelationResolver().getRelatedOrNull(sprint, AtsRelationTypes.AgileTeamToSprint_AgileTeam);
      if (aTeamArt != null) {
         aTeam = atsApi.getAgileService().getAgileTeam(aTeamArt);
      }
      return aTeam;
   }

   @Override
   public IAgileTeam getAgileTeamByName(String agileTeamName) {
      IAgileTeam aTeam = null;
      ArtifactId aTeamArt =
         atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.AgileTeam, agileTeamName);
      if (aTeamArt.isValid()) {
         aTeam = atsApi.getAgileService().getAgileTeam(aTeamArt);
      }
      return aTeam;
   }

   @Override
   public Set<IAtsUser> getTeamMebers(IAgileTeam agileTeam) {
      Set<IAtsUser> activeMembers = new HashSet<>();
      // add users related to AgileTeam
      for (ArtifactToken user : atsApi.getRelationResolver().getRelated(agileTeam, CoreRelationTypes.Users_User)) {
         activeMembers.add(atsApi.getUserService().getUserByArtifactId(user));
      }
      // add lead and members related to AtsTeam
      for (ArtifactToken atsTeam : atsApi.getRelationResolver().getRelated(agileTeam,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {

         for (ArtifactToken user : atsApi.getRelationResolver().getRelated(atsTeam,
            AtsRelationTypes.TeamMember_Member)) {
            if (atsApi.getAttributeResolver().getSoleAttributeValue(user, CoreAttributeTypes.Active, true)) {
               activeMembers.add(atsApi.getUserService().getUserByArtifactId(user));
            }
         }

         for (ArtifactToken user : atsApi.getRelationResolver().getRelated(atsTeam, AtsRelationTypes.TeamLead_Lead)) {
            if (atsApi.getAttributeResolver().getSoleAttributeValue(user, CoreAttributeTypes.Active, true)) {
               activeMembers.add(atsApi.getUserService().getUserByArtifactId(user));
            }
         }
      }
      return activeMembers;
   }

   /********************************
    ** Agile Feature Group
    ***********************************/
   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact) {
      return AgileFactory.getAgileFeatureGroup(logger, atsApi, artifact);
   }

   @Override
   public void deleteAgileFeatureGroup(long id) {
      ArtifactId featureGroup = atsApi.getQueryService().getArtifact(id);
      if (!atsApi.getStoreService().isOfType(featureGroup, AtsArtifactTypes.AgileFeatureGroup)) {
         throw new OseeArgumentException("ID %d is not a valid Agile Feature Group", id);
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Delete Agile Feature Group");
      changes.deleteArtifact(featureGroup);
      changes.execute();
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(long teamId, String name, String guid, Long id) {
      return AgileFactory.createAgileFeatureGroup(logger, atsApi, teamId, name, guid, id);
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup) {
      return AgileFactory.createAgileFeatureGroup(logger, atsApi, newFeatureGroup);
   }

   @Override
   public Collection<IAgileFeatureGroup> getAgileFeatureGroups(IAgileTeam team) {
      List<IAgileFeatureGroup> groups = new LinkedList<>();
      ArtifactId artifact = team.getStoreObject();
      for (ArtifactId groupArt : atsApi.getRelationResolver().getRelated(artifact,
         AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup)) {
         groups.add(atsApi.getAgileService().getAgileFeatureGroup(groupArt));
      }
      return groups;
   }

   @Override
   public IAgileBacklog getBacklogForTeam(long teamId) {
      IAgileBacklog backlog = null;
      ArtifactId teamArt = atsApi.getQueryService().getArtifact(teamId);
      ArtifactId backlogArt =
         atsApi.getRelationResolver().getRelatedOrNull(teamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog);
      if (backlogArt != null) {
         backlog = getAgileBacklog(backlogArt);
      }
      return backlog;
   }

   /********************************
    ** Agile Sprint
    ***********************************/
   @Override
   public IAgileSprint getAgileSprint(ArtifactId artifact) {
      return AgileFactory.getAgileSprint(logger, atsApi, artifact);
   }

   @Override
   public IAgileSprint getAgileSprint(long id) {
      return getAgileSprint(ArtifactId.valueOf(id));
   }

   @Override
   public IAgileSprint createAgileSprint(long teamId, String name, Long id) {
      return AgileFactory.createAgileSprint(logger, atsApi, teamId, name, id);
   }

   @Override
   public Collection<IAgileSprint> getSprintsForTeam(long teamId) {
      List<IAgileSprint> sprints = new ArrayList<>();
      ArtifactId team = atsApi.getQueryService().getArtifact(teamId);
      for (ArtifactId sprintArt : atsApi.getRelationResolver().getRelated(team,
         AtsRelationTypes.AgileTeamToSprint_Sprint)) {
         sprints.add(getAgileSprint(sprintArt));
      }
      return sprints;
   }

   @Override
   public Collection<IAgileSprint> getAgileSprints(IAgileTeam team) {
      List<IAgileSprint> sprints = new LinkedList<>();
      ArtifactId artifact = team.getStoreObject();
      for (ArtifactToken sprintArt : atsApi.getRelationResolver().getRelated(artifact,
         AtsRelationTypes.AgileTeamToSprint_Sprint)) {
         sprints.add(atsApi.getWorkItemService().getAgileSprint(sprintArt));
      }
      return sprints;
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public IAgileBacklog getAgileBacklog(ArtifactId artifact) {
      return AgileFactory.getAgileBacklog(logger, atsApi, artifact);
   }

   @Override
   public IAgileBacklog getAgileBacklog(IAgileTeam team) {
      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, team.getId());
      if (teamFolder == null) {
         return null;
      }
      ArtifactToken backlogArt =
         atsApi.getRelationResolver().getRelatedOrNull(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog);
      if (backlogArt == null) {
         return null;
      }
      return new AgileBacklog(logger, atsApi, backlogArt);
   }

   @Override
   public IAgileBacklog getAgileBacklog(long id) {
      IAgileBacklog backlog = null;
      ArtifactId teamArt = atsApi.getQueryService().getArtifact(id);
      if (teamArt != null) {
         backlog = getAgileBacklog(teamArt);
      }
      return backlog;
   }

   @Override
   public IAgileBacklog createAgileBacklog(long teamId, String name, Long id) {
      return AgileFactory.createAgileBacklog(logger, atsApi, teamId, name, id);
   }

   @Override
   public IAgileBacklog updateAgileBacklog(JaxAgileBacklog updatedBacklog) {
      AgileBacklogWriter writer = new AgileBacklogWriter(atsApi, this, updatedBacklog);
      return writer.write();
   }

   @Override
   public AgileWriterResult updateAgileItem(JaxAgileItem newItem) {
      AgileItemWriter writer = new AgileItemWriter(atsApi, this, newItem);
      return writer.write();
   }

   @Override
   public Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> ids) {
      List<IAgileFeatureGroup> features = new LinkedList<>();
      for (ArtifactId featureArt : atsApi.getQueryService().getArtifacts(ids)) {
         features.add(atsApi.getAgileService().getAgileFeatureGroup(featureArt));
      }
      return features;
   }

   @Override
   public Collection<IAgileItem> getItems(IAgileBacklog backlog) {
      return getItems(backlog, AtsRelationTypes.Goal_Member);
   }

   private Collection<IAgileItem> getItems(IAtsObject backlogOrSprint, RelationTypeSide relationType) {
      List<IAgileItem> items = new LinkedList<>();
      ArtifactId backlogArt = backlogOrSprint.getStoreObject();
      for (ArtifactToken art : atsApi.getRelationResolver().getRelated(backlogArt, relationType)) {
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            items.add(atsApi.getWorkItemService().getAgileItem(art));
         } else {
            throw new OseeStateException("Inavlid artifact [%s] in [%s].  Only workflows are allowed, not [%s]",
               art.toStringWithId(), backlogOrSprint, art.getArtifactTypeId());
         }
      }
      return items;
   }

   @Override
   public Collection<IAgileItem> getItems(IAgileSprint sprint) {
      return getItems(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem);
   }

   @Override
   public Collection<IAgileFeatureGroup> getFeatureGroups(IAgileItem aItem) {
      List<IAgileFeatureGroup> groups = new LinkedList<>();
      ArtifactId itemArt = atsApi.getQueryService().getArtifact(aItem);
      for (ArtifactId featureGroup : atsApi.getRelationResolver().getRelated(itemArt,
         AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
         groups.add(atsApi.getAgileService().getAgileFeatureGroup(featureGroup));
      }
      return groups;
   }

   @Override
   public IAgileSprint getSprint(IAgileItem item) {
      IAgileSprint sprint = null;
      ArtifactToken itemArt = atsApi.getQueryService().getArtifact(item);
      ArtifactToken sprintArt =
         atsApi.getRelationResolver().getRelatedOrNull(itemArt, AtsRelationTypes.AgileSprintToItem_Sprint);
      if (sprintArt != null) {
         sprint = atsApi.getWorkItemService().getAgileSprint(sprintArt);
      }
      return sprint;
   }

   @Override
   public void deleteSprint(long sprintId) {
      ArtifactId sprint = atsApi.getQueryService().getArtifact(sprintId);
      if (sprint != null) {
         IAtsChangeSet changes = atsApi.createChangeSet("Delete Agile Sprint");
         changes.deleteArtifact(sprint);
         changes.execute();
      }
   }

   @Override
   public IAgileTeam getAgileTeam(IAgileItem item) {
      ArtifactId itemArt = atsApi.getQueryService().getArtifact(item);
      ArtifactId backlogArt = atsApi.getRelationResolver().getRelatedOrNull(itemArt, AtsRelationTypes.Goal_Member);
      if (backlogArt != null) {
         ArtifactId teamArt =
            atsApi.getRelationResolver().getRelatedOrNull(backlogArt, AtsRelationTypes.AgileTeamToBacklog_AgileTeam);
         if (teamArt != null) {
            return atsApi.getAgileService().getAgileTeam(teamArt);
         }
      }
      ArtifactId sprintArt =
         atsApi.getRelationResolver().getRelatedOrNull(itemArt, AtsRelationTypes.AgileSprintToItem_Sprint);
      if (sprintArt != null) {
         ArtifactId teamArt =
            atsApi.getRelationResolver().getRelatedOrNull(sprintArt, AtsRelationTypes.AgileTeamToSprint_AgileTeam);
         if (teamArt != null) {
            return atsApi.getAgileService().getAgileTeam(teamArt);
         }
      }
      return null;
   }

   @Override
   public ArtifactToken getRelatedBacklogArt(IAtsWorkItem workItem) {
      ArtifactToken relatedBacklogArt = null;
      for (ArtifactToken backlogArt : atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Goal_Goal)) {
         if (isBacklog(backlogArt)) {
            relatedBacklogArt = backlogArt;
         }
      }
      return relatedBacklogArt;
   }

   @Override
   public boolean isBacklog(Object object) {
      boolean backlog = false;
      if (object instanceof IAtsWorkItem) {
         backlog = atsApi.getRelationResolver().getRelatedCount((IAtsWorkItem) object,
            AtsRelationTypes.AgileTeamToBacklog_AgileTeam) == 1;
      } else if (object instanceof ArtifactToken) {
         backlog = atsApi.getRelationResolver().getRelatedCount((ArtifactToken) object,
            AtsRelationTypes.AgileTeamToBacklog_AgileTeam) == 1;
      }
      return backlog;
   }

   @Override
   public boolean isSprint(ArtifactId artifact) {
      return atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileSprint);
   }

   @Override
   public Collection<ArtifactToken> getRelatedSprints(ArtifactId artifact) {
      Set<ArtifactToken> sprints = new HashSet<>();
      for (ArtifactToken sprintArt : atsApi.getRelationResolver().getRelatedArtifacts(artifact,
         AtsRelationTypes.AgileSprintToItem_Sprint)) {
         sprints.add(sprintArt);
      }
      return sprints;
   }

   @Override
   public String getAgileTeamPointsStr(IAtsWorkItem workItem) {
      String result =
         atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, AtsAttributeTypes.Points, "");
      if (Strings.isInValid(result)) {
         Double pts =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.PointsNumeric, 0.0);
         if (pts == 0.0) {
            result = "";
         } else {
            result = pts.toString();
         }
      }
      return result;
   }

   @Override
   public IAgileTeam getAgileTeam(IAtsWorkItem workItem) {
      IAgileTeam agileTeam = null;
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      if (teamWf != null) {
         // attempt to get from team definitions relation to agile team
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         if (teamDef != null) {
            ArtifactId agileTeamArt =
               atsApi.getRelationResolver().getRelatedOrNull(teamDef, AtsRelationTypes.AgileTeamToAtsTeam_AgileTeam);
            if (agileTeamArt != null) {
               agileTeam = atsApi.getAgileService().getAgileTeam(agileTeamArt);
            }
         }
         // attempt to get from workitem relation to sprint
         if (agileTeam == null) {
            ArtifactId sprintArt =
               atsApi.getRelationResolver().getRelatedOrNull(workItem, AtsRelationTypes.AgileSprintToItem_Sprint);
            if (sprintArt != null) {
               IAgileSprint sprint = getAgileSprint(sprintArt);
               if (sprint != null) {
                  agileTeam = atsApi.getAgileService().getAgileTeamFromSprint(sprint);
               }
            }
         }
         // attemp to get from workitem relation to backlog
         if (agileTeam == null) {
            ArtifactId backlogArt = atsApi.getRelationResolver().getRelatedOrNull(workItem, AtsRelationTypes.Goal_Goal);
            if (backlogArt != null) {
               IAgileBacklog backlog = getAgileBacklog(backlogArt);
               if (backlog != null) {
                  agileTeam = atsApi.getAgileService().getAgileTeamFromBacklog(backlog);
               }
            }
         }
      }
      return agileTeam;
   }

   @Override
   public IAgileTeam getAgileTeamFromSprint(IAgileSprint sprint) {
      IAgileTeam agileTeam = null;
      ArtifactId agileTeamArt =
         atsApi.getRelationResolver().getRelatedOrNull(sprint, AtsRelationTypes.AgileTeamToSprint_AgileTeam);
      if (agileTeamArt != null) {
         agileTeam = atsApi.getAgileService().getAgileTeam(agileTeamArt);
      }
      return agileTeam;
   }

   @Override
   public IAgileTeam getAgileTeamFromBacklog(IAgileBacklog backlog) {
      IAgileTeam agileTeam = null;
      ArtifactId agileBacklogArt =
         atsApi.getRelationResolver().getRelatedOrNull(backlog, AtsRelationTypes.AgileTeamToBacklog_AgileTeam);
      if (agileBacklogArt != null) {
         agileTeam = atsApi.getAgileService().getAgileTeam(agileBacklogArt);
      }
      return agileTeam;
   }

   @Override
   public XResultData storeSprintReports(long teamId, long sprintId) {
      XResultData results = new XResultData();
      results.setTitle("Store Sprint Reports");
      IAtsChangeSet changes = atsApi.createChangeSet("Store Agile Sprint Reports");
      IAgileSprint sprint = getAgileSprint(atsApi.getQueryService().getArtifact(sprintId));
      createUpdateBurnChart(new SprintBurndownOperations(atsApi), teamId, sprintId, atsApi, changes, sprint);
      createUpdateBurnChart(new SprintBurnupOperations(atsApi), teamId, sprintId, atsApi, changes, sprint);
      for (IAgileSprintHtmlOperation operation : atsApi.getAgileSprintHtmlReportOperations()) {
         createUpdateBurnChart(operation, teamId, sprintId, atsApi, changes, sprint);
      }
      changes.executeIfNeeded();
      return results;
   }

   public static void createUpdateBurnChart(IAgileSprintHtmlOperation operation, long teamId, long sprintId, AtsApi atsApi, IAtsChangeSet changes, IAgileSprint sprint) {
      String html = operation.getReportHtml(teamId, sprintId);

      ArtifactId burndownArt =
         atsApi.getRelationResolver().getChildNamedOrNull(sprint, operation.getReportType().name());
      if (burndownArt == null) {
         burndownArt = changes.createArtifact(CoreArtifactTypes.GeneralDocument, operation.getReportType().name());
         changes.setSoleAttributeValue(burndownArt, CoreAttributeTypes.Extension, "html");
         changes.addChild(sprint.getStoreObject(), burndownArt);
      }
      try {
         changes.setSoleAttributeValue(burndownArt, CoreAttributeTypes.NativeContent, Lib.stringToInputStream(html));
      } catch (UnsupportedEncodingException ex) {
         throw new OseeArgumentException(ex, "Error trying to store Agile " + operation.getReportType());
      }
   }

   @Override
   public Collection<IAtsTeamDefinition> getAtsTeams(IAgileTeam aTeam) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (ArtifactId teamArt : atsApi.getRelationResolver().getRelated(aTeam.getStoreObject(),
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         teamDefs.add(atsApi.getQueryService().getConfigItem(teamArt));
      }
      return teamDefs;
   }

   /********************************
    ** Agile Program
    ***********************************/

   @Override
   public IAgileProgram createAgileProgram(IAgileProgram agileProgram) {
      AgileProgramOperations ops = new AgileProgramOperations(atsApi);
      IAgileProgram program = ops.createAgileProgram(agileProgram);
      return program;
   }

   @Override
   public IAgileProgramFeature createAgileProgramFeature(IAgileProgramBacklogItem programBacklogItem, JaxAgileProgramFeature jaxFeature) {
      AgileProgramOperations ops = new AgileProgramOperations(atsApi);
      IAgileProgramFeature feature = ops.createAgileProgramFeature(programBacklogItem, jaxFeature);
      return feature;
   }

   @Override
   public void setAgileStory(IAtsTeamWorkflow teamWf, IAgileStory story, IAtsChangeSet changes) {
      changes.unrelateFromAll(AtsRelationTypes.AgileStoryToItems_Story, teamWf.getStoreObject());
      changes.relate(story, AtsRelationTypes.AgileStoryToItems_AtsItem, teamWf);
   }

   @Override
   public IAgileStory createAgileStory(IAgileProgramFeature feature, JaxAgileStory jaxStory) {
      AgileProgramOperations ops = new AgileProgramOperations(atsApi);
      IAgileStory story = ops.createAgileStory(feature, jaxStory);
      return story;
   }

   @Override
   public IAgileProgramBacklog createAgileProgramBacklog(IAgileProgram agileProgram, JaxAgileProgramBacklog jaxProgramBacklog) {
      AgileProgramOperations ops = new AgileProgramOperations(atsApi);
      IAgileProgramBacklog progBacklog = ops.createAgileProgramBacklog(agileProgram, jaxProgramBacklog);
      return progBacklog;
   }

   @Override
   public IAgileProgramBacklogItem createAgileProgramBacklogItem(IAgileProgramBacklog programBacklog, JaxAgileProgramBacklogItem jaxProgramBacklogItem) {
      AgileProgramOperations ops = new AgileProgramOperations(atsApi);
      IAgileProgramBacklogItem progBacklogItem =
         ops.createAgileProgramBacklogItem(programBacklog, jaxProgramBacklogItem);
      return progBacklogItem;
   }

   @Override
   public List<ArtifactToken> getTeamMembersOrdered(IAgileTeam aTeam) {
      Set<IAtsUser> activeTeamMembers = atsApi.getAgileService().getTeamMebers(aTeam);

      // Construct list of users with team members sorted first and other users last
      List<ArtifactToken> results = new LinkedList<>();
      for (IAtsUser user : activeTeamMembers) {
         results.add(ArtifactToken.valueOf(user.getStoreObject(), user.getName()));
      }
      Collections.sort(results, new NamedComparator(SortOrder.ASCENDING));

      // Add UnAssigned after team members for convenience
      results.add(ArtifactToken.valueOf(AtsCoreUsers.UNASSIGNED_USER.getId(), AtsCoreUsers.UNASSIGNED_USER.getName(),
         CoreBranches.COMMON));

      return results;
   }

   @Override
   public List<ArtifactToken> getOtherMembersOrdered(IAgileTeam aTeam) {
      List<ArtifactToken> activeTeamMembers = getTeamMembersOrdered(aTeam);
      List<ArtifactToken> results = new LinkedList<>();
      for (IAtsUser user : atsApi.getUserService().getUsers()) {
         if (!user.equals(AtsCoreUsers.UNASSIGNED_USER) && user.isActive() && !activeTeamMembers.contains(
            user.getStoreObject())) {
            results.add(user.getStoreObject());
         }
      }
      Collections.sort(results, new NamedComparator(SortOrder.ASCENDING));
      return results;
   }

}