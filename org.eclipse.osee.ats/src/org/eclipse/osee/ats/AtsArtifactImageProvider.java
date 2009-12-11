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

import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerBaseImage(DecisionReviewArtifact.ARTIFACT_NAME, AtsImage.REVIEW, this);
      ImageManager.registerBaseImage("Action", AtsImage.ACTION, this);
      ImageManager.registerBaseImage("Version", FrameworkImage.VERSION, this);
      ImageManager.registerBaseImage("Task", AtsImage.TASK, this);
      ImageManager.registerBaseImage("Actionable Item", AtsImage.ACTIONABLE_ITEM, this);
      ImageManager.registerBaseImage("Team Workflow", AtsImage.TEAM_WORKFLOW, this);
      ImageManager.registerBaseImage("Team Definition", AtsImage.TEAM_DEFINITION, this);
      ImageManager.registerBaseImage("Goal", AtsImage.GOAL, this);
      ImageManager.registerBaseImage("PeerToPeer Review", AtsImage.REVIEW, this);

      ImageManager.registerOverrideImageProvider(this, AtsArtifactTypes.Version.getName());
      ImageManager.registerOverrideImageProvider(this, TaskArtifact.ARTIFACT_NAME);
      ImageManager.registerOverrideImageProvider(this, PeerToPeerReviewArtifact.ARTIFACT_NAME);
      ImageManager.registerOverrideImageProvider(this, DecisionReviewArtifact.ARTIFACT_NAME);
      for (IArtifactType artifactType : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactTypes()) {
         ImageManager.registerOverrideImageProvider(this, artifactType.getName());
      }
   }

   @Override
   public String setupImage(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         if (artifact.getSoleAttributeValue("ats.Next Version", false)) {
            return ImageManager.setupImage(artifact, AtsImage.NEXT, Location.BOT_RIGHT);
         }
         if (artifact.getSoleAttributeValue("ats.Released", false)) {
            return ImageManager.setupImage(artifact, AtsImage.RELEASED, Location.TOP_RIGHT);
         }
         if (artifact.getSoleAttributeValue("ats.Version Locked", false) && !artifact.getSoleAttributeValue(
               "ats.Released", false)) {
            return ImageManager.setupImage(artifact, AtsImage.VERSION_LOCKED, Location.BOT_RIGHT);
         }
      }

      if (artifact instanceof StateMachineArtifact) {
         StateMachineArtifact stateMachine = (StateMachineArtifact) artifact;
         if (stateMachine.isSubscribed(UserManager.getUser())) {
            // was 8,6
            return ImageManager.setupImage(artifact, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (stateMachine.isFavorite(UserManager.getUser())) {
            // was 7,0
            return ImageManager.setupImage(artifact, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
      }

      return super.setupImage(artifact);
   }

}