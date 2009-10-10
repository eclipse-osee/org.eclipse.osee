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

/**
 * @author Donald G. Dunne
 */
public class ATSAttributes {

   private final String displayName;
   private final String storeName;
   private final String description;
   private static Map<String, ATSAttributes> attrNameToAttr = new HashMap<String, ATSAttributes>();

   public static final ATSAttributes ALLOW_CREATE_BRANCH = new ATSAttributes("Allow Create Branch");
   public static final ATSAttributes ALLOW_COMMIT_BRANCH = new ATSAttributes("Allow Commit Branch");
   public static final ATSAttributes WORKING_BRANCH_WIDGET = new ATSAttributes("Working Branch");
   public static final ATSAttributes VALIDATE_REQ_CHANGES_WIDGET = new ATSAttributes("Validate Requirement Changes");
   public static final ATSAttributes CREATE_CODE_TEST_TASKS_OFF_REQUIREMENTS =
         new ATSAttributes("Create Code/Test Tasks");
   public static final ATSAttributes CHECK_SIGNALS_VIA_CDB_WIDGET = new ATSAttributes("Check Signals Via CDB");
   public static final ATSAttributes SHOW_CDB_DIFF_REPORT_WIDGET = new ATSAttributes("Show CDB Differences Report");
   public static final ATSAttributes ROLE_ATTRIBUTE = new ATSAttributes("Role");
   public static final ATSAttributes ACTIONABLE_ITEM_GUID_ATTRIBUTE =
         new ATSAttributes("Actionable Item", "Actionable Items that are impacted by this change.");
   public static final ATSAttributes TEAM_DEFINITION_GUID_ATTRIBUTE = new ATSAttributes("Team Definition");
   public static final ATSAttributes TITLE_ATTRIBUTE =
         new ATSAttributes("Title", "Name", "Enter clear and consise title that can be generally understood.");
   public static final ATSAttributes STATE_ATTRIBUTE = new ATSAttributes("State", "States of workflow state machine.");
   public static final ATSAttributes CURRENT_STATE_ATTRIBUTE =
         new ATSAttributes("Current State", "Current state of workflow state machine.");
   public static final ATSAttributes FULL_NAME_ATTRIBUTE =
         new ATSAttributes("Full Name", "Expanded and descriptive name.");
   public static final ATSAttributes DESCRIPTION_ATTRIBUTE = new ATSAttributes("Description", "Detailed explanation.");
   public static final ATSAttributes CHANGE_TYPE_ATTRIBUTE = new ATSAttributes("Change Type", "Type of change.");
   public static final ATSAttributes ASSIGNEE_ATTRIBUTE =
         new ATSAttributes("Assignees", "Users currently assigned to do work.");
   public static final ATSAttributes PRIORITY_TYPE_ATTRIBUTE = new ATSAttributes("Priority", "1 = High; 5 = Low");
   public static final ATSAttributes USER_COMMUNITY_ATTRIBUTE =
         new ATSAttributes(
               "User Community",
               "If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
   public static final ATSAttributes NEED_BY_ATTRIBUTE =
         new ATSAttributes("Need By", "ats.Need By", "Hard schedule date that workflow must be completed.");
   public static final ATSAttributes VALIDATION_REQUIRED_ATTRIBUTE =
         new ATSAttributes("Validation Required",
               "If selected, originator will be asked to validate the implementation.");
   public static final ATSAttributes ACTIVE_ATTRIBUTE = new ATSAttributes("Active", "Active ATS configuration object.");
   public static final ATSAttributes LOG_ATTRIBUTE = new ATSAttributes("Log");
   public static final ATSAttributes LOCATION_ATTRIBUTE =
         new ATSAttributes("Location", "Enter location of materials to review.");
   public static final ATSAttributes REVIEW_DEFECT_ATTRIBUTE = new ATSAttributes("Review Defect");
   public static final ATSAttributes STATE_NOTES_ATTRIBUTE = new ATSAttributes("State Notes");
   public static final ATSAttributes ESTIMATED_HOURS_ATTRIBUTE =
         new ATSAttributes(
               "Estimated Hours",
               "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final ATSAttributes WEEKLY_BENEFIT_ATTRIBUTE =
         new ATSAttributes("Weekly Benefit",
               "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final ATSAttributes PERCENT_REWORK_ATTRIBUTE = new ATSAttributes("Percent Rework");
   public static final ATSAttributes TASK_USES_RESOLUTION_OPTIONS_ATTRIBUTE =
         new ATSAttributes("Task Uses Resolution Options",
               "True if resolution field is driven by option selections versus simple text.");
   public static final ATSAttributes RESOLUTION_ATTRIBUTE = new ATSAttributes("Resolution", "Implementation details.");
   public static final ATSAttributes RELATED_TO_STATE_ATTRIBUTE =
         new ATSAttributes("Related To State", "State of parent workflow this object is related to.");
   public static final ATSAttributes CANCEL_REASON_ATTRIBUTE =
         new ATSAttributes("Cancel Reason", "Reason for cancellation of this ATS object.");
   public static final ATSAttributes SMA_NOTE_ATTRIBUTE =
         new ATSAttributes("Notes", "ats.SMA Note", "Notes applicable to ATS object");
   public static final ATSAttributes WORK_PACKAGE_ATTRIBUTE =
         new ATSAttributes("Work Package", "Designated accounting work package for completing workflow.");
   public static final ATSAttributes COMMIT_MANAGER_WIDGET =
         new ATSAttributes("Commit Manager", "Commit branches to parent and parallel branches.");
   public static final ATSAttributes CATEGORY_ATTRIBUTE =
         new ATSAttributes("Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CATEGORY2_ATTRIBUTE =
         new ATSAttributes("Category2", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CATEGORY3_ATTRIBUTE =
         new ATSAttributes("Category3", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final ATSAttributes CANCELLED_FROM_STATE_ATTRIBUTE =
         new ATSAttributes("Cancelled From State", "State prior to cancellation of workflow.");
   public static final ATSAttributes RELEASED_ATTRIBUTE =
         new ATSAttributes("Released", "True if object is in a released state.");
   public static final ATSAttributes NEXT_VERSION_ATTRIBUTE =
         new ATSAttributes("Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final ATSAttributes ACTIONABLE_ATTRIBUTE =
         new ATSAttributes("Actionable", "True if item can have Action written against or assigned to.");
   public static final ATSAttributes TEAM_USES_VERSIONS_ATTRIBUTE =
         new ATSAttributes("Team Uses Versions", "True if Team Workflow uses versioning/releasing option.");
   public static final ATSAttributes LEGACY_PCR_ID_ATTRIBUTE =
         new ATSAttributes("Legacy PCR Id",
               "Field to register problem change report id from legacy items imported into ATS.");
   public static final ATSAttributes BASELINE_BRANCH_GUID_ATTRIBUTE =
         new ATSAttributes("Baseline Branch Guid", "Basline branch associated with ATS object.");
   public static final ATSAttributes DECISION_REVIEW_OPTIONS_ATTRIBUTE =
         new ATSAttributes(
               "Decision Review Options",
               "Options available for selection in review.  " + "Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final ATSAttributes DECISION_ATTRIBUTE =
         new ATSAttributes("Decision", "Option selected during decision review.");
   public static final ATSAttributes PROBLEM_ATTRIBUTE = new ATSAttributes("Problem", "Problem found during analysis.");
   public static final ATSAttributes REQUIRES_FOLLOWUP_ATTRIBUTE =
         new ATSAttributes("Requires Followup", "True if review requires someone to folloup after decision.");
   public static final ATSAttributes PROPOSED_RESOLUTION_ATTRIBUTE =
         new ATSAttributes("Proposed Resolution", "Recommended resolution.");
   // TODO Remove this after 0.5.0 release
   public static final ATSAttributes BLOCKING_REVIEW_ATTRIBUTE =
         new ATSAttributes("Blocking Review",
               "True if workflow should be blocked from contining until review is completed.");
   public static final ATSAttributes REVIEW_BLOCKS_ATTRIBUTE =
         new ATSAttributes("Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final ATSAttributes ESTIMATED_RELEASE_DATE_ATTRIBUTE =
         new ATSAttributes("Estimated Release Date", "Date the changes will be made available to the users.");
   public static final ATSAttributes ESTIMATED_COMPLETION_DATE_ATTRIBUTE =
         new ATSAttributes("Estimated Completion Date", "Date the changes will be completed.");
   public static final ATSAttributes RELEASE_DATE_ATTRIBUTE =
         new ATSAttributes("Release Date", "Date the changes were made available to the users.");
   public static final ATSAttributes MAN_DAYS_NEEDED_ATTRIBUTE = new ATSAttributes("Man Days Needed");

   protected ATSAttributes(String displayName, String storeName, String description) {
      this.displayName = displayName;
      this.storeName = storeName;
      this.description = description;
      attrNameToAttr.put(getStoreName(), this);
   }

   /**
    * Creates attribute with displayName = "<name>" and storeName = "ats.<name>"
    * 
    * @param name
    */
   private ATSAttributes(String name) {
      this(name, "ats." + name, null);
   }

   /**
    * Creates attribute with displayName = "<displayName>" and storeName = "ats.<displayName>"
    * 
    * @param name
    */
   private ATSAttributes(String displayName, String description) {
      this(displayName, "ats." + displayName, description);
   }

   public static ATSAttributes getAtsAttributeByStoreName(String storeName) {
      return attrNameToAttr.get(storeName);
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

   public String getStoreName() {
      return storeName;
   }

   public String getDescription() {
      return description;
   }

}
