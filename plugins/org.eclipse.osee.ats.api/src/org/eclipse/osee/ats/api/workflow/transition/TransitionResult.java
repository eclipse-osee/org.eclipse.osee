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

   public static final TransitionResult REVIEW_REQUIRES_MEETING_ATTENDEES =
      new TransitionResult("Peer Review requires Meeting Attendee(s)");
   public static final TransitionResult MUST_BE_TARGETED_FOR_VERSION =
      new TransitionResult("Actions must be targeted for a Version.  Please set \"Target Version\" before transition.");
   public static final TransitionResult MUST_HAVE_ASSIGNEE =
      new TransitionResult("[Assignee] is required for transition");
   public static final TransitionResult NO_WORKFLOWS_PROVIDED_FOR_TRANSITION =
      new TransitionResult("No Workflows provided for transition; aborting.");
   public static final TransitionResult TO_STATE_CANT_BE_NULL =
      new TransitionResult("To-State can not be null for transition.");
   public static final TransitionResult UNABLE_TO_ASSIGN = new TransitionResult("Unable to Assign this workflow.");
   public static final TransitionResult TASK_CANT_TRANSITION_IF_PARENT_COMPLETED = new TransitionResult(
      "You can not transition a task that belongs to a completed Workflow.  Un-complete workflow first.");
   public static final TransitionResult DELETE_WORKING_BRANCH_BEFORE_CANCEL =
      new TransitionResult("Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
   public static final TransitionResult NOT_ALL_BRANCHES_COMMITTED =
      new TransitionResult("All branches must be configured, committed, or overridden before transitioning.");
   public static final TransitionResult WORKING_BRANCH_BEING_COMMITTED =
      new TransitionResult("Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
   public static final TransitionResult WORKING_BRANCH_EXISTS =
      new TransitionResult("Working Branch exists.\n\nPlease commit or delete working branch before transition.");
   public static final TransitionResult CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED =
      new TransitionResult("Can not transition with \"Anonymous\", or \"OseeSystem\" user as assignee.");
   public static final TransitionResult CAN_NOT_TRANSITION_AS_SYSTEM_USER =
      new TransitionResult("Can not transition as \"Anonymous\", or \"OseeSystem\".");
   public static final TransitionResult COMPLETE_BLOCKING_REVIEWS =
      new TransitionResult("All Blocking Reviews must be completed before transition.");
   public static final TransitionResult CANCEL_REVIEWS_BEFORE_CANCEL =
      new TransitionResult("All Reviews must be cancelled before Cancelling Workflow.");
   public static final TransitionResult TASKS_NOT_COMPLETED =
      new TransitionResult("Tasks Must be Completed/Cancelled to Transition");
   public static final TransitionResult WORKITEM_DELETED =
      new TransitionResult("Work Item has been deleted.  Transition is invalid.");
   public static final TransitionResult REVIEW_ROLES_NOT_COMPLETED =
      new TransitionResult("Peer Review Roles must be completed.");
   public static final TransitionResult REVIEW_DEFECTS_NOT_CLOSED =
      new TransitionResult("Peer Review Defects must be closed.");

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
      this(details, (Exception) null);
   }

   public TransitionResult(String format, Object... args) {
      this(String.format(format, args), (Exception) null);
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
