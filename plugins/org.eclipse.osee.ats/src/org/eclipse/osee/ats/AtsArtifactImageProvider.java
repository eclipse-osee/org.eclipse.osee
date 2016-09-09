/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import org.eclipse.osee.ats.agile.AgileUtilClient;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.util.SubscribeManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.DecisionReview, AtsImage.DECISION_REVIEW, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Action, AtsImage.ACTION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Version, FrameworkImage.VERSION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Task, AtsImage.TASK, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.ActionableItem, AtsImage.ACTIONABLE_ITEM, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.TeamWorkflow, AtsImage.TEAM_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.TeamDefinition, AtsImage.TEAM_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.RuleDefinition, AtsImage.RULE_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.WorkDefinition, AtsImage.WORK_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Goal, AtsImage.GOAL, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.PeerToPeerReview, AtsImage.PEER_REVIEW, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Program, AtsImage.PROGRAM, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Insertion, AtsImage.INSERTION, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.InsertionActivity, AtsImage.INSERTION_ACTIVITY, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.AgileTeam, AtsImage.AGILE_TEAM, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.AgileFeatureGroup, AtsImage.AGILE_FEATURE_GROUP, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.AgileSprint, AtsImage.AGILE_SPRINT, this);
      ArtifactImageManager.registerBaseImage(AtsArtifactTypes.WorkPackage, AtsImage.WORK_PACKAGE, this);

      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Version);
      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Task);
      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.PeerToPeerReview);
      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.DecisionReview);
      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Goal);
      ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.AgileSprint);
      for (IArtifactType artifactType : AtsClientService.get().getStoreService().getTeamWorkflowArtifactTypes()) {
         ArtifactImageManager.registerOverrideImageProvider(this, artifactType);
      }
   }

   @Override
   public String setupImage(IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (aArtifact.isOfType(AtsArtifactTypes.Version)) {
         if (aArtifact.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false)) {
            return ArtifactImageManager.setupImage(aArtifact, AtsImage.NEXT, Location.BOT_RIGHT);
         }
         if (aArtifact.getSoleAttributeValue(AtsAttributeTypes.Released, false)) {
            return ArtifactImageManager.setupImage(aArtifact, AtsImage.RELEASED, Location.TOP_RIGHT);
         }
         if (aArtifact.getSoleAttributeValue(AtsAttributeTypes.VersionLocked,
            false) && !aArtifact.getSoleAttributeValue(AtsAttributeTypes.Released, false)) {
            return ArtifactImageManager.setupImage(aArtifact, AtsImage.VERSION_LOCKED, Location.BOT_RIGHT);
         }
      }

      if (artifact instanceof GoalArtifact) {
         GoalArtifact goalArt = (GoalArtifact) artifact;
         KeyedImage keyedImage = AtsImage.GOAL;
         if (AgileUtilClient.isBacklog(goalArt)) {
            keyedImage = AtsImage.AGILE_BACKLOG;
         }
         if (SubscribeManager.isSubscribed(goalArt, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 8,6
            return ArtifactImageManager.setupImage(keyedImage, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (FavoritesManager.isFavorite(goalArt, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 7,0
            return ArtifactImageManager.setupImage(keyedImage, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
         return ArtifactImageManager.setupImage(keyedImage);
      }
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact stateMachine = (AbstractWorkflowArtifact) artifact;
         if (SubscribeManager.isSubscribed(stateMachine, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 8,6
            return ArtifactImageManager.setupImage(aArtifact, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (FavoritesManager.isFavorite(stateMachine, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 7,0
            return ArtifactImageManager.setupImage(aArtifact, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
      }

      return super.setupImage(artifact);
   }

}