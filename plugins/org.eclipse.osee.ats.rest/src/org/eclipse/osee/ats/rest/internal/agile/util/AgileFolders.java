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
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AgileFolders {

   public static ArtifactReadable getOrCreateTopSprintFolder(IAtsServer atsServer, long teamUuid, IAtsChangeSet changes) {
      ArtifactReadable teamFolder = getTeamFolder(atsServer, teamUuid);
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

   public static ArtifactReadable getTeamFolder(IAtsServer atsServer, long teamUuid) {
      return atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
         new Long(teamUuid).intValue()).getResults().getAtMostOneOrNull();
   }

   public static ArtifactReadable getOrCreateTopFeatureGroupFolder(IAtsServer atsServer, TransactionBuilder tx, long teamUuid, ArtifactReadable userArt) {
      ArtifactReadable teamFolder = AgileFolders.getTeamFolder(atsServer, teamUuid);
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

   public static ArtifactReadable getOrCreateTopAgileFolder(IAtsServer atsServer, TransactionBuilder tx, ArtifactReadable userArt) {
      ArtifactId agileFolder = atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
         AtsArtifactToken.TopAgileFolder).getResults().getAtMostOneOrNull();
      if (agileFolder == null) {
         agileFolder = tx.createArtifact(AtsArtifactToken.TopAgileFolder);
         ArtifactReadable rootArtifact = atsServer.getOrcsApi().getQueryFactory().fromBranch(
            atsServer.getAtsBranch()).andIsHeirarchicalRootArtifact().getResults().getExactlyOne();
         tx.addChildren(rootArtifact, agileFolder);
      }
      return (ArtifactReadable) agileFolder;
   }

}
