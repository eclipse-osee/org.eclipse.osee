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
package org.eclipse.osee.ats.api.workflow.transition;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTransitionManager {

   public abstract TransitionResults handleAll();

   /**
    * Validate AbstractWorkflowArtifact for transition including checking widget validation, rules, assignment, etc.
    * 
    * @return Result.isFalse if failure
    */
   public abstract void handleTransitionValidation(TransitionResults results);

   public abstract void isTransitionValidForExtensions(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef);

   /**
    * Request extra information if transition requires hours spent prompt, cancellation reason, etc.
    * 
    * @return Result.isFalse if failure or Result.isCancelled if canceled
    */
   public abstract void handleTransitionUi(TransitionResults results);

   /**
    * Process transition and persist changes to given skynet transaction
    * 
    * @return Result.isFalse if failure
    */
   public abstract void handleTransition(TransitionResults results);

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public abstract IAtsUser getTransitionAsUser() ;

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public abstract Date getTransitionOnDate();

   public abstract void setTransitionOnDate(Date transitionOnDate);

   /**
    * Get transition to assignees. Verify that UnAssigned is not selected with another assignee. Ensure an assignee is
    * entered, else use current user or UnAssigneed if current user is SystemUser.
    */
   public abstract List<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem, IAtsStateDefinition toState) ;

   public abstract TransitionResults handleAllAndPersist();

}