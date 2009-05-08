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

package org.eclipse.osee.ats.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewJob extends Job {

   private final TeamWorkFlowArtifact teamParent;
   private final String againstState;
   private PeerToPeerReviewArtifact peerToPeerReviewArtifact;
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
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         peerToPeerReviewArtifact =
               teamParent.getSmaMgr().getReviewManager().createNewPeerToPeerReview(reviewTitle, againstState,
                     transaction);
         peerToPeerReviewArtifact.persistAttributesAndRelations(transaction);
         transaction.execute();

         AtsLib.openAtsAction(peerToPeerReviewArtifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Error creating PeerToPeer Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }
}
