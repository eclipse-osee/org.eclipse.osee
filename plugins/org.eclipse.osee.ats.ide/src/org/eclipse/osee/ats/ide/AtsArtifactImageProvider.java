/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.FavoritesManager;
import org.eclipse.osee.ats.ide.util.SubscribeManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.util.CoreImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   private static final Map<ArtifactImage, KeyedImage> keyedImageMap = new HashMap<>();
   private static AtsArtifactImageProvider provider = new AtsArtifactImageProvider();
   private static AtomicBoolean initRan = new AtomicBoolean(false);

   @Override
   public void init() {
      if (!initRan.getAndSet(true)) {

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
         for (ArtifactTypeToken artifactType : AtsArtifactTypes.TeamWorkflow.getAllDescendantTypes()) {
            ArtifactImageManager.registerOverrideImageProvider(this, artifactType);
         }
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
         AbstractWorkflowArtifact wfArt = (AbstractWorkflowArtifact) artifact;
         if (SubscribeManager.isSubscribed(wfArt, AtsApiService.get().getUserService().getCurrentUser())) {
            // was 8,6
            return ArtifactImageManager.setupImage(artifact, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (FavoritesManager.isFavorite(wfArt)) {
            // was 7,0
            return ArtifactImageManager.setupImage(artifact, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
      }

      return super.setupImage(artifact);
   }

}