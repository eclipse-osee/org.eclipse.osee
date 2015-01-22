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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.util.AtsChangeSet;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactId;
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
   public IAgileTeam getAgileTeam(Object artifact) {
      return new AgileTeam(logger, atsServer, (ArtifactReadable) artifact);
   }

   @Override
   public IAgileTeam getAgileTeamById(long teamUuid) {
      return getAgileTeam(getArtifact(teamUuid));
   }

   @Override
   public IAgileTeam createUpdateAgileTeam(JaxAgileTeam team) {
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());

      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create-Update new Agile Team");

      ArtifactReadable agileTeamArt = atsServer.getArtifactByUuid(team.getUuid());
      if (agileTeamArt == null) {
         agileTeamArt =
            (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileTeam, team.getName(), team.getGuid());
      }
      transaction.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, team.isActive());
      ArtifactReadable topAgileFolder = getOrCreateTopAgileFolder(transaction, userArt);
      if (!topAgileFolder.equals(agileTeamArt.getParent())) {
         transaction.unrelateFromAll(CoreRelationTypes.Default_Hierarchical__Parent, agileTeamArt);
         transaction.addChildren(topAgileFolder, agileTeamArt);
      }

      Set<ArtifactReadable> atsTeamArts = new HashSet<ArtifactReadable>();
      for (long atsTeamUuid : team.getAtsTeamUuids()) {
         ArtifactReadable atsTeamArt = atsServer.getArtifactByUuid(atsTeamUuid);
         if (atsTeamArt != null && atsTeamArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            atsTeamArts.add(atsTeamArt);
         } else {
            throw new OseeArgumentException("UUID %d is not a valid Ats Team Definition", atsTeamUuid);
         }
      }
      transaction.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

      if (team.getBacklogUuid() > 0) {
         ArtifactReadable backlogArt = atsServer.getArtifactByUuid(team.getBacklogUuid());
         ResultSet<ArtifactReadable> related = agileTeamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         ArtifactReadable relatedBacklogArt = null;
         if (related.size() > 0) {
            relatedBacklogArt = related.iterator().next();
         }
         if (backlogArt != null && !relatedBacklogArt.equals(backlogArt)) {
            transaction.unrelate(agileTeamArt, AtsRelationTypes.AgileTeamToSprint_Sprint, backlogArt);
         } else if (backlogArt == null && relatedBacklogArt != null) {
            transaction.unrelateFromAll(AtsRelationTypes.AgileTeamToSprint_Sprint, agileTeamArt);
         }
      }

      transaction.commit();
      return getAgileTeam(agileTeamArt);
   }

   @Override
   public void deleteAgileTeam(long uuid) {
      ArtifactReadable team = atsServer.getArtifactByUuid(uuid);
      if (!team.isOfType(AtsArtifactTypes.AgileTeam)) {
         throw new OseeArgumentException("UUID %d is not a valid Agile", uuid);
      }
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), team,
            "Delete Agile Team");
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

   @SuppressWarnings("unchecked")
   private ArtifactReadable getOrCreateTopAgileFolder(TransactionBuilder tx, ArtifactReadable userArt) {
      ArtifactId agileFolder =
         atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(
            AtsArtifactToken.TopAgileFolder).getResults().getAtMostOneOrNull();
      if (agileFolder == null) {
         agileFolder = tx.createArtifact(AtsArtifactToken.TopAgileFolder);
         ArtifactReadable rootArtifact =
            atsServer.getOrcsApi().getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andIsHeirarchicalRootArtifact().getResults().getExactlyOne();
         tx.addChildren(rootArtifact, agileFolder);
      }
      return (ArtifactReadable) agileFolder;
   }

   @Override
   public Collection<IAgileTeam> getTeams() {
      List<IAgileTeam> teams = new ArrayList<IAgileTeam>();
      for (ArtifactReadable teamArt : atsServer.getQuery().andTypeEquals(AtsArtifactTypes.AgileTeam).getResults()) {
         teams.add(getAgileTeam(teamArt));
      }
      return teams;
   }

   /********************************
    ** Agile Feature Group
    ***********************************/
   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(Object artifact) {
      return new AgileFeatureGroup(logger, atsServer, (ArtifactReadable) artifact);
   }

   @Override
   public void deleteAgileFeatureGroup(long uuid) {
      ArtifactReadable featureGroup = atsServer.getArtifactByUuid(uuid);
      if (!featureGroup.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         throw new OseeArgumentException("UUID %d is not a valid Agile Feature Group", uuid);
      }
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), featureGroup,
            "Delete Agile Feature Group");
      transaction.deleteArtifact(featureGroup);
      transaction.commit();
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(long teamUuid, String name, String guid) {
      JaxAgileFeatureGroup feature = new JaxAgileFeatureGroup();
      feature.setName(name);
      feature.setGuid(guid);
      feature.setTeamUuid(teamUuid);
      feature.setActive(true);
      return createAgileFeatureGroup(feature);
   }

   @Override
   public IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup) {
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create new Agile Feature Group");
      ArtifactReadable featureGroupArt =
         (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileFeatureGroup, newFeatureGroup.getName(),
            newFeatureGroup.getGuid());
      transaction.setSoleAttributeValue(featureGroupArt, AtsAttributeTypes.Active, newFeatureGroup.isActive());

      ArtifactReadable agileTeamArt =
         getOrCreateTopFeatureGroupFolder(transaction, newFeatureGroup.getTeamUuid(), userArt);
      transaction.addChildren(agileTeamArt, featureGroupArt);

      transaction.commit();
      return getAgileFeatureGroup(featureGroupArt);
   }

   private ArtifactReadable getOrCreateTopFeatureGroupFolder(TransactionBuilder tx, long teamUuid, ArtifactReadable userArt) {
      ArtifactReadable teamFolder = getArtifact(teamUuid);
      ArtifactReadable featureGroupFolder = null;
      for (ArtifactReadable child : teamFolder.getChildren()) {
         if (child.getName().equals(AgileUtil.FEATURE_GROUP_FOLDER_NAME)) {
            featureGroupFolder = child;
         }
      }
      if (featureGroupFolder == null) {
         featureGroupFolder =
            (ArtifactReadable) tx.createArtifact(CoreArtifactTypes.Folder, AgileUtil.FEATURE_GROUP_FOLDER_NAME);
         tx.addChildren(teamFolder, featureGroupFolder);
      }
      return featureGroupFolder;
   }

   /********************************
    ** Agile Sprint
    ***********************************/
   @Override
   public IAgileSprint getAgileSprint(Object artifact) {
      return new AgileSprint(logger, atsServer, (ArtifactReadable) artifact);
   }

   @Override
   public IAgileSprint createAgileSprint(long teamUuid, String name, String guid) {

      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreFactory().createAtsChangeSet("Create new Agile Sprint",
            AtsCoreUsers.SYSTEM_USER);

      ArtifactReadable sprintArt = (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.AgileSprint, name);
      IAgileSprint sprint = atsServer.getWorkItemFactory().getAgileSprint(sprintArt);

      atsServer.getUtilService().setAtsId(atsServer.getSequenceProvider(), sprint,
         TeamDefinitions.getTopTeamDefinition(atsServer.getConfig()), changes);

      // Initialize state machine
      atsServer.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsServer.getUserService().getCurrentUser(), changes);

      changes.add(sprintArt);

      ArtifactReadable teamFolder = getArtifact(teamUuid);
      ArtifactReadable agileSprintFolderArt = getOrCreateTopSprintFolder(teamUuid, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.Default_Hierarchical__Child, sprintArt);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToSprint_Sprint, sprintArt);

      changes.execute();
      return getAgileSprint(sprintArt);
   }

   private ArtifactReadable getOrCreateTopSprintFolder(long teamUuid, AtsChangeSet changes) {
      ArtifactReadable teamFolder = getArtifact(teamUuid);
      ArtifactReadable sprintFolder = null;
      for (ArtifactReadable child : teamFolder.getChildren()) {
         if (child.getName().equals(AgileUtil.SPRINT_FOLDER_NAME)) {
            sprintFolder = child;
         }
      }
      if (sprintFolder == null) {
         sprintFolder =
            (ArtifactReadable) changes.createArtifact(CoreArtifactTypes.Folder, AgileUtil.SPRINT_FOLDER_NAME);
         changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, sprintFolder);
      }
      return sprintFolder;
   }

   private ArtifactReadable getArtifact(long teamUuid) {
      return atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andLocalId(
         new Long(teamUuid).intValue()).getResults().getAtMostOneOrNull();
   }

   @Override
   public Collection<IAgileSprint> getSprintsForTeam(long teamUuid) {
      List<IAgileSprint> sprints = new ArrayList<IAgileSprint>();
      ArtifactReadable team = getArtifact(teamUuid);
      for (ArtifactReadable sprintArt : team.getRelated(AtsRelationTypes.AgileTeamToSprint_Sprint)) {
         sprints.add(getAgileSprint(sprintArt));
      }
      return sprints;
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public IAgileBacklog getAgileBacklog(Object artifact) {
      return new AgileBacklog(logger, atsServer, (ArtifactReadable) artifact);
   }

   @Override
   public IAgileBacklog createAgileBacklog(long teamUuid, String name, String guid) {

      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreFactory().createAtsChangeSet("Create new Agile Backlog",
            AtsCoreUsers.SYSTEM_USER);

      ArtifactReadable backlogArt = (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.Goal, name);
      IAgileBacklog sprint = atsServer.getWorkItemFactory().getAgileBacklog(backlogArt);

      atsServer.getUtilService().setAtsId(atsServer.getSequenceProvider(), sprint,
         TeamDefinitions.getTopTeamDefinition(atsServer.getConfig()), changes);

      // Initialize state machine
      atsServer.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsServer.getUserService().getCurrentUser(), changes);

      changes.add(backlogArt);

      ArtifactReadable teamFolder = getArtifact(teamUuid);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog, backlogArt);
      changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, backlogArt);

      changes.execute();
      return getAgileBacklog(backlogArt);
   }

   @Override
   public JaxAgileItem updateItem(JaxAgileItem newItem) {
      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreFactory().createAtsChangeSet("Update new Agile Item",
            AtsCoreUsers.SYSTEM_USER);

      if (newItem.isSetFeatures()) {
         Collection<IAgileFeatureGroup> features = getAgileFeatureGroups(newItem.getFeatures());
         List<ArtifactReadable> featureArts = new LinkedList<ArtifactReadable>();
         for (IAgileFeatureGroup feature : features) {
            featureArts.add((ArtifactReadable) feature.getStoreObject());
         }
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
            for (IAgileFeatureGroup feature : features) {
               ArtifactReadable featureArt = (ArtifactReadable) feature.getStoreObject();
               if (!featureArt.areRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup, awa)) {
                  changes.relate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
            for (ArtifactReadable featureArt : awa.getRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
               if (!featureArts.contains(featureArt)) {
                  changes.unrelate(featureArt, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
         }
      }

      if (newItem.isSetSprint()) {
         IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(newItem.getSprintUuid());
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
            if (sprint != null) {
               changes.setRelation(awa, AtsRelationTypes.AgileSprintToItem_Sprint, sprint);
            } else {
               changes.unrelateAll(awa, AtsRelationTypes.AgileSprintToItem_Sprint);
            }
            changes.add(sprint);
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }

      return newItem;
   }

   @Override
   public Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> uuids) {
      List<IAgileFeatureGroup> features = new LinkedList<IAgileFeatureGroup>();
      for (ArtifactReadable featureArt : atsServer.getArtifacts(uuids)) {
         features.add(atsServer.getConfigItemFactory().getAgileFeatureGroup(featureArt));
      }
      return features;
   }

   @Override
   public IAgileBacklog getBacklogForTeam(long teamUuid) {
      IAgileBacklog backlog = null;
      ArtifactReadable teamArt =
         atsServer.getQuery().andLocalId(Long.valueOf(teamUuid).intValue()).getResults().getAtMostOneOrNull();
      ArtifactReadable backlogArt =
         teamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getAtMostOneOrNull();
      if (backlogArt != null) {
         backlog = getAgileBacklog(backlogArt);
      }
      return backlog;
   }

}
