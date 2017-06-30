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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItem extends IAtsObject, HasAssignees {

   String getAtsId();

   IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException;

   IAtsStateManager getStateMgr();

   IAtsLog getLog();

   IAtsWorkDefinition getWorkDefinition() throws OseeCoreException;

   IAtsStateDefinition getStateDefinition() throws OseeCoreException;

   boolean isTask();

   boolean isReview();

   boolean isTeamWorkflow();

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

   boolean isGoal();

   boolean isInWork();

   boolean isCompleted();

   boolean isCompletedOrCancelled();

   boolean isCancelled();

   boolean isDecisionReview();

   boolean isPeerReview();

   void setStateMgr(IAtsStateManager stateMgr);

   void clearCaches();

   @Override
   default String toStringWithId() {
      return String.format("[%s]-[%s]-[%s]", getName(), getAtsId(), getId());
   }
}