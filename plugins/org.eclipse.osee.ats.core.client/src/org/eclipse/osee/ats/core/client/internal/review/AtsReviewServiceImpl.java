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
package org.eclipse.osee.ats.core.client.internal.review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.review.ValidateReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G Dunne
 */
public class AtsReviewServiceImpl implements IAtsReviewService {

   private final IAtsClient atsClient;

   public AtsReviewServiceImpl(IAtsClient atsClient) {
      this.atsClient = atsClient;
   }

   @Override
   public boolean isValidationReviewRequired(IAtsWorkItem workItem) throws OseeCoreException {
      boolean required = false;
      if (workItem.isTeamWorkflow()) {
         required = atsClient.getArtifact(workItem).getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false);
      }
      return required;
   }

   @Override
   public IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, IAtsUser transitionUser, IAtsChangeSet changes) throws OseeCoreException {
      return ValidateReviewManager.createValidateReview((TeamWorkFlowArtifact) teamWf, false, transitionDate,
         transitionUser, changes);
   }

   @Override
   public Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) teamWf);
   }

   @Override
   public ReviewBlockType getReviewBlockType(IAtsAbstractReview review) throws OseeCoreException {
      return ((AbstractReviewArtifact) review).getReviewBlockType();
   }

   @Override
   public boolean isStandAloneReview(IAtsAbstractReview review) {
      return ((AbstractReviewArtifact) review).isStandAloneReview();
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) {
      List<IAtsAbstractReview> reviews = new ArrayList<>();
      for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(
         (TeamWorkFlowArtifact) teamWf.getStoreObject())) {
         reviews.add(atsClient.getWorkItemFactory().getReview(reviewArt));
      }
      return reviews;
   }

}
