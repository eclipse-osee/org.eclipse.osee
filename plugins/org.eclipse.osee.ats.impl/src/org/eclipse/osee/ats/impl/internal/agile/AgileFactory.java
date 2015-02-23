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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.util.AtsChangeSet;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AgileFactory {

   private AgileFactory() {
      // Utilitiy class
   }

   public static IAgileTeam createUpdateAgileTeam(Log logger, IAtsServer atsServer, JaxAgileTeam team) {
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
      ArtifactReadable topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsServer, transaction, userArt);
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

      transaction.commit();
      return getAgileTeam(logger, atsServer, agileTeamArt);
   }

   public static IAgileTeam getAgileTeam(Log logger, IAtsServer atsServer, Object artifact) {
      return new AgileTeam(logger, atsServer, (ArtifactReadable) artifact);
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, IAtsServer atsServer, long teamUuid, String name, String guid) {
      JaxAgileFeatureGroup feature = new JaxAgileFeatureGroup();
      feature.setName(name);
      feature.setGuid(guid);
      feature.setTeamUuid(teamUuid);
      feature.setActive(true);
      return createAgileFeatureGroup(logger, atsServer, feature);
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, IAtsServer atsServer, JaxAgileFeatureGroup newFeatureGroup) {
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create new Agile Feature Group");
      ArtifactReadable featureGroupArt =
         (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileFeatureGroup, newFeatureGroup.getName(),
            newFeatureGroup.getGuid());
      transaction.setSoleAttributeValue(featureGroupArt, AtsAttributeTypes.Active, newFeatureGroup.isActive());

      ArtifactReadable featureGroupFolder =
         AgileFolders.getOrCreateTopFeatureGroupFolder(atsServer, transaction, newFeatureGroup.getTeamUuid(), userArt);
      transaction.addChildren(featureGroupFolder, featureGroupArt);

      ArtifactReadable team = AgileFolders.getTeamFolder(atsServer, newFeatureGroup.getTeamUuid());
      transaction.relate(team, AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup, featureGroupArt);

      transaction.commit();
      return getAgileFeatureGroup(logger, atsServer, featureGroupArt);
   }

   public static IAgileFeatureGroup getAgileFeatureGroup(Log logger, IAtsServer atsServer, Object artifact) {
      return new AgileFeatureGroup(logger, atsServer, (ArtifactReadable) artifact);
   }

   public static IAgileSprint createAgileSprint(Log logger, IAtsServer atsServer, long teamUuid, String name, String guid) {

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

      ArtifactReadable teamFolder = AgileFolders.getTeamFolder(atsServer, teamUuid);
      ArtifactReadable agileSprintFolderArt = AgileFolders.getOrCreateTopSprintFolder(atsServer, teamUuid, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.Default_Hierarchical__Child, sprintArt);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToSprint_Sprint, sprintArt);

      changes.execute();
      return getAgileSprint(logger, atsServer, sprintArt);
   }

   public static IAgileSprint getAgileSprint(Log logger, IAtsServer atsServer, Object artifact) {
      return new AgileSprint(logger, atsServer, (ArtifactReadable) artifact);
   }

   public static IAgileBacklog createAgileBacklog(Log logger, IAtsServer atsServer, long teamUuid, String name, String guid) {

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

      ArtifactReadable teamFolder = AgileFolders.getTeamFolder(atsServer, teamUuid);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog, backlogArt);
      changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, backlogArt);

      changes.execute();
      return getAgileBacklog(logger, atsServer, backlogArt);
   }

   public static IAgileBacklog getAgileBacklog(Log logger, IAtsServer atsServer, Object artifact) {
      return new AgileBacklog(logger, atsServer, (ArtifactReadable) artifact);
   }

}
