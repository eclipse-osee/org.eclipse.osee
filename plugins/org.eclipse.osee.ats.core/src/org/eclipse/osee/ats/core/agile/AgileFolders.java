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

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class AgileFolders {

   public static ArtifactId getOrCreateTopSprintFolder(IAtsServices services, long teamUuid, IAtsChangeSet changes) {
      ArtifactId teamFolder = getTeamFolder(services, teamUuid);
      ArtifactId sprintFolder = null;
      for (ArtifactToken child : services.getRelationResolver().getChildren(teamFolder)) {
         if (child.getName().equals(IAgileService.SPRINT_FOLDER_NAME)) {
            sprintFolder = child;
         }
      }
      if (sprintFolder == null) {
         sprintFolder = changes.createArtifact(CoreArtifactTypes.Folder, IAgileService.SPRINT_FOLDER_NAME);
         changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, sprintFolder);
      }
      return sprintFolder;
   }

   public static ArtifactId getTeamFolder(IAtsServices services, long teamUuid) {
      return services.getArtifact(teamUuid);
   }

   public static ArtifactId getOrCreateTopFeatureGroupFolder(IAtsServices services, long teamUuid, ArtifactId artifact, IAtsChangeSet changes) {
      ArtifactId teamFolder = AgileFolders.getTeamFolder(services, teamUuid);
      ArtifactId featureGroupFolder = null;
      for (ArtifactToken child : services.getRelationResolver().getChildren(teamFolder)) {
         if (child.getName().equals(IAgileService.FEATURE_GROUP_FOLDER_NAME)) {
            featureGroupFolder = child;
         }
      }
      if (featureGroupFolder == null) {
         featureGroupFolder = changes.createArtifact(CoreArtifactTypes.Folder, IAgileService.FEATURE_GROUP_FOLDER_NAME);
         changes.addChild(teamFolder, featureGroupFolder);
      }
      return featureGroupFolder;
   }

   public static ArtifactId getOrCreateTopAgileFolder(IAtsServices services, ArtifactId userArt, IAtsChangeSet changes) {
      ArtifactId agileFolder = services.getArtifact(AtsArtifactToken.TopAgileFolder);
      if (agileFolder == null) {
         agileFolder = changes.createArtifact(AtsArtifactToken.TopAgileFolder);
         ArtifactId rootArtifact = services.getArtifact(CoreArtifactTokens.DefaultHierarchyRoot);
         changes.addChild(rootArtifact, agileFolder);
      }
      return agileFolder;
   }

}
