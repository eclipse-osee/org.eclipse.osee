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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;

/**
 * @author Donald G. Dunne
 */
public class ATSAttributes {
   private static final Map<String, ATSAttributes> WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP =
      new HashMap<String, ATSAttributes>();

   public static final ATSAttributes ALLOW_CREATE_BRANCH = new ATSAttributes(AtsAttributeTypes.ATS_ALLOW_CREATE_BRANCH,
      "");
   public static final ATSAttributes ALLOW_COMMIT_BRANCH = new ATSAttributes(AtsAttributeTypes.ATS_ALLOW_COMMIT_BRANCH,
      "");
   public static final ATSAttributes WORKING_BRANCH_WIDGET = new ATSAttributes("Working Branch");
   public static final ATSAttributes VALIDATE_REQ_CHANGES_WIDGET = new ATSAttributes("Validate Requirement Changes");
   public static final ATSAttributes CREATE_CODE_TEST_TASKS_OFF_REQUIREMENTS = new ATSAttributes(
      "Create Code/Test Tasks");
   public static final ATSAttributes CHECK_SIGNALS_VIA_CDB_WIDGET = new ATSAttributes("Check Signals Via CDB");
   public static final ATSAttributes SHOW_CDB_DIFF_REPORT_WIDGET = new ATSAttributes("Show CDB Differences Report");
   public static final ATSAttributes ROLE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_ROLE, "");
   public static final ATSAttributes ACTIONABLE_ITEM_GUID_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_ACTIONABLE_ITEM, "Actionable Items that are impacted by this change.");
   public static final ATSAttributes TEAM_DEFINITION_GUID_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_TEAM_DEFINITION, "");
   public static final ATSAttributes TITLE_ATTRIBUTE = new ATSAttributes("Title", AtsAttributeTypes.ATS_TITLE,
      "Enter clear and consise title that can be generally understood.");
   public static final ATSAttributes STATE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_STATE,
      "States of workflow state machine.");
   public static final ATSAttributes CURRENT_STATE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_CURRENT_STATE,
      "Current state of workflow state machine.");
   public static final ATSAttributes FULL_NAME_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_FULL_NAME,
      "Expanded and descriptive name.");
   public static final ATSAttributes DESCRIPTION_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_DESCRIPTION,
      "Detailed explanation.");
   public static final ATSAttributes CHANGE_TYPE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_CHANGE_TYPE,
      "Type of change.");
   public static final ATSAttributes ASSIGNEE_ATTRIBUTE = new ATSAttributes("Assignees",
      "Users currently assigned to do work.");
   public static final ATSAttributes PRIORITY_TYPE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_PRIORITY_TYPE,
      "1 = High; 5 = Low");
   public static final ATSAttributes USER_COMMUNITY_ATTRIBUTE =
      new ATSAttributes(
         AtsAttributeTypes.ATS_USER_COMMUNITY,
         "If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
   public static final ATSAttributes NEED_BY_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_NEED_BY,
      "Hard schedule date that workflow must be completed.");
   public static final ATSAttributes VALIDATION_REQUIRED_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_VALIDATION_REQUIRED,
      "If selected, originator will be asked to validate the implementation.");
   public static final ATSAttributes ACTIVE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_ACTIVE,
      "Active ATS configuration object.");
   public static final ATSAttributes LOG_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_LOG, "");
   public static final ATSAttributes LOCATION_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_LOCATION,
      "Enter location of materials to review.");
   public static final ATSAttributes REVIEW_DEFECT_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_REVIEW_DEFECT,
      "");
   public static final ATSAttributes STATE_NOTES_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_STATE_NOTES, "");
   public static final ATSAttributes ESTIMATED_HOURS_ATTRIBUTE =
      new ATSAttributes(
         AtsAttributeTypes.ATS_ESTIMATED_HOURS,
         "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final ATSAttributes WEEKLY_BENEFIT_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_WEEKLY_BENEFIT,
      "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final ATSAttributes PERCENT_REWORK_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_PERCENT_REWORK,
      "");
   public static final ATSAttributes TASK_USES_RESOLUTION_OPTIONS_ATTRIBUTE = new ATSAttributes(
      "Task Uses Resolution Options", "True if resolution field is driven by option selections versus simple text.");
   public static final ATSAttributes RESOLUTION_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_RESOLUTION,
      "Implementation details.");
   public static final ATSAttributes RELATED_TO_STATE_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_RELATED_TO_STATE, "State of parent workflow this object is related to.");
   public static final ATSAttributes CANCEL_REASON_ATTRIBUTE = new ATSAttributes("Cancel Reason",
      "Reason for cancellation of this ATS object.");
   public static final ATSAttributes SMA_NOTE_ATTRIBUTE = new ATSAttributes("Notes", AtsAttributeTypes.ATS_SMA_NOTE,
      "Notes applicable to ATS object");
   public static final ATSAttributes WORK_PACKAGE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_WORK_PACKAGE,
      "Designated accounting work package for completing workflow.");
   public static final ATSAttributes COMMIT_MANAGER_WIDGET = new ATSAttributes("Commit Manager",
      "Commit branches to parent and parallel branches.");
   public static final ATSAttributes POINTS_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_POINTS,
      "Abstract value that describes risk, complexity, and size of Actions.");
   public static final ATSAttributes NUMERIC1_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_NUMERIC_1,
      "Open field for user to be able to enter numbers for sorting.");
   public static final ATSAttributes NUMERIC2_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_NUMERIC_2,
      "Open field for user to be able to enter numbers for sorting.");
   public static final ATSAttributes GOAL_ORDER_VOTE_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_GOAL_ORDER_VOTE, "Vote for order item belongs to within goal.");
   public static final ATSAttributes CATEGORY_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_CATEGORY_1,
      "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CATEGORY2_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_CATEGORY_2,
      "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CATEGORY3_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_CATEGORY_3,
      "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CANCELLED_FROM_STATE_ATTRIBUTE = new ATSAttributes("Cancelled From State",
      "State prior to cancellation of workflow.");
   public static final ATSAttributes RELEASED_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_RELEASED,
      "True if object is in a released state.");
   public static final ATSAttributes VERSION_LOCKED_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_VERSION_LOCKED,
      "True if version artifact is locked.");
   public static final ATSAttributes NEXT_VERSION_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_NEXT_VERSION,
      "True if version artifact is \"Next\" version to be released.");
   public static final ATSAttributes ACTIONABLE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_ACTIONABLE,
      "True if item can have Action written against or assigned to.");
   public static final ATSAttributes TEAM_USES_VERSIONS_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_TEAM_USES_VERSIONS, "True if Team Workflow uses versioning/releasing option.");
   public static final ATSAttributes LEGACY_PCR_ID_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_LEGACY_PCR_ID,
      "Field to register problem change report id from legacy items imported into ATS.");
   public static final ATSAttributes BASELINE_BRANCH_GUID_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_BASELINE_BRANCH_GUID, "Basline branch associated with ATS object.");
   public static final ATSAttributes DECISION_REVIEW_OPTIONS_ATTRIBUTE =
      new ATSAttributes(
         AtsAttributeTypes.ATS_DECISION_REVIEW_OPTIONS,
         "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final ATSAttributes DECISION_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_DECISION,
      "Option selected during decision review.");
   public static final ATSAttributes PROBLEM_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_PROBLEM,
      "Problem found during analysis.");
   public static final ATSAttributes REQUIRES_FOLLOWUP_ATTRIBUTE = new ATSAttributes("Requires Followup",
      "True if review requires someone to folloup after decision.");
   public static final ATSAttributes PROPOSED_RESOLUTION_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_PROPOSED_RESOLUTION, "Recommended resolution.");
   // TODO Remove this after 0.5.0 release
   public static final ATSAttributes BLOCKING_REVIEW_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_BLOCKING_REVIEW,
      "True if workflow should be blocked from contining until review is completed.");
   public static final ATSAttributes REVIEW_BLOCKS_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_REVIEW_BLOCKS,
      "Review Completion will block it's parent workflow in this manner.");
   public static final ATSAttributes ESTIMATED_RELEASE_DATE_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_ESTIMATED_RELEASE_DATE, "Date the changes will be made available to the users.");
   public static final ATSAttributes ESTIMATED_COMPLETION_DATE_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_ESTIMATED_COMPLETION_DATE, "Date the changes will be completed.");
   public static final ATSAttributes RELEASE_DATE_ATTRIBUTE = new ATSAttributes(AtsAttributeTypes.ATS_RELEASE_DATE,
      "Date the changes were made available to the users.");
   public static final ATSAttributes HOURS_PER_WORK_DAY_ATTRIBUTE = new ATSAttributes(
      AtsAttributeTypes.ATS_HOURS_PER_WORK_DAY, "");

   private final String displayName;
   private IAttributeType attributeType;
   private final String description;
   private final String workItemId;

   private ATSAttributes(String displayName, AtsAttributeTypes attributeType, String description) {
      this(displayName, attributeType.getName(), attributeType, description);
   }

   private ATSAttributes(AtsAttributeTypes attributeType, String description) {
      this(simpleName(attributeType), attributeType, description);
   }

   private static String simpleName(IAttributeType attributeType) {
      return attributeType.getName().replace("ats.", "");
   }

   protected ATSAttributes(String displayName, String workItemId, String description) {
      this(displayName, workItemId, null, description);
   }

   private ATSAttributes(String displayName, String workItemId, IAttributeType attributeType, String description) {
      this.displayName = displayName;
      this.attributeType = attributeType;
      this.workItemId = workItemId;
      this.description = description;
      WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP.put(workItemId, this);
   }

   private ATSAttributes(String name) {
      this(name, "");
   }

   private ATSAttributes(String name, String description) {
      this(name, "ats." + name, null, description);
   }

   public static ATSAttributes getAtsAttributeByStoreName(String workItemId) {
      return WORK_ITEM_ID_TO_ATS_ATTRIBUTE_MAP.get(workItemId);
   }

   @Override
   public final boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   public String getDisplayName() {
      return displayName;
   }

   protected void setAttributeType(IAttributeType attributeType) {
      this.attributeType = attributeType;
   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public String getWorkItemId() {
      return getAttributeType() != null ? getAttributeType().getName() : workItemId;
   }

   public String getDescription() {
      return description;
   }

}
