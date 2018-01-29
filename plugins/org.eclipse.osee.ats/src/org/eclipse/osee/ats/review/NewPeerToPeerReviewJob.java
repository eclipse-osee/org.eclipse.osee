/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.review;

import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewJob extends Job {

   private final TeamWorkFlowArtifact teamParent;
   private final String againstState;
   private final String reviewTitle;
   private ReviewBlockType blockType;
   private ReviewFormalType reviewType;
   private IAtsActionableItem actionableItem;

   public NewPeerToPeerReviewJob(TeamWorkFlowArtifact teamParent, String reviewTitle, String againstState) {
      super("Creating New PeerToPeer Review");
      this.teamParent = teamParent;
      this.againstState = againstState;
      this.reviewTitle = reviewTitle;
   }

   public NewPeerToPeerReviewJob(TeamWorkFlowArtifact teamParent, IAtsActionableItem actionableItem, String reviewTitle, String againstState, ReviewBlockType blockType, ReviewFormalType reviewType) {
      super("Creating New PeerToPeer Review");
      this.teamParent = teamParent;
      this.actionableItem = actionableItem;
      this.againstState = againstState;
      this.reviewTitle = reviewTitle;
      this.blockType = blockType;
      this.reviewType = reviewType;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         if (teamParent != null && actionableItem != null) {
            throw new OseeArgumentException("Either Team Workflow or Actionable Item must be null");
         }
         IAtsChangeSet changes = AtsClientService.get().createChangeSet("New Peer To Peer Review");
         PeerToPeerReviewArtifact peerArt = null;
         if (teamParent != null) {
            peerArt = PeerToPeerReviewManager.createNewPeerToPeerReview(teamParent, reviewTitle, againstState, changes);
         } else {
            peerArt = PeerToPeerReviewManager.createNewPeerToPeerReview(actionableItem, reviewTitle, null, new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), changes);
         }
         if (blockType != null) {
            changes.setSoleAttributeValue((ArtifactId) peerArt, AtsAttributeTypes.ReviewBlocks, blockType.name());
         }
         if (reviewType != null) {
            changes.setSoleAttributeValue((ArtifactId) peerArt, AtsAttributeTypes.ReviewFormalType, reviewType.name());
         }
         changes.execute();

         AtsEditors.openATSAction(peerArt, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Error creating PeerToPeer Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }
}
