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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.column.ReviewFormalTypeColumn;
import org.eclipse.osee.ats.core.client.review.ReviewFormalType;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Donald G. Dunne
 */
public class ReviewWorldSearchItem extends WorldUISearchItem {

   private final Collection<IAtsActionableItem> aias;
   private final boolean recurseChildren;
   private boolean includeCompleted;
   private final IAtsVersion versionArt;
   private final IAtsUser userArt;
   private final boolean includeCancelled;
   private final String stateName;
   private final ReviewFormalType reviewFormalType;
   private final ReviewType reviewType;

   public ReviewWorldSearchItem(String displayName, Collection<IAtsActionableItem> aias, boolean includeCompleted, boolean includeCancelled, boolean recurseChildren, IAtsVersion versionArt, IAtsUser userArt, ReviewFormalType reviewFormalType, ReviewType reviewType, String stateName) throws OseeCoreException {
      super(displayName, AtsImage.REVIEW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.recurseChildren = recurseChildren;
      this.stateName = stateName;
      this.aias = aias;
      Conditions.checkNotNull(aias, "Actionable Items");
      this.includeCompleted = includeCompleted;
      this.reviewFormalType = reviewFormalType;
      this.reviewType = reviewType;
   }

   public ReviewWorldSearchItem(ReviewWorldSearchItem reviewWorldUISearchItem) throws OseeCoreException {
      super(reviewWorldUISearchItem, AtsImage.REVIEW);
      this.versionArt = null;
      this.userArt = null;
      this.recurseChildren = reviewWorldUISearchItem.recurseChildren;
      this.aias = reviewWorldUISearchItem.aias;
      Conditions.checkNotNull(aias, "Actionable Items");
      this.includeCompleted = reviewWorldUISearchItem.includeCompleted;
      this.includeCancelled = reviewWorldUISearchItem.includeCancelled;
      this.stateName = reviewWorldUISearchItem.stateName;
      this.reviewFormalType = reviewWorldUISearchItem.reviewFormalType;
      this.reviewType = reviewWorldUISearchItem.reviewType;
   }

   public Collection<String> getProductSearchName() {
      return ActionableItems.getNames(aias);
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<String> actionableItemGuids = new HashSet<String>(aias.size());
      for (IAtsActionableItem aia : aias) {
         if (recurseChildren) {
            for (IAtsActionableItem childTeamDef : ActionableItems.getActionableItemsFromItemAndChildren(aia)) {
               actionableItemGuids.add(childTeamDef.getGuid());
            }
         } else {
            actionableItemGuids.add(aia.getGuid());
         }
      }
      List<ArtifactSearchCriteria> criteria = new ArrayList<ArtifactSearchCriteria>();
      if (actionableItemGuids.isEmpty()) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.ActionableItem));
      } else {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.ActionableItem, actionableItemGuids));
      }

      addIncludeCompletedCancelledCriteria(criteria, includeCompleted, includeCancelled);

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromCriteria(AtsUtil.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultSet = new HashSet<Artifact>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.ReviewArtifact) && includeReview(art) && isCompletedCancelledValid(art)) {
            resultSet.add(art);
         }
         if (art instanceof TeamWorkFlowArtifact) {
            for (Artifact revArt : ReviewManager.getReviews((TeamWorkFlowArtifact) art)) {
               if (includeReview(revArt) && isCompletedCancelledValid(revArt)) {
                  resultSet.add(revArt);
               }
            }
         }
      }
      return WorkflowManager.filterState(stateName, resultSet);
   }

   // Because reviews can be stand-alone or attached to team workflow, the criteria above
   // doesn't necessarily filter out completed cancelled, make check again here
   public boolean isCompletedCancelledValid(Artifact artifact) throws OseeCoreException {
      if (artifact instanceof AbstractWorkflowArtifact) {
         if (!includeCancelled && ((AbstractWorkflowArtifact) artifact).isCancelled()) {
            return false;
         }
         if (!includeCompleted && ((AbstractWorkflowArtifact) artifact).isCompleted()) {
            return false;
         }
         return true;
      }
      return false;
   }

   public boolean includeReview(Artifact reviewArt) throws OseeCoreException {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) reviewArt;

      // don't include if artifact type doesn't match selected review type
      if (reviewType != null && ((reviewType == ReviewType.Decision && !reviewArt.isOfType(AtsArtifactTypes.DecisionReview)) || (reviewType == ReviewType.PeerToPeer && !reviewArt.isOfType(AtsArtifactTypes.PeerToPeerReview)))) {
         return false;
      }
      // don't include if userArt specified and userArt not assignee
      if (userArt != null && !awa.getStateMgr().getAssignees().contains(userArt)) {
         return false;
      }
      // don't include if version specified and workflow's not targeted for version
      if (versionArt != null) {
         TeamWorkFlowArtifact team = awa.getParentTeamWorkflow();
         IAtsVersion version = AtsVersionService.get().getTargetedVersion(team);
         if (team != null && (version == null || !version.equals(versionArt))) {
            return false;
         }
      }

      if (reviewFormalType != null) {
         ReviewFormalType reviewType = ReviewFormalTypeColumn.getReviewFormalType(reviewArt);
         if (reviewType == null || reviewFormalType != reviewType) {
            return false;
         }
      }
      return true;

   }

   public static void addIncludeCompletedCancelledCriteria(List<ArtifactSearchCriteria> criteria, boolean includeCompleted, boolean includeCancelled) throws OseeCoreException {
      try {
         if (AttributeTypeManager.getType(AtsAttributeTypes.CurrentStateType) != null) {
            if (!includeCancelled && !includeCompleted) {
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, StateType.Working.name()));
            } else {
               List<String> cancelOrComplete = new ArrayList<String>(2);
               cancelOrComplete.add(StateType.Working.name());
               if (includeCompleted) {
                  cancelOrComplete.add(StateType.Completed.name());
               }
               if (includeCancelled) {
                  cancelOrComplete.add(StateType.Cancelled.name());
               }
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, cancelOrComplete));
            }
         }
      } catch (OseeTypeDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         // Backward compatibility; remove after 0.9.7 release
         List<String> cancelOrComplete = new ArrayList<String>(2);
         if (!includeCancelled) {
            cancelOrComplete.add(TeamState.Cancelled.getName() + ";;;");
         }
         if (!includeCompleted) {
            cancelOrComplete.add(TeamState.Completed.getName() + ";;;");
         }
         if (cancelOrComplete.size() > 0) {
            criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentState, cancelOrComplete));
         }
      }
   }

   public void setShowFinished(boolean showFinished) {
      this.includeCompleted = showFinished;
   }

   @Override
   public WorldUISearchItem copy() throws OseeCoreException {
      return new ReviewWorldSearchItem(this);
   }

}
