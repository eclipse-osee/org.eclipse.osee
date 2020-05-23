/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.workflow.transition;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TransitionResult {

   public static TransitionResult MUST_BE_TARGETED_FOR_VERSION =
      new TransitionResult("Actions must be targeted for a Version.  Please set \"Target Version\" before transition.");
   public static TransitionResult NO_WORKFLOWS_PROVIDED_FOR_TRANSITION =
      new TransitionResult("No Workflows provided for transition; aborting.");
   public static TransitionResult TO_STATE_CANT_BE_NULL =
      new TransitionResult("To-State can not be null for transition.");
   public static TransitionResult MUST_BE_ASSIGNED =
      new TransitionResult("You must be assigned to transition this workflow.\nContact Assignee.");
   public static TransitionResult TASK_CANT_TRANSITION_IF_PARENT_COMPLETED = new TransitionResult(
      "You can not transition a task that belongs to a completed Workflow.  Un-complete workflow first.");
   public static TransitionResult DELETE_WORKING_BRANCH_BEFORE_CANCEL =
      new TransitionResult("Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
   public static TransitionResult NOT_ALL_BRANCHES_COMMITTED =
      new TransitionResult("All branches must be configured, committed, or overridden before transitioning.");
   public static TransitionResult WORKING_BRANCH_BEING_COMMITTED =
      new TransitionResult("Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
   public static TransitionResult WORKING_BRANCH_EXISTS =
      new TransitionResult("Working Branch exists.\n\nPlease commit or delete working branch before transition.");
   public static TransitionResult CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED =
      new TransitionResult("Can not transition with \"Anonymous\", or \"OseeSystem\" user as assignee.");
   public static TransitionResult CAN_NOT_TRANSITION_AS_SYSTEM_USER =
      new TransitionResult("Can not transition as \"Anonymous\", or \"OseeSystem\".");
   public static TransitionResult COMPLETE_BLOCKING_REVIEWS =
      new TransitionResult("All Blocking Reviews must be completed before transition.");
   public static TransitionResult CANCEL_REVIEWS_BEFORE_CANCEL =
      new TransitionResult("All Reviews must be cancelled before Cancelling Workflow.");
   public static TransitionResult TASKS_NOT_COMPLETED =
      new TransitionResult("Tasks Must be Completed/Cancelled to Transition");
   public static TransitionResult WORKITEM_DELETED =
      new TransitionResult("Work Item has been deleted.  Transition is invalid.");

   private String details;
   private String exception;

   public TransitionResult() {
      // for jax-rs
   }

   public TransitionResult(String details, Exception ex) {
      if (Strings.isInValid(details) && ex == null) {
         throw new OseeArgumentException("Must have details or Exception");
      }
      this.details = details;
      if (ex != null) {
         this.exception = Lib.exceptionToString(ex);
      }
   }

   public TransitionResult(String details) {
      this(details, null);
   }

   public String getDetails() {
      return details;
   }

   @Override
   public String toString() {
      return getDetails();
   }

   public String getException() {
      return exception;
   }

   public void setDetails(String details) {
      this.details = details;
   }

   public void setException(String exception) {
      this.exception = exception;
   }

}
