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
import org.eclipse.osee.framework.core.data.ArtifactTypeId;

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

   default boolean isTeamWorkflow() {
      return isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   boolean isOfType(ArtifactTypeId... artifactType);

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
      return isOfType(AtsArtifactTypes.ReviewArtifact);
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
      return String.format("[%s]-[%s]-[%s]", getName(), getAtsId(), getId());
   }
}