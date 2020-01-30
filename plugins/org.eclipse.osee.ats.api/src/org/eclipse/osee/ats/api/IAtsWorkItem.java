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
package org.eclipse.osee.ats.api;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItem extends IAtsObject, HasAssignees {

   String getAtsId();

   IAtsTeamWorkflow getParentTeamWorkflow();

   IAtsStateManager getStateMgr();

   IAtsLog getLog();

   IAtsWorkDefinition getWorkDefinition();

   IAtsStateDefinition getStateDefinition();

   IAtsUser getCreatedBy();

   Date getCreatedDate();

   IAtsUser getCompletedBy();

   IAtsUser getCancelledBy();

   String getCompletedFromState();

   String getCancelledFromState();

   String getArtifactTypeName();

   Date getCompletedDate();

   Date getCancelledDate();

   String getCancelledReason();

   IAtsAction getParentAction();

   IAtsWorkItem SENTINEL = createSentinel();

   default boolean isTeamWorkflow() {
      return isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   default boolean isDecisionReview() {
      return isOfType(AtsArtifactTypes.DecisionReview);
   }

   default boolean isPeerReview() {
      return isOfType(AtsArtifactTypes.PeerToPeerReview);
   }

   default boolean isTask() {
      return isOfType(AtsArtifactTypes.Task);
   }

   default boolean isReview() {
      return isOfType(AtsArtifactTypes.AbstractReview);
   }

   default boolean isGoal() {
      return this instanceof IAtsGoal;
   }

   default boolean isInWork() {
      return getStateDefinition().getStateType().isWorkingState();
   }

   default boolean isCompleted() {
      return getStateDefinition().getStateType().isCompletedState();
   }

   default boolean isCancelled() {
      return getStateDefinition().getStateType().isCancelledState();
   }

   default boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   void setStateMgr(IAtsStateManager stateMgr);

   void clearCaches();

   @Override
   default String toStringWithId() {
      return String.format("[%s]-[%s]-[%s]", getName(), getAtsId(), getIdString());

   }

   public static IAtsWorkItem createSentinel() {
      final class IAtsWorkItemSentinel extends NamedIdBase implements IAtsWorkItem {

         @Override
         public ArtifactTypeToken getArtifactType() {
            return null;
         }

         @Override
         public List<IAtsUser> getAssignees() {
            return null;
         }

         @Override
         public List<IAtsUser> getImplementers() {
            return null;
         }

         @Override
         public String getAtsId() {
            return null;
         }

         @Override
         public IAtsTeamWorkflow getParentTeamWorkflow() {
            return null;
         }

         @Override
         public IAtsStateManager getStateMgr() {
            return null;
         }

         @Override
         public IAtsLog getLog() {
            return null;
         }

         @Override
         public IAtsWorkDefinition getWorkDefinition() {
            return null;
         }

         @Override
         public IAtsStateDefinition getStateDefinition() {
            return null;
         }

         @Override
         public IAtsUser getCreatedBy() {
            return null;
         }

         @Override
         public Date getCreatedDate() {
            return null;
         }

         @Override
         public IAtsUser getCompletedBy() {
            return null;
         }

         @Override
         public IAtsUser getCancelledBy() {
            return null;
         }

         @Override
         public String getCompletedFromState() {
            return null;
         }

         @Override
         public String getCancelledFromState() {
            return null;
         }

         @Override
         public String getArtifactTypeName() {
            return null;
         }

         @Override
         public Date getCompletedDate() {
            return null;
         }

         @Override
         public Date getCancelledDate() {
            return null;
         }

         @Override
         public String getCancelledReason() {
            return null;
         }

         @Override
         public IAtsAction getParentAction() {
            return null;
         }

         @Override
         public void setStateMgr(IAtsStateManager stateMgr) {
            // do nothing
         }

         @Override
         public void clearCaches() {
            // do nothing
         }

         @Override
         public AtsApi getAtsApi() {
            return null;
         }

      }
      return new IAtsWorkItemSentinel();
   }

}