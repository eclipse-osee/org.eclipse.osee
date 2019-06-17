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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
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

   public static ArtifactId getOrCreateTopSprintFolder(AtsApi atsApi, long teamId, IAtsChangeSet changes) {
      ArtifactId teamFolder = getTeamFolder(atsApi, teamId);
      ArtifactId sprintFolder = null;
      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(teamFolder)) {
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

   public static ArtifactId getTeamFolder(AtsApi atsApi, long teamId) {
      return atsApi.getQueryService().getArtifact(teamId);
   }

   public static ArtifactId getOrCreateTopFeatureGroupFolder(AtsApi atsApi, long teamId, ArtifactId artifact, IAtsChangeSet changes) {
      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, teamId);
      ArtifactId featureGroupFolder = null;
      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(teamFolder)) {
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

   public static ArtifactId getOrCreateTopAgileFolder(AtsApi atsApi, ArtifactId userArt, IAtsChangeSet changes) {
      ArtifactId agileFolder = atsApi.getQueryService().getArtifact(AtsArtifactToken.TopAgileFolder);
      if (agileFolder == null) {
         agileFolder = changes.createArtifact(AtsArtifactToken.TopAgileFolder);
         ArtifactId rootArtifact = atsApi.getQueryService().getArtifact(CoreArtifactTokens.DefaultHierarchyRoot);
         List<ArtifactId> children = new ArrayList<ArtifactId>();
         children.add(agileFolder);
         children.addAll(atsApi.getRelationResolver().getChildren(rootArtifact));
         changes.setRelationsAndOrder(rootArtifact, CoreRelationTypes.Default_Hierarchical__Child, children);
      }
      return agileFolder;
   }

}
