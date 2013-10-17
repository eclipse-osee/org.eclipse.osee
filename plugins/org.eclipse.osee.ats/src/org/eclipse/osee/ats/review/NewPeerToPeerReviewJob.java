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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewJob extends Job {

   private final TeamWorkFlowArtifact teamParent;
   private final String againstState;
   private final String reviewTitle;

   public NewPeerToPeerReviewJob(TeamWorkFlowArtifact teamParent, String reviewTitle, String againstState) {
      super("Creating New PeerToPeer Review");
      this.teamParent = teamParent;
      this.againstState = againstState;
      this.reviewTitle = reviewTitle;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         AtsChangeSet changes = new AtsChangeSet("New Peer To Peer Review");
         PeerToPeerReviewArtifact peerArt =
            PeerToPeerReviewManager.createNewPeerToPeerReview(teamParent, reviewTitle, againstState, changes);
         changes.execute();

         AtsUtil.openATSAction(peerArt, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Error creating PeerToPeer Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }
}
