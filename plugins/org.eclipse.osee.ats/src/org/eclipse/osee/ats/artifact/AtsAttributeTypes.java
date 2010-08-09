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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 */
public class AtsAttributeTypes extends NamedIdentity implements IAttributeType {

   public static final AtsAttributeTypes ATS_ACTIVE = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "Active",
      "Active ATS configuration object.");
   public static final AtsAttributeTypes ATS_RESOLUTION = new AtsAttributeTypes("AAMFEdUMfV1KdbQNaKwA", "Resolution",
      "Implementation details.");
   public static final AtsAttributeTypes ATS_ROLE = new AtsAttributeTypes("AAMFEeCqMz0XCSBJ+IQA", "Role");
   public static final AtsAttributeTypes ATS_LOG = new AtsAttributeTypes("AAMFEdgB1DX3eJSZb0wA", "Log");
   public static final AtsAttributeTypes ATS_STATE = new AtsAttributeTypes("AAMFEdMa3wzVvp60xLQA", "State",
      "States of workflow state machine.");
   public static final AtsAttributeTypes ATS_POINTS = new AtsAttributeTypes("AY2EeqhzcDEGtXtREkAA", "Points",
      "Abstract value that describes risk, complexity, and size of Actions.");
   public static final AtsAttributeTypes ATS_NUMERIC_1 = new AtsAttributeTypes("AABY2xxQsDm811kCViwA", "Numeric1",
      "Open field for user to be able to enter numbers for sorting.");
   public static final AtsAttributeTypes ATS_NUMERIC_2 = new AtsAttributeTypes("AABiRtvZsAEkU4BS9qwA", "Numeric2",
      ATS_NUMERIC_1.getDescription());
   public static final AtsAttributeTypes ATS_CATEGORY_1 = new AtsAttributeTypes("AAMFEdrYniOQYrYUKKQA", "Category",
      "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final AtsAttributeTypes ATS_CATEGORY_2 = new AtsAttributeTypes("AAMFEdthBkolbJKLXuAA", "Category2",
      ATS_CATEGORY_1.getDescription());
   public static final AtsAttributeTypes ATS_CATEGORY_3 = new AtsAttributeTypes("AAMFEd06oxr8LMzZxdgA", "Category3",
      ATS_CATEGORY_1.getDescription());
   public static final AtsAttributeTypes ATS_ACTIONABLE = new AtsAttributeTypes("AAMFEcvDtBiaJ3TMatAA", "Actionable",
      "True if item can have Action written against or assigned to.");
   public static final AtsAttributeTypes ATS_PROBLEM = new AtsAttributeTypes("AAMFEdQUxRyevvTu+bwA", "Problem",
      "Problem found during analysis.");
   public static final AtsAttributeTypes ATS_DESCRIPTION = new AtsAttributeTypes("AAMFEdWJ_ChxX6+YKbwA", "Description",
      "Detailed explanation.");
   public static final AtsAttributeTypes ATS_FULL_NAME = new AtsAttributeTypes("AAMFEdZI9XLT34cTonAA", "Full Name",
      "Expanded and descriptive name.");
   public static final AtsAttributeTypes ATS_LOCATION = new AtsAttributeTypes("AAMFEeAW4QBlesdfacwA", "Location",
      "Enter location of materials to review.");
   public static final AtsAttributeTypes ATS_CHANGE_TYPE = new AtsAttributeTypes("AAMFEc+MwGHnPCv7HlgA", "Change Type",
      "Type of change.");
   public static final AtsAttributeTypes ATS_PRIORITY_TYPE = new AtsAttributeTypes("AAMFEc8JzH1U6XGD59QA", "Priority",
      "1 = High; 5 = Low");
   public static final AtsAttributeTypes ATS_NEED_BY = new AtsAttributeTypes("AAMFEcxAGzHAKfDNAIwA", "Need By",
      "Hard schedule date that workflow must be completed.");
   public static final AtsAttributeTypes ATS_SMA_NOTE = new AtsAttributeTypes("AAMFEdm7ywte8qayfbAA", "SMA Note",
      "Notes applicable to ATS object");
   public static final AtsAttributeTypes ATS_STATE_NOTES = new AtsAttributeTypes("AAMFEdiWPm7M_xV1EswA", "State Notes");
   public static final AtsAttributeTypes ATS_RELEASED = new AtsAttributeTypes("AAMFEcnMoUZMLA2zB1AA", "Released",
      "True if object is in a released state.");
   public static final AtsAttributeTypes ATS_DECISION = new AtsAttributeTypes("AAMFEd7uDXcmqq_FrCQA", "Decision",
      "Option selected during decision review.");
   public static final AtsAttributeTypes ATS_DECISION_REVIEW_OPTIONS =
      new AtsAttributeTypes(
         "AAMFEd5hRy1+SRJRqfwA",
         "Decision Review Options",
         "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final AtsAttributeTypes ATS_LEGACY_PCR_ID = new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA",
      "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final AtsAttributeTypes ATS_TEAM_DEFINITION = new AtsAttributeTypes("AAMFEdd5bFEe18bd0lQA",
      "Team Definition");
   public static final AtsAttributeTypes ATS_CURRENT_STATE = new AtsAttributeTypes("AAMFEdOWL3u6hmX2VbwA",
      "Current State", "Current state of workflow state machine.");
   public static final AtsAttributeTypes ATS_ALLOW_CREATE_BRANCH = new AtsAttributeTypes("AAMFEbARuQEvi6rtY5gA",
      "Allow Create Branch");
   public static final AtsAttributeTypes ATS_ALLOW_COMMIT_BRANCH = new AtsAttributeTypes("AAMFEbCZCkwgj73BsQgA",
      "Allow Commit Branch");
   public static final AtsAttributeTypes ATS_USER_COMMUNITY =
      new AtsAttributeTypes(
         "AAMFEdAPtAq1IEwiCQAA",
         "User Community",
         "If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
   public static final AtsAttributeTypes ATS_RELEASE_DATE = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA",
      "Release Date", "Date the changes were made available to the users.");
   public static final AtsAttributeTypes ATS_REVIEW_DEFECT = new AtsAttributeTypes("AAMFEd+MSVAb8JQ6f5gA",
      "Review Defect");
   public static final AtsAttributeTypes ATS_ESTIMATED_HOURS =
      new AtsAttributeTypes(
         "AAMFEdCSqBh+cPyadiwA",
         "Estimated Hours",
         "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final AtsAttributeTypes ATS_WEEKLY_BENEFIT = new AtsAttributeTypes("AAMFEdEnEU9AecOHMOwA",
      "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final AtsAttributeTypes ATS_PERCENT_REWORK = new AtsAttributeTypes("AAMFEdKfjl2TII9+tuwA",
      "Percent Rework");
   public static final AtsAttributeTypes ATS_BLOCKING_REVIEW = new AtsAttributeTypes("AAMFEctKkjMRrIy1C7gA",
      "Blocking Review");
   public static final AtsAttributeTypes ATS_REVIEW_BLOCKS = new AtsAttributeTypes("AAMFEc6G2A8jmRWJgagA",
      "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final AtsAttributeTypes ATS_ACTIONABLE_ITEM = new AtsAttributeTypes("AAMFEdbcR2zpGzFOLOQA",
      "Actionable Item", "Actionable Items that are impacted by this change.");
   public static final AtsAttributeTypes ATS_HOURS_PER_WORK_DAY = new AtsAttributeTypes("AAMFEdGlqFsZp22RMdAA",
      "Hours Per Work Day");
   public static final AtsAttributeTypes ATS_VALIDATION_REQUIRED = new AtsAttributeTypes("AAMFEcjT0TwkD2R4w1QA",
      "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final AtsAttributeTypes ATS_PROPOSED_RESOLUTION = new AtsAttributeTypes("AAMFEdSSRDGgBQ5tctAA",
      "Proposed Resolution", "Recommended resolution.");
   public static final AtsAttributeTypes ATS_ESTIMATED_RELEASE_DATE = new AtsAttributeTypes("AAMFEcy6VB7Ble5SP1QA",
      "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final AtsAttributeTypes ATS_ESTIMATED_COMPLETION_DATE = new AtsAttributeTypes("AAMFEc18k3Gh+GP7zqAA",
      "Estimated Completion Date", "Date the changes will be completed.");
   public static final AtsAttributeTypes ATS_WORK_PACKAGE = new AtsAttributeTypes("AAMFEdpJqRp2wvA2qvAA",
      "Work Package", "Designated accounting work package for completing workflow.");
   public static final AtsAttributeTypes ATS_GOAL_ORDER_VOTE = new AtsAttributeTypes("Aiecsz9pP1CRoQdaYRAA",
      "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final AtsAttributeTypes ATS_TEAM_USES_VERSIONS = new AtsAttributeTypes("AAMFEcrHnzPxQ7w3ligA",
      "Team Uses Versions", "True if Team Workflow uses versioning/releasing option.");
   public static final AtsAttributeTypes ATS_VERSION_LOCKED = new AtsAttributeTypes("AAzRtEJXbjzR5jySOZgA",
      "Version Locked", "True if version artifact is locked.");
   public static final AtsAttributeTypes ATS_NEXT_VERSION = new AtsAttributeTypes("AAMFEcpH8Xb72hsF5AwA",
      "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final AtsAttributeTypes ATS_BASELINE_BRANCH_GUID = new AtsAttributeTypes("AAMFEdIjJ2za2fblEVgA",
      "Baseline Branch Guid", "Basline branch associated with ATS object.");
   public static final AtsAttributeTypes ATS_RELATED_TO_STATE = new AtsAttributeTypes("AAMFEdkwHULOmHbMbGgA",
      "Related To State", "State of parent workflow this object is related to.");

   public static final AtsAttributeTypes ATS_TITLE = new AtsAttributeTypes(CoreAttributeTypes.Name.getGuid(),
      CoreAttributeTypes.Name.getName(), "Enter clear and consise title that can be generally understood.");

   private AtsAttributeTypes(String guid, String name) {
      super(guid, "ats." + name);
   }

   private AtsAttributeTypes(String guid, String name, String description) {
      super(guid, "ats." + name, description);
   }
}