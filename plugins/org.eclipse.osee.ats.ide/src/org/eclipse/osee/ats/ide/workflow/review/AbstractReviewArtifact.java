/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractReviewArtifact extends AbstractWorkflowArtifact implements IAtsAbstractReview {

   public AbstractReviewArtifact(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   public static List<AtsUser> getImplementersByState(AbstractWorkflowArtifact workflow, IStateToken state) {
      List<AtsUser> users = new ArrayList<>();
      if (workflow.isCancelled()) {
         users.add(workflow.getCancelledBy());
      } else {
         for (AtsUser user : workflow.getAssignees(state)) {
            if (!users.contains(user)) {
               users.add(user);
            }
         }
         if (workflow.isCompleted()) {
            AtsUser user = workflow.getCompletedBy();
            if (user != null && !users.contains(user)) {
               users.add(user);
            }
         }
      }
      return users;
   }

   public boolean isBlocking() {
      return getReviewBlockType() != ReviewBlockType.None;
   }

   public ReviewBlockType getReviewBlockType() {
      String typeStr = getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, null);
      if (typeStr == null) {
         return ReviewBlockType.None;
      }
      return ReviewBlockType.valueOf(typeStr);
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() {
      if (isStandAloneReview()) {
         return null;
      }
      if (parentAwa != null) {
         return parentAwa;
      }
      parentAwa = (AbstractWorkflowArtifact) getParentTeamWorkflow();
      return parentAwa;
   }

   @Override
   public IAtsAction getParentAction() {
      if (isStandAloneReview()) {
         return null;
      }
      if (parentAction != null) {
         return parentAction;
      }
      parentTeamArt = (TeamWorkFlowArtifact) getParentTeamWorkflow();
      if (parentTeamArt != null) {
         parentAction = parentTeamArt.getParentAction();
      }
      return parentAction;
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      if (isStandAloneReview() || isDeleted()) {
         return null;
      }
      if (parentTeamArt != null) {
         return parentTeamArt;
      }
      List<TeamWorkFlowArtifact> teams =
         getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow, TeamWorkFlowArtifact.class);
      if (teams.size() > 1) {
         OseeLog.log(Activator.class, Level.SEVERE,
            getArtifactTypeName() + " " + getAtsId() + " has multiple parent workflows");
      } else if (!isStandAloneReview() && teams.isEmpty()) {
         if (!isDeleted()) {
            OseeLog.log(Activator.class, Level.SEVERE,
               getArtifactTypeName() + " " + getAtsId() + " has no parent workflow");
         }
      }
      if (!teams.isEmpty()) {
         parentTeamArt = teams.iterator().next();
      }
      return parentTeamArt;
   }

   public Artifact getArtifact() {
      return this;
   }

   public static AbstractReviewArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractReviewArtifact) {
         return (AbstractReviewArtifact) artifact;
      }
      return null;
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() {
      return AtsApiService.get().getActionableItemService().getActionableItems(this);
   }

   @Override
   public String getRelatedToState() {
      return getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "");
   }

   @Override
   public boolean isStandAloneReview() {
      return !getActionableItems().isEmpty();
   }

   @Override
   public BooleanState isParentAtsArtifactLoaded() {
      if (isStandAloneReview()) {
         return BooleanState.UnSet;
      }
      return parentAwa == null ? BooleanState.No : BooleanState.Yes;
   }

}
