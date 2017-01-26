/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.util.IArtifactProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsReviewServiceImpl implements IAtsReviewService {

   private final IArtifactProvider artifactProvider;
   private final IAtsWorkItemService workItemService;
   private final IAtsServer atsServer;

   public AtsReviewServiceImpl(IAtsServer atsServer, IArtifactProvider artifactProvider, IAtsWorkItemService workItemService) {
      this.atsServer = atsServer;
      this.artifactProvider = artifactProvider;
      this.workItemService = workItemService;
   }

   @Override
   public boolean isValidationReviewRequired(IAtsWorkItem workItem) throws OseeCoreException {
      boolean required = false;
      if (workItem.isTeamWorkflow()) {
         required =
            artifactProvider.getArtifact(workItem).getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false);
      }
      return required;
   }

   @Override
   public IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, IAtsUser transitionUser, IAtsChangeSet changes) throws OseeCoreException {
      // TODO Implement this
      throw new OseeStateException("Not Implemented Yet");
   }

   @Override
   public Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return workItemService.getReviews(teamWf, teamWf.getStateMgr().getCurrentState());
   }

   @Override
   public ReviewBlockType getReviewBlockType(IAtsAbstractReview review) throws OseeCoreException {
      String blockStr = artifactProvider.getArtifact(review).getSoleAttributeAsString(AtsAttributeTypes.ReviewBlocks,
         ReviewBlockType.None.name());
      return ReviewBlockType.valueOf(blockStr);
   }

   @Override
   public boolean isStandAloneReview(IAtsAbstractReview review) {
      return artifactProvider.getArtifact(review).getAttributeCount(AtsAttributeTypes.ActionableItem) > 0;
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) {
      List<IAtsAbstractReview> reviews = new ArrayList<>();

      for (ArtifactReadable reviewArt : ((ArtifactReadable) teamWf.getStoreObject()).getRelated(
         AtsRelationTypes.TeamWorkflowToReview_Review)) {
         reviews.add(atsServer.getWorkItemFactory().getReview(reviewArt));
      }
      return reviews;
   }

   @Override
   public IAtsPeerReviewRoleManager createPeerReviewRoleManager(IAtsPeerToPeerReview peerRev) {
      return new UserRoleManager(peerRev, atsServer);
   }

}
