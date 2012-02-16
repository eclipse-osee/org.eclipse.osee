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
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.column.ReviewFormalTypeColumn;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewFormalType;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.util.AtsCacheManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ReviewWorldSearchItem extends WorldUISearchItem {

   private Collection<ActionableItemArtifact> aias;
   private final boolean recurseChildren;
   private boolean includeCompleted;
   private final Collection<String> aiNames;
   private final Artifact versionArt;
   private final User userArt;
   private final boolean includeCancelled;
   private final String stateName;
   private final ReviewFormalType reviewFormalType;
   private final ReviewType reviewType;

   public ReviewWorldSearchItem(String displayName, List<String> aiNames, boolean includeCompleted, boolean includeCancelled, boolean recurseChildren, Artifact versionArt, User userArt, ReviewFormalType reviewFormalType, ReviewType reviewType, String stateName) {
      super(displayName, AtsImage.REVIEW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.aiNames = aiNames;
      this.includeCompleted = includeCompleted;
      this.recurseChildren = recurseChildren;
      this.reviewFormalType = reviewFormalType;
      this.reviewType = reviewType;
      this.stateName = stateName;
   }

   public ReviewWorldSearchItem(String displayName, Collection<ActionableItemArtifact> aias, boolean includeCompleted, boolean includeCancelled, boolean recurseChildren, Artifact versionArt, User userArt, ReviewFormalType reviewFormalType, ReviewType reviewType, String stateName) {
      super(displayName, AtsImage.REVIEW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.recurseChildren = recurseChildren;
      this.stateName = stateName;
      this.aiNames = null;
      this.aias = aias;
      this.includeCompleted = includeCompleted;
      this.reviewFormalType = reviewFormalType;
      this.reviewType = reviewType;
   }

   public ReviewWorldSearchItem(ReviewWorldSearchItem reviewWorldUISearchItem) {
      super(reviewWorldUISearchItem, AtsImage.REVIEW);
      this.versionArt = null;
      this.userArt = null;
      this.recurseChildren = reviewWorldUISearchItem.recurseChildren;
      this.aiNames = reviewWorldUISearchItem.aiNames;
      this.aias = reviewWorldUISearchItem.aias;
      this.includeCompleted = reviewWorldUISearchItem.includeCompleted;
      this.includeCancelled = reviewWorldUISearchItem.includeCancelled;
      this.stateName = reviewWorldUISearchItem.stateName;
      this.reviewFormalType = reviewWorldUISearchItem.reviewFormalType;
      this.reviewType = reviewWorldUISearchItem.reviewType;
   }

   public Collection<String> getProductSearchName() {
      if (aiNames != null) {
         return aiNames;
      } else if (aias != null) {
         return Artifacts.getNames(aias);
      }
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   /**
    * Loads all actionable items if specified by name versus by AI class
    */
   public void getAIs() {
      if (aiNames != null && aias == null) {
         aias = new HashSet<ActionableItemArtifact>();
         for (String teamDefName : aiNames) {
            ActionableItemArtifact aia =
               (ActionableItemArtifact) AtsCacheManager.getSoleArtifactByName(AtsArtifactTypes.ActionableItem,
                  teamDefName);
            if (aia != null) {
               aias.add(aia);
            }
         }
      } else if (aiNames == null && aias == null) {
         aias = new HashSet<ActionableItemArtifact>();
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      getAIs();
      Set<String> actionableItemGuids = new HashSet<String>(aias.size());
      for (ActionableItemArtifact aia : aias) {
         if (recurseChildren) {
            for (ActionableItemArtifact childTeamDef : ActionableItemManagerCore.getActionableItemsFromItemAndChildren(aia)) {
               actionableItemGuids.add(childTeamDef.getGuid());
            }
         } else {
            actionableItemGuids.add(aia.getGuid());
         }
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
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
         if (team != null && (team.getTargetedVersion() == null || !team.getTargetedVersion().equals(versionArt))) {
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

   public static void addIncludeCompletedCancelledCriteria(List<AbstractArtifactSearchCriteria> criteria, boolean includeCompleted, boolean includeCancelled) throws OseeCoreException {
      try {
         if (AttributeTypeManager.getType(AtsAttributeTypes.CurrentStateType) != null) {
            if (!includeCancelled && !includeCompleted) {
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, WorkPageType.Working.name()));
            } else {
               List<String> cancelOrComplete = new ArrayList<String>(2);
               cancelOrComplete.add(WorkPageType.Working.name());
               if (includeCompleted) {
                  cancelOrComplete.add(WorkPageType.Completed.name());
               }
               if (includeCancelled) {
                  cancelOrComplete.add(WorkPageType.Cancelled.name());
               }
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, cancelOrComplete));
            }
         }
      } catch (OseeTypeDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         // Backward compatibility; remove after 0.9.7 release
         List<String> cancelOrComplete = new ArrayList<String>(2);
         if (!includeCancelled) {
            cancelOrComplete.add(TeamState.Cancelled.getPageName() + ";;;");
         }
         if (!includeCompleted) {
            cancelOrComplete.add(TeamState.Completed.getPageName() + ";;;");
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
   public WorldUISearchItem copy() {
      return new ReviewWorldSearchItem(this);
   }

}
