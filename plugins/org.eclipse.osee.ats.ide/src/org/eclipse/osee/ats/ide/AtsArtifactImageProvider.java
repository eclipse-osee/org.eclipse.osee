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
package org.eclipse.osee.ats.ide;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.FavoritesManager;
import org.eclipse.osee.ats.ide.util.SubscribeManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.CoreImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   private static final Map<ArtifactImage, KeyedImage> keyedImageMap = new HashMap<>();
   private static AtsArtifactImageProvider provider = new AtsArtifactImageProvider();
   private static Boolean initRan = false;

   @Override
   public synchronized void init() {
      if (!initRan) {
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.DecisionReview, AtsImage.DECISION_REVIEW, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Action, AtsImage.ACTION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Version, FrameworkImage.VERSION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Task, AtsImage.TASK, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.ActionableItem, AtsImage.ACTIONABLE_ITEM, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.TeamDefinition, AtsImage.TEAM_DEFINITION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.RuleDefinition, AtsImage.RULE_DEFINITION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.WorkDefinition, AtsImage.WORK_DEFINITION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Goal, AtsImage.GOAL, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.PeerToPeerReview, AtsImage.PEER_REVIEW, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Program, AtsImage.PROGRAM, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.Insertion, AtsImage.INSERTION, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.InsertionActivity, AtsImage.INSERTION_ACTIVITY, this);
         ArtifactImageManager.registerBaseImage(AtsArtifactTypes.WorkPackage, AtsImage.WORK_PACKAGE, this);

         for (ArtifactImage artImage : AtsArtifactImages.getImages()) {
            CoreImage keyedImage = new CoreImage(Activator.PLUGIN_ID, artImage.getImageName());
            keyedImageMap.put(artImage, keyedImage);
            ArtifactImageManager.registerBaseImage(artImage.getArtifactType(), keyedImage, this);
         }

         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Version);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Task);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.PeerToPeerReview);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.DecisionReview);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Goal);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.AgileBacklog);
         ArtifactImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.AgileSprint);
         for (ArtifactTypeToken artifactType : AtsClientService.get().getStoreService().getTeamWorkflowArtifactTypes()) {
            ArtifactImageManager.registerOverrideImageProvider(this, artifactType);
         }
         initRan = true;
      }
   }

   public static KeyedImage getKeyedImage(ArtifactImage artifactImage) {
      provider.init();
      return provider.getImage(artifactImage);
   }

   public KeyedImage getImage(ArtifactImage artifactImage) {
      return keyedImageMap.get(artifactImage);
   }

   @Override
   public String setupImage(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         if (artifact.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false)) {
            return ArtifactImageManager.setupImage(artifact, AtsImage.NEXT, Location.BOT_RIGHT);
         }
         if (artifact.getSoleAttributeValue(AtsAttributeTypes.Released, false)) {
            return ArtifactImageManager.setupImage(artifact, AtsImage.RELEASED, Location.TOP_RIGHT);
         }
         if (artifact.getSoleAttributeValue(AtsAttributeTypes.VersionLocked,
            false) && !artifact.getSoleAttributeValue(AtsAttributeTypes.Released, false)) {
            return ArtifactImageManager.setupImage(artifact, AtsImage.VERSION_LOCKED, Location.BOT_RIGHT);
         }
      }
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact stateMachine = (AbstractWorkflowArtifact) artifact;
         if (SubscribeManager.isSubscribed(stateMachine, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 8,6
            return ArtifactImageManager.setupImage(artifact, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (FavoritesManager.isFavorite(stateMachine, AtsClientService.get().getUserService().getCurrentUser())) {
            // was 7,0
            return ArtifactImageManager.setupImage(artifact, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
      }

      return super.setupImage(artifact);
   }

}