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
import java.util.Iterator;
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
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
   public IAgileTeam createAgileTeam(String name, String guid) {
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create new Agile Team");
      ArtifactReadable agileArt = (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileTeam, name, guid);

      ArtifactReadable topAgileFolder = getOrCreateTopAgileFolder(transaction, userArt);
      transaction.addChildren(topAgileFolder, agileArt);

      transaction.commit();
      return getAgileTeam(agileArt);
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
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create new Agile Feature Group");
      ArtifactReadable featureGroupArt =
         (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileFeatureGroup, name, guid);

      ArtifactReadable agileTeamArt = getOrCreateTopFeatureGroupFolder(transaction, teamUuid, userArt);
      transaction.addChildren(agileTeamArt, featureGroupArt);

      transaction.commit();
      return getAgileFeatureGroup(featureGroupArt);
   }

   private ArtifactReadable getOrCreateTopFeatureGroupFolder(TransactionBuilder tx, long teamUuid, ArtifactReadable userArt) {
      ArtifactReadable teamFolder =
         atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andLocalId(
            new Long(teamUuid).intValue()).getResults().getAtMostOneOrNull();
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

      ArtifactReadable agileSprintFolderArt = getOrCreateTopSprintFolder(teamUuid, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.Default_Hierarchical__Child, sprintArt);

      changes.execute();
      return getAgileSprint(sprintArt);
   }

   private ArtifactReadable getOrCreateTopSprintFolder(long teamUuid, AtsChangeSet changes) {
      ArtifactReadable teamFolder =
         atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andLocalId(
            new Long(teamUuid).intValue()).getResults().getAtMostOneOrNull();
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

}
