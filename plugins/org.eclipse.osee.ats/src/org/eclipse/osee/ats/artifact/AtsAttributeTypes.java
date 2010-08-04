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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.HasDescription;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 */
public class AtsAttributeTypes extends NamedIdentity implements IAttributeType, HasDescription {

   public static final AtsAttributeTypes ATS_ACTIVE = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "Active");
   public static final AtsAttributeTypes ATS_RESOLUTION = new AtsAttributeTypes("AAMFEdUMfV1KdbQNaKwA", "Resolution");
   public static final AtsAttributeTypes ATS_ROLE = new AtsAttributeTypes("AAMFEeCqMz0XCSBJ+IQA", "Role");
   public static final AtsAttributeTypes ATS_LOG = new AtsAttributeTypes("AAMFEdgB1DX3eJSZb0wA", "Log");
   public static final AtsAttributeTypes ATS_STATE = new AtsAttributeTypes("AAMFEdMa3wzVvp60xLQA", "State");
   public static final AtsAttributeTypes ATS_POINTS = new AtsAttributeTypes("AY2EeqhzcDEGtXtREkAA", "Points");
   public static final AtsAttributeTypes ATS_NUMERIC_1 = new AtsAttributeTypes("AABY2xxQsDm811kCViwA", "Numeric1");
   public static final AtsAttributeTypes ATS_NUMERIC_2 = new AtsAttributeTypes("AABiRtvZsAEkU4BS9qwA", "Numeric2");
   public static final AtsAttributeTypes ATS_CATEGORY_1 = new AtsAttributeTypes("AAMFEdrYniOQYrYUKKQA", "Category");
   public static final AtsAttributeTypes ATS_CATEGORY_2 = new AtsAttributeTypes("AAMFEdthBkolbJKLXuAA", "Category2");
   public static final AtsAttributeTypes ATS_CATEGORY_3 = new AtsAttributeTypes("AAMFEd06oxr8LMzZxdgA", "Category3");
   public static final AtsAttributeTypes ATS_ACTIONABLE = new AtsAttributeTypes("AAMFEcvDtBiaJ3TMatAA", "Actionable");
   public static final AtsAttributeTypes ATS_PROBLEM = new AtsAttributeTypes("AAMFEdQUxRyevvTu+bwA", "Problem");
   public static final AtsAttributeTypes ATS_DESCRIPTION = new AtsAttributeTypes("AAMFEdWJ_ChxX6+YKbwA", "Description");
   public static final AtsAttributeTypes ATS_FULL_NAME = new AtsAttributeTypes("AAMFEdZI9XLT34cTonAA", "Full Name");
   public static final AtsAttributeTypes ATS_LOCATION = new AtsAttributeTypes("AAMFEeAW4QBlesdfacwA", "Location");
   public static final AtsAttributeTypes ATS_CHANGE_TYPE = new AtsAttributeTypes("AAMFEc+MwGHnPCv7HlgA", "Change Type");
   public static final AtsAttributeTypes ATS_PRIORITY_TYPE = new AtsAttributeTypes("AAMFEc8JzH1U6XGD59QA", "Priority");
   public static final AtsAttributeTypes ATS_NEED_BY = new AtsAttributeTypes("AAMFEcxAGzHAKfDNAIwA", "Need By");
   public static final AtsAttributeTypes ATS_SMA_NOTE = new AtsAttributeTypes("AAMFEdm7ywte8qayfbAA", "SMA Note");
   public static final AtsAttributeTypes ATS_STATE_NOTES = new AtsAttributeTypes("AAMFEdiWPm7M_xV1EswA", "State Notes");
   public static final AtsAttributeTypes ATS_RELEASED = new AtsAttributeTypes("AAMFEcnMoUZMLA2zB1AA", "Released");
   public static final AtsAttributeTypes ATS_DECISION = new AtsAttributeTypes("AAMFEd7uDXcmqq_FrCQA", "Decision");

   public static final AtsAttributeTypes ATS_DECISION_REVIEW_OPTIONS = new AtsAttributeTypes("AAMFEd5hRy1+SRJRqfwA",
      "Decision Review Options");
   public static final AtsAttributeTypes ATS_LEGACY_PCR_ID = new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA",
      "Legacy PCR Id");
   public static final AtsAttributeTypes ATS_TEAM_DEFINITION = new AtsAttributeTypes("AAMFEdd5bFEe18bd0lQA",
      "Team Definition");
   public static final AtsAttributeTypes ATS_CURRENT_STATE = new AtsAttributeTypes("AAMFEdOWL3u6hmX2VbwA",
      "Current State");
   public static final AtsAttributeTypes ATS_ALLOW_CREATE_BRANCH = new AtsAttributeTypes("AAMFEbARuQEvi6rtY5gA",
      "Allow Create Branch");
   public static final AtsAttributeTypes ATS_ALLOW_COMMIT_BRANCH = new AtsAttributeTypes("AAMFEbCZCkwgj73BsQgA",
      "Allow Commit Branch");
   public static final AtsAttributeTypes ATS_USER_COMMUNITY = new AtsAttributeTypes("AAMFEdAPtAq1IEwiCQAA",
      "User Community");
   public static final AtsAttributeTypes ATS_RELEASE_DATE = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA",
      "Release Date");
   public static final AtsAttributeTypes ATS_REVIEW_DEFECT = new AtsAttributeTypes("AAMFEd+MSVAb8JQ6f5gA",
      "Review Defect");
   public static final AtsAttributeTypes ATS_ESTIMATED_HOURS = new AtsAttributeTypes("AAMFEdCSqBh+cPyadiwA",
      "Estimated Hours");
   public static final AtsAttributeTypes ATS_WEEKLY_BENEFIT = new AtsAttributeTypes("AAMFEdEnEU9AecOHMOwA",
      "Weekly Benefit");
   public static final AtsAttributeTypes ATS_PERCENT_REWORK = new AtsAttributeTypes("AAMFEdKfjl2TII9+tuwA",
      "Percent Rework");
   public static final AtsAttributeTypes ATS_BLOCKING_REVIEW = new AtsAttributeTypes("AAMFEctKkjMRrIy1C7gA",
      "Blocking Review");
   public static final AtsAttributeTypes ATS_REVIEW_BLOCKS = new AtsAttributeTypes("AAMFEc6G2A8jmRWJgagA",
      "Review Blocks");
   public static final AtsAttributeTypes ATS_ACTIONABLE_ITEM = new AtsAttributeTypes("AAMFEdbcR2zpGzFOLOQA",
      "Actionable Item");
   public static final AtsAttributeTypes ATS_HOURS_PER_WORK_DAY = new AtsAttributeTypes("AAMFEdGlqFsZp22RMdAA",
      "Hours Per Work Day");
   public static final AtsAttributeTypes ATS_VALIDATION_REQUIRED = new AtsAttributeTypes("AAMFEcjT0TwkD2R4w1QA",
      "Validation Required");
   public static final AtsAttributeTypes ATS_PROPOSED_RESOLUTION = new AtsAttributeTypes("AAMFEdSSRDGgBQ5tctAA",
      "Proposed Resolution");
   public static final AtsAttributeTypes ATS_ESTIMATED_RELEASE_DATE = new AtsAttributeTypes("AAMFEcy6VB7Ble5SP1QA",
      "Estimated Release Date");
   public static final AtsAttributeTypes ATS_ESTIMATED_COMPLETION_DATE = new AtsAttributeTypes("AAMFEc18k3Gh+GP7zqAA",
      "Estimated Completion Date");
   public static final AtsAttributeTypes ATS_WORK_PACKAGE = new AtsAttributeTypes("AAMFEdpJqRp2wvA2qvAA",
      "Work Package");
   public static final AtsAttributeTypes ATS_GOAL_ORDER_VOTE = new AtsAttributeTypes("Aiecsz9pP1CRoQdaYRAA",
      "Goal Order Vote");
   public static final AtsAttributeTypes ATS_TEAM_USES_VERSIONS = new AtsAttributeTypes("AAMFEcrHnzPxQ7w3ligA",
      "Team Uses Versions");
   public static final AtsAttributeTypes ATS_VERSION_LOCKED = new AtsAttributeTypes("AAzRtEJXbjzR5jySOZgA",
      "Version Locked");
   public static final AtsAttributeTypes ATS_NEXT_VERSION = new AtsAttributeTypes("AAMFEcpH8Xb72hsF5AwA",
      "Next Version");
   public static final AtsAttributeTypes ATS_BASELINE_BRANCH_GUID = new AtsAttributeTypes("AAMFEdIjJ2za2fblEVgA",
      "Baseline Branch Guid");
   public static final AtsAttributeTypes ATS_RELATED_TO_STATE = new AtsAttributeTypes("AAMFEdkwHULOmHbMbGgA",
      "Related To State");

   public static final AtsAttributeTypes ATS_TITLE = new AtsAttributeTypes(CoreAttributeTypes.NAME);

   //   public static final AtsAttributeTypes ATS_CANCELLED_FROM_STATE = new AtsAttributeTypes("Cancelled From State");
   //   public static final AtsAttributeTypes ATS_REQUIRES_FOLLOWUP = new AtsAttributeTypes("Requires Followup" );
   //   public static final AtsAttributeTypes ATS_TASK_USES_RESOLUTION_OPTIONS = new AtsAttributeTypes("Task Uses Resolution Options");
   //   public static final AtsAttributeTypes ATS_CANCEL_REASON = new AtsAttributeTypes("Cancel Reason");
   //   public static final AtsAttributeTypes ATS_VALIDATE_REQ_CHANGES_WIDGET = new AtsAttributeTypes("Validate Requirement Changes");
   //   public static final AtsAttributeTypes ATS_CREATE_CODE_TEST_TASKS_OFF_REQUIREMENTS = new AtsAttributeTypes("Create Code/Test Tasks");
   //   public static final AtsAttributeTypes ATS_CHECK_SIGNALS_VIA_CDB_WIDGET = new AtsAttributeTypes("Check Signals Via CDB");
   //   public static final AtsAttributeTypes ATS_SHOW_CDB_DIFF_REPORT_WIDGET = new AtsAttributeTypes("Show CDB Differences Report");
   //   public static final AtsAttributeTypes ATS_COMMIT_MANAGER_WIDGET = new AtsAttributeTypes("Commit Manager");
   //   public static final AtsAttributeTypes ATS_WORKING_BRANCH_WIDGET = new AtsAttributeTypes("Working Branch");
   //   public static final AtsAttributeTypes ATS_ASSIGNEE = new AtsAttributeTypes("Assignees");

   static {
      ATS_ACTIVE.setDescription("Active ATS configuration object.");
      ATS_RESOLUTION.setDescription("Implementation details.");
      ATS_ROLE.setDescription("");
      ATS_LOG.setDescription("");
      ATS_STATE.setDescription("States of workflow state machine.");
      ATS_POINTS.setDescription("Abstract value that describes risk, complexity, and size of Actions.");
      ATS_NUMERIC_1.setDescription("Open field for user to be able to enter numbers for sorting.");
      ATS_NUMERIC_2.setDescription("Open field for user to be able to enter numbers for sorting.");
      ATS_CATEGORY_1.setDescription("Open field for user to be able to enter text to use for categorizing/sorting.");
      ATS_CATEGORY_2.setDescription("Open field for user to be able to enter text to use for categorizing/sorting.");
      ATS_CATEGORY_3.setDescription("Open field for user to be able to enter text to use for categorizing/sorting.");
      ATS_ACTIONABLE.setDescription("True if item can have Action written against or assigned to.");
      ATS_PROBLEM.setDescription("Problem found during analysis.");
      ATS_DESCRIPTION.setDescription("Detailed explanation.");
      ATS_FULL_NAME.setDescription("Expanded and descriptive name.");
      ATS_LOCATION.setDescription("Enter location of materials to review.");
      ATS_CHANGE_TYPE.setDescription("Type of change.");
      ATS_PRIORITY_TYPE.setDescription("1 = High; 5 = Low");
      ATS_NEED_BY.setDescription("Hard schedule date that workflow must be completed.");
      ATS_SMA_NOTE.setDescription("Notes applicable to ATS object");
      ATS_STATE_NOTES.setDescription("");
      ATS_RELEASED.setDescription("True if object is in a released state.");
      ATS_DECISION.setDescription("Option selected during decision review.");
      ATS_DECISION_REVIEW_OPTIONS.setDescription("Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
      ATS_LEGACY_PCR_ID.setDescription("Field to register problem change report id from legacy items imported into ATS.");
      ATS_TEAM_DEFINITION.setDescription("");
      ATS_CURRENT_STATE.setDescription("Current state of workflow state machine.");
      ATS_ALLOW_CREATE_BRANCH.setDescription("");
      ATS_ALLOW_COMMIT_BRANCH.setDescription("");
      ATS_USER_COMMUNITY.setDescription("If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
      ATS_RELEASE_DATE.setDescription("Date the changes were made available to the users.");
      ATS_REVIEW_DEFECT.setDescription("");
      ATS_ESTIMATED_HOURS.setDescription("Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
      ATS_WEEKLY_BENEFIT.setDescription("Estimated number of hours that will be saved over a single year if this change is completed.");
      ATS_PERCENT_REWORK.setDescription("");
      ATS_BLOCKING_REVIEW.setDescription("True if workflow should be blocked from contining until review is completed.");
      ATS_REVIEW_BLOCKS.setDescription("Review Completion will block it's parent workflow in this manner.");
      ATS_ACTIONABLE_ITEM.setDescription("Actionable Items that are impacted by this change.");
      ATS_HOURS_PER_WORK_DAY.setDescription("");
      ATS_VALIDATION_REQUIRED.setDescription("If selected, originator will be asked to validate the implementation.");
      ATS_PROPOSED_RESOLUTION.setDescription("Recommended resolution.");
      ATS_ESTIMATED_RELEASE_DATE.setDescription("Date the changes will be made available to the users.");
      ATS_ESTIMATED_COMPLETION_DATE.setDescription("Date the changes will be completed.");
      ATS_WORK_PACKAGE.setDescription("Designated accounting work package for completing workflow.");
      ATS_GOAL_ORDER_VOTE.setDescription("Vote for order item belongs to within goal.");
      ATS_TEAM_USES_VERSIONS.setDescription("True if Team Workflow uses versioning/releasing option.");
      ATS_VERSION_LOCKED.setDescription("True if version artifact is locked.");
      ATS_NEXT_VERSION.setDescription("True if version artifact is \"Next\" version to be released.");
      ATS_BASELINE_BRANCH_GUID.setDescription("Baseline branch associated with ATS object.");
      ATS_RELATED_TO_STATE.setDescription("State of parent workflow this object is related to.");
      ATS_TITLE.setDescription("Enter clear and consise title that can be generally understood.");
   }

   private String description;

   private AtsAttributeTypes(IAttributeType attributeType) {
      super(attributeType.getGuid(), attributeType.getName());
   }

   private AtsAttributeTypes(String guid, String name) {
      super(guid, "ats." + name);
   }

   private void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String getDescription() {
      return description;
   }
}