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
package org.eclipse.osee.ats.rest.internal.agile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.agile.AgileBacklog;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.util.AgileBacklogWriter;
import org.eclipse.osee.ats.rest.internal.agile.util.AgileFolders;
import org.eclipse.osee.ats.rest.internal.agile.util.AgileItemWriter;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AgileService implements IAgileService {

   private final Log logger;
   private final IAtsServer atsServer;

   public AgileService(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   /********************************
    ** Agile Team
    ***********************************/
   @Override
   public IAgileTeam getAgileTeam(ArtifactId artifact) {
      return AgileFactory.getAgileTeam(logger, atsServer, artifact);
   }

   @Override
   public IAgileTeam getAgileTeam(long uuid) {
      IAgileTeam team = null;
      ArtifactReadable teamArt = atsServer.getArtifact(uuid);
      if (teamArt != null) {
         team = getAgileTeam(teamArt);
      }
      return team;
   }

   @Override
   public IAgileTeam getAgileTeamById(long teamUuid) {
      IAgileTeam team = null;
      ArtifactReadable artifact = getArtifact(teamUuid);
      if (artifact != null) {
         team = getAgileTeam(artifact);
      }
      return team;
   }

   @Override
   public IAgileTeam createAgileTeam(JaxNewAgileTeam newTeam) {
      return AgileFactory.createAgileTeam(logger, atsServer, newTeam);
   }

   @Override
   public IAgileTeam updateAgileTeam(JaxAgileTeam team) {
      return AgileFactory.updateAgileTeam(logger, atsServer, team);
   }

   @Override
   public void deleteAgileTeam(long uuid) {
      ArtifactReadable team = atsServer.getArtifact(uuid);
      if (!team.isOfType(AtsArtifactTypes.AgileTeam)) {
         throw new OseeArgumentException("UUID %d is not a valid Agile Team", uuid);
      }
      TransactionBuilder transaction = atsServer.getOrcsApi().getTransactionFactory().createTransaction(
         atsServer.getAtsBranch(), team, "Delete Agile Team");
      deleteRecurse(transaction, team.getChildren());
      transaction.deleteArtifact(team);
      transaction.commit();
   }

   private void deleteRecurse(TransactionBuilder transaction, ResultSet<ArtifactReadable> resultSet) {
      Iterator<ArtifactReadable> iterator = resultSet.iterator();
      while (iterator.hasNext()) {
         ArtifactReadable art = iterator.next();
         deleteRecurse(transaction, art.getChildren());
         transaction.deleteArtifact(art);
      }
   }

   @Override
   public Collection<IAgileTeam> getTeams() {
      List<IAgileTeam> teams = new ArrayList<>();
      for (ArtifactReadable teamArt : atsServer.getQuery().andTypeEquals(AtsArtifactTypes.AgileTeam).getResults()) {
         teams.add(getAgileTeam(teamArt));
      }
      return teams;
   }

   @Override
   public IAttributeType getAgileTeamPointsAttributeType(IAgileTeam team) {
      IAttributeType type = AtsAttributeTypes.Points;
      String attrTypeName =
         atsServer.getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.PointsAttributeType, null);
      if (Strings.isValid(attrTypeName)) {
         type = getTypeFromName(attrTypeName);
      }
      return type;
   }

   private IAttributeType getTypeFromName(String attrTypeName) {
      IAttributeType type = null;
      for (IAttributeType attrType : atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getAll()) {
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

   /********************************
    ** Agile Feature Group
    ***********************************/
   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact) {
      return AgileFactory.getAgileFeatureGroup(logger, atsServer, artifact);
   }

   @Override
   public void deleteAgileFeatureGroup(long uuid) {
      ArtifactReadable featureGroup = atsServer.getArtifact(uuid);
      if (!featureGroup.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         throw new OseeArgumentException("UUID %d is not a valid Agile Feature Group", uuid);
      }
      TransactionBuilder transaction = atsServer.getOrcsApi().getTransactionFactory().createTransaction(
         atsServer.getAtsBranch(), featureGroup, "Delete Agile Feature Group");
      transaction.deleteArtifact(featureGroup);
      transaction.commit();
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(long teamUuid, String name, String guid, Long uuid) {
      return AgileFactory.createAgileFeatureGroup(logger, atsServer, teamUuid, name, guid, uuid);
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup) {
      return AgileFactory.createAgileFeatureGroup(logger, atsServer, newFeatureGroup);
   }

   @Override
   public Collection<IAgileFeatureGroup> getAgileFeatureGroups(IAgileTeam team) {
      List<IAgileFeatureGroup> groups = new LinkedList<>();
      ArtifactReadable artifact = (ArtifactReadable) team.getStoreObject();
      for (ArtifactReadable groupArt : artifact.getRelated(AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup)) {
         groups.add(atsServer.getConfigItemFactory().getAgileFeatureGroup(groupArt));
      }
      return groups;
   }

   @Override
   public IAgileBacklog getBacklogForTeam(long teamUuid) {
      IAgileBacklog backlog = null;
      ArtifactReadable teamArt =
         atsServer.getQuery().andUuid(Long.valueOf(teamUuid).intValue()).getResults().getAtMostOneOrNull();
      ArtifactReadable backlogArt =
         teamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getAtMostOneOrNull();
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
      return AgileFactory.getAgileSprint(logger, atsServer, artifact);
   }

   @Override
   public IAgileSprint createAgileSprint(long teamUuid, String name, String guid, Long uuid) {
      return AgileFactory.createAgileSprint(logger, atsServer, teamUuid, name, guid, uuid);
   }

   private ArtifactReadable getArtifact(long teamUuid) {
      return atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
         new Long(teamUuid).intValue()).getResults().getAtMostOneOrNull();
   }

   @Override
   public Collection<IAgileSprint> getSprintsForTeam(long teamUuid) {
      List<IAgileSprint> sprints = new ArrayList<>();
      ArtifactReadable team = getArtifact(teamUuid);
      for (ArtifactReadable sprintArt : team.getRelated(AtsRelationTypes.AgileTeamToSprint_Sprint)) {
         sprints.add(getAgileSprint(sprintArt));
      }
      return sprints;
   }

   @Override
   public Collection<IAgileSprint> getAgileSprints(IAgileTeam team) {
      List<IAgileSprint> sprints = new LinkedList<>();
      ArtifactReadable artifact = (ArtifactReadable) team.getStoreObject();
      for (ArtifactReadable groupArt : artifact.getRelated(AtsRelationTypes.AgileTeamToSprint_Sprint)) {
         sprints.add(atsServer.getWorkItemFactory().getAgileSprint(groupArt));
      }
      return sprints;
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public IAgileBacklog getAgileBacklog(ArtifactId artifact) {
      return AgileFactory.getAgileBacklog(logger, atsServer, artifact);
   }

   @Override
   public IAgileBacklog getAgileBacklog(IAgileTeam team) {
      ArtifactReadable teamFolder = AgileFolders.getTeamFolder(atsServer, team.getId());
      if (teamFolder == null) {
         return null;
      }
      ArtifactReadable backlogArt = teamFolder.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getOneOrNull();
      if (backlogArt == null) {
         return null;
      }
      return new AgileBacklog(logger, atsServer, backlogArt);
   }

   @Override
   public IAgileBacklog getAgileBacklog(long uuid) {
      IAgileBacklog backlog = null;
      ArtifactReadable teamArt = atsServer.getArtifact(uuid);
      if (teamArt != null) {
         backlog = getAgileBacklog(teamArt);
      }
      return backlog;
   }

   @Override
   public IAgileBacklog createAgileBacklog(long teamUuid, String name, String guid, Long uuid) {
      return AgileFactory.createAgileBacklog(logger, atsServer, teamUuid, name, guid, uuid);
   }

   @Override
   public IAgileBacklog updateAgileBacklog(JaxAgileBacklog updatedBacklog) {
      AgileBacklogWriter writer = new AgileBacklogWriter(atsServer, this, updatedBacklog);
      return writer.write();
   }

   @Override
   public JaxAgileItem updateItem(JaxAgileItem newItem) {
      AgileItemWriter writer = new AgileItemWriter(atsServer, this, newItem);
      return writer.write();
   }

   @Override
   public Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> uuids) {
      List<IAgileFeatureGroup> features = new LinkedList<>();
      for (ArtifactReadable featureArt : atsServer.getArtifacts(uuids)) {
         features.add(atsServer.getConfigItemFactory().getAgileFeatureGroup(featureArt));
      }
      return features;
   }

   @Override
   public Collection<IAgileItem> getItems(IAgileBacklog backlog) {
      return getItems(backlog, AtsRelationTypes.Goal_Member);
   }

   private Collection<IAgileItem> getItems(IAtsObject backlogOrSprint, RelationTypeSide relationType) {
      List<IAgileItem> items = new LinkedList<>();
      ArtifactReadable backlogArt = (ArtifactReadable) backlogOrSprint.getStoreObject();
      for (ArtifactReadable art : backlogArt.getRelated(relationType)) {
         if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            items.add(atsServer.getWorkItemFactory().getAgileItem(art));
         } else {
            throw new OseeStateException("Inavlid artifact [%s] in [%s].  Only workflows are allowed, not [%s]",
               art.toStringWithId(), backlogOrSprint, art.getArtifactType().getName());
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
      ArtifactReadable itemArt = atsServer.getArtifact(aItem);
      for (ArtifactReadable featureGroup : itemArt.getRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
         groups.add(atsServer.getAgileService().getAgileFeatureGroup(featureGroup));
      }
      return groups;
   }

   @Override
   public IAgileSprint getSprint(IAgileItem item) {
      IAgileSprint sprint = null;
      ArtifactReadable itemArt = atsServer.getArtifact(item);
      ArtifactReadable sprintArt = itemArt.getRelated(AtsRelationTypes.AgileSprintToItem_Sprint).getAtMostOneOrNull();
      if (sprintArt != null) {
         sprint = atsServer.getWorkItemFactory().getAgileSprint(sprintArt);
      }
      return sprint;
   }

   @Override
   public void deleteSprint(long sprintUuid) {
      ArtifactReadable sprint = atsServer.getArtifact(sprintUuid);
      if (sprint != null) {
         TransactionBuilder transaction = atsServer.getOrcsApi().getTransactionFactory().createTransaction(
            atsServer.getAtsBranch(), sprint, "Delete Agile Sprint");
         transaction.deleteArtifact(sprint);
         transaction.commit();
      }
   }

   @Override
   public IAgileTeam getAgileTeam(IAgileItem item) {
      ArtifactReadable itemArt = atsServer.getArtifact(item);
      ArtifactReadable backlogArt = itemArt.getRelated(AtsRelationTypes.Goal_Member).getAtMostOneOrNull();
      if (backlogArt != null) {
         ArtifactReadable teamArt =
            backlogArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_AgileTeam).getAtMostOneOrNull();
         if (teamArt != null) {
            return atsServer.getConfigItemFactory().getAgileTeam(teamArt);
         }
      }
      ArtifactReadable sprintArt = itemArt.getRelated(AtsRelationTypes.AgileSprintToItem_Sprint).getAtMostOneOrNull();
      if (sprintArt != null) {
         ArtifactReadable teamArt =
            sprintArt.getRelated(AtsRelationTypes.AgileTeamToSprint_AgileTeam).getAtMostOneOrNull();
         if (teamArt != null) {
            return atsServer.getConfigItemFactory().getAgileTeam(teamArt);
         }
      }
      return null;
   }

}
