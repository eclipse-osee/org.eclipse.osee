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
package org.eclipse.osee.ats.core.client.review;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ReviewManager {

   public ReviewManager() {
      super();
   }

   /**
    * Return Remain Hours for all reviews
    */
   public static double getRemainHours(TeamWorkFlowArtifact teamArt)  {
      double hours = 0;
      for (AbstractReviewArtifact reviewArt : getReviews(teamArt)) {
         hours += reviewArt.getRemainHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Estimated Review Hours of "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   public static double getEstimatedHours(TeamWorkFlowArtifact teamArt, IStateToken relatedToState)  {
      double hours = 0;
      for (AbstractReviewArtifact revArt : getReviews(teamArt, relatedToState)) {
         hours += revArt.getEstimatedHoursTotal();
      }
      return hours;
   }

   /**
    * Return Estimated Hours for all reviews
    */
   public static double getEstimatedHours(TeamWorkFlowArtifact teamArt)  {
      double hours = 0;
      for (AbstractReviewArtifact revArt : getReviews(teamArt)) {
         hours += revArt.getEstimatedHoursTotal();
      }
      return hours;

   }

   public static Collection<AbstractReviewArtifact> getReviews(IAtsTeamWorkflow teamWf)  {
      return ((TeamWorkFlowArtifact) teamWf.getStoreObject()).getRelatedArtifacts(
         AtsRelationTypes.TeamWorkflowToReview_Review, AbstractReviewArtifact.class);
   }

   public static Collection<IAtsAbstractReview> getReviewsFromCurrentState(TeamWorkFlowArtifact teamArt)  {
      return Collections.castAll(getReviews(teamArt, teamArt.getStateMgr().getCurrentState()));
   }

   public static Collection<AbstractReviewArtifact> getReviews(TeamWorkFlowArtifact teamArt, IStateToken state)  {
      Set<AbstractReviewArtifact> arts = new HashSet<>();
      for (AbstractReviewArtifact revArt : getReviews(teamArt)) {
         if (revArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "").equals(state.getName())) {
            arts.add(revArt);
         }
      }
      return arts;
   }

   public static boolean hasReviews(TeamWorkFlowArtifact teamArt)  {
      return teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowToReview_Review) > 0;
   }

   public static Result areReviewsComplete(TeamWorkFlowArtifact teamArt) {
      return areReviewsComplete(teamArt, true);
   }

   public static Result areReviewsComplete(TeamWorkFlowArtifact teamArt, boolean popup) {
      try {
         for (AbstractReviewArtifact reviewArt : getReviews(teamArt)) {
            if (!reviewArt.isCompleted() && reviewArt.isCancelled()) {
               return new Result("Not Complete");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return Result.TrueResult;
   }

   /**
    * Return Hours Spent for Reviews of "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   public static double getHoursSpent(TeamWorkFlowArtifact teamArt, IStateToken relatedToState)  {
      double spent = 0;
      for (AbstractReviewArtifact reviewArt : getReviews(teamArt, relatedToState)) {
         spent += HoursSpentUtil.getHoursSpentTotal(reviewArt, AtsClientService.get().getServices());
      }
      return spent;
   }

   /**
    * Return Total Percent Complete / # Reviews for "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   public static int getPercentComplete(TeamWorkFlowArtifact teamArt, IStateToken relatedToState)  {
      int spent = 0;
      Collection<AbstractReviewArtifact> reviewArts = getReviews(teamArt, relatedToState);
      for (AbstractReviewArtifact reviewArt : reviewArts) {
         spent += PercentCompleteTotalUtil.getPercentCompleteTotal(reviewArt, AtsClientService.get().getServices());
      }
      if (spent == 0) {
         return 0;
      }
      return spent / reviewArts.size();
   }

   public static AbstractReviewArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractReviewArtifact) {
         return (AbstractReviewArtifact) artifact;
      }
      return null;
   }

   public static int getPercentComplete(TeamWorkFlowArtifact teamWf) {
      int spent = 0;
      Collection<AbstractReviewArtifact> reviewArts = getReviews(teamWf);
      for (AbstractReviewArtifact reviewArt : reviewArts) {
         spent += PercentCompleteTotalUtil.getPercentCompleteTotal(reviewArt, AtsClientService.get().getServices());
      }
      if (spent == 0) {
         return 0;
      }
      return spent / reviewArts.size();

   }
}
