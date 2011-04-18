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
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsAttributeTypes {

   // @formatter:off
   public static final IAttributeType Actionable = createType("AAMFEcvDtBiaJ3TMatAA", "Actionable", "True if item can have Action written against or assigned to.");
   public static final IAttributeType ActionableItem = createType("AAMFEdbcR2zpGzFOLOQA", "Actionable Item", "Actionable Items that are impacted by this change.");
   public static final IAttributeType ActionDetailsFormat = createType("Aij_PfM7wCsEA2Z720wA", "Action Details Format", "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   public static final IAttributeType Active = createType("AAMFEclQOVmzkIvzyWwA", "Active", "Active ATS configuration object.");
   public static final IAttributeType AllowCommitBranch = createType("AAMFEbCZCkwgj73BsQgA", "Allow Commit Branch");
   public static final IAttributeType AllowCreateBranch = createType("AAMFEbARuQEvi6rtY5gA", "Allow Create Branch");
   public static final IAttributeType BaselineBranchGuid = createType("AAMFEdIjJ2za2fblEVgA", "Baseline Branch Guid", "Basline branch associated with ATS object.");
   public static final IAttributeType BlockingReview = createType("AAMFEctKkjMRrIy1C7gA", "Blocking Review");
   
   public static final IAttributeType Category1 = createType("AAMFEdrYniOQYrYUKKQA", "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final IAttributeType Category2 = createType("AAMFEdthBkolbJKLXuAA", "Category2", Category1.getDescription());
   public static final IAttributeType Category3 = createType("AAMFEd06oxr8LMzZxdgA", "Category3", Category1.getDescription());

   public static final IAttributeType ChangeType = createType("AAMFEc+MwGHnPCv7HlgA", "Change Type", "Type of change.");
   
   public static final IAttributeType CancelledDate = createType("AXnyKG1waCcPPHHGEFQA", "Cancelled Date", "Date the workflow was cancelled.");
   public static final IAttributeType CancelledBy = createType("AXpNsieBHnqaJJfduGgA", "Cancelled By", "UserId of the user who cancelled workflow.");
   public static final IAttributeType CancelledReason = createType("AXqJE0SmwRQzvzlqC9gA", "Cancelled Reason", "Explanation of why worklfow was cancelled.");
   public static final IAttributeType CancelledFromState = createType("AXrxlXOwGiAnlaUNX6AA", "Cancelled From State", "State workflow was in when cancelled.");
   
   public static final IAttributeType CreatedDate = createType("AXny90bBpmfNkLpNhqwA", "Created Date", "Date the workflow was created.");
   public static final IAttributeType CreatedBy = createType("AXpTVIExV1p0kp9IKKQA", "Created By", "UserId of the user who created the workflow.");

   public static final IAttributeType CompletedDate = createType("AXnxSfRg6UhirNzaZnQA", "Completed Date", "Date the workflow was completed.");
   public static final IAttributeType CompletedBy = createType("AXo6tqxrOStgd9P16XQA", "Completed By", "UserId of the user who completed workflow.");
   public static final IAttributeType CompletedFromState = createType("AXr9OO909xRiI3MFNOwA", "Completed From State", "State workflow was in when completed.");

   public static final IAttributeType CurrentState = createType("AAMFEdOWL3u6hmX2VbwA", "Current State", "Current state of workflow state machine.");
   public static final IAttributeType CurrentStateType = createType("ATOWheEyGUJmPmPuqyAA", "Current State Type", "Type of Current State: InWork, Completed or Cancelled.");
   public static final IAttributeType Decision = createType("AAMFEd7uDXcmqq_FrCQA", "Decision", "Option selected during decision review.");

   public static final IAttributeType DecisionReviewOptions = createType("AAMFEd5hRy1+SRJRqfwA", "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final IAttributeType Description = createType("AAMFEdWJ_ChxX6+YKbwA", "Description", "Detailed explanation.");
   public static final IAttributeType DslSheet = createType("AGrqojZDowPDaLh4kBAA", "DSL Sheet", "XText DSL Sheet for ATS");
   public static final IAttributeType EstimatedCompletionDate = createType("AAMFEc18k3Gh+GP7zqAA", "Estimated Completion Date", "Date the changes will be completed.");
   public static final IAttributeType EstimatedHours = createType("AAMFEdCSqBh+cPyadiwA", "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final IAttributeType EstimatedReleaseDate = createType("AAMFEcy6VB7Ble5SP1QA", "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final IAttributeType FullName = createType("AAMFEdZI9XLT34cTonAA", "Full Name", "Expanded and descriptive name.");
   public static final IAttributeType GoalOrderVote = createType("Aiecsz9pP1CRoQdaYRAA", "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final IAttributeType HoursPerWorkDay = createType("AAMFEdGlqFsZp22RMdAA", "Hours Per Work Day");
   public static final IAttributeType LegacyPcrId = createType("AAMFEd3TakphMtQX1zgA", "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final IAttributeType Location = createType("AAMFEeAW4QBlesdfacwA", "Location", "Enter location of materials to review.");
   public static final IAttributeType LocChanged= createType("AQR27biJiQlOKTEKCvwA", "LOC Changed", "Total Lines of Code Changed");
   public static final IAttributeType LocReviewed = createType("AQR5ckRsrh4PpayYGAgA", "LOC Reviewed", "Total Lines of Code Reviewed");
   public static final IAttributeType Log = createType("AAMFEdgB1DX3eJSZb0wA", "Log");
   
   public static final IAttributeType MeetingLocation = createType("APom8wytSX0G3mcb3qQA", "Meeting Location", "Location meeting is held.");
   public static final IAttributeType MeetingAttendee = createType("APrZQQaOlFcX1CxbO6QA", "Meeting Attendee", "Attendee of meeting.");
   public static final IAttributeType MeetingLength = createType("APoxOFjXzV49ZmO3CfwA", "Meeting Length", "Length of meeting.");

   public static final IAttributeType NeedBy = createType("AAMFEcxAGzHAKfDNAIwA", "Need By", "Hard schedule date that workflow must be completed.");
   public static final IAttributeType NextVersion = createType("AAMFEcpH8Xb72hsF5AwA", "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final IAttributeType Numeric1 = createType("AABY2xxQsDm811kCViwA", "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final IAttributeType Numeric2 = createType("AABiRtvZsAEkU4BS9qwA", "Numeric2", Numeric1.getDescription());
   public static final IAttributeType OperationalImpact = createType("ADTfjCBpFxlyV3o1wLwA", "Operational Impact");
   public static final IAttributeType OperationalImpactDescription = createType("ADTfjCDvUF5PtiKdQ3wA", "Operational Impact Description");
   public static final IAttributeType OperationalImpactWorkaround = createType("AbMqFfIwQHRbmzT_VTAA", "Operational Impact Workaround");
   public static final IAttributeType OperationalImpactWorkaroundDescription = createType("AbMo7PoIukFDhQFJxKwA", "Operational Impact Workaround Description");
   public static final IAttributeType PagesChanged= createType("AQR8yMuv4W84UwvSJAQA", "Pages Changed", "Total Pages of Changed");
   public static final IAttributeType PagesReviewed = createType("AQR9qM8TTyCMb7sf4cQA", "Pages Reviewed", "Total Pages Reviewed");
   public static final IAttributeType PercentRework = createType("AAMFEdKfjl2TII9+tuwA", "Percent Rework");
   public static final IAttributeType PercentComplete = createType("AALLbOZiBBDN39YsRSAA", "Percent Complete");
   
   public static final IAttributeType Points = createType("AY2EeqhzcDEGtXtREkAA", "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final IAttributeType PriorityType = createType("AAMFEc8JzH1U6XGD59QA", "Priority", "1 = High; 5 = Low");
   public static final IAttributeType Problem = createType("AAMFEdQUxRyevvTu+bwA", "Problem", "Problem found during analysis.");
   public static final IAttributeType ProposedResolution = createType("AAMFEdSSRDGgBQ5tctAA", "Proposed Resolution", "Recommended resolution.");
   public static final IAttributeType RelatedToState = createType("AAMFEdkwHULOmHbMbGgA", "Related To State", "State of parent workflow this object is related to.");
   public static final IAttributeType Released = createType("AAMFEcnMoUZMLA2zB1AA", "Released", "True if object is in a released state.");
   public static final IAttributeType ReleaseDate = createType("AAMFEc3+cGcMDOCdmdAA", "Release Date", "Date the changes were made available to the users.");
   public static final IAttributeType Resolution = createType("AAMFEdUMfV1KdbQNaKwA", "Resolution", "Implementation details.");
   public static final IAttributeType ReviewBlocks = createType("AAMFEc6G2A8jmRWJgagA", "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final IAttributeType ReviewDefect = createType("AAMFEd+MSVAb8JQ6f5gA", "Review Defect");
   public static final IAttributeType ReviewFormalType = createType("AOwrClAkonFC_UKqyJAA", "Review Formal Type");
   public static final IAttributeType Role = createType("AAMFEeCqMz0XCSBJ+IQA", "Role");
   public static final IAttributeType RuleDefinition = createType("AEqAJNnkyW4_d5_WhpgA", "Rule Definition");
   public static final IAttributeType SmaNote = createType("AAMFEdm7ywte8qayfbAA", "SMA Note", "Notes applicable to ATS object");
   public static final IAttributeType State = createType("AAMFEdMa3wzVvp60xLQA", "State", "States of workflow state machine.");
   public static final IAttributeType StateNotes = createType("AAMFEdiWPm7M_xV1EswA", "State Notes");
   public static final IAttributeType TeamDefinition = createType("AAMFEdd5bFEe18bd0lQA", "Team Definition");
   public static final IAttributeType TeamUsesVersions = createType("AAMFEcrHnzPxQ7w3ligA", "Team Uses Versions", "True if Team Workflow uses versioning/releasing option.");
   public static final IAttributeType Title = createType(CoreAttributeTypes.Name.getGuid(), CoreAttributeTypes.Name.getName(), "Enter clear and consise title that can be generally understood.");
   public static final IAttributeType UserCommunity = createType("AAMFEdAPtAq1IEwiCQAA", "User Community", "If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
   public static final IAttributeType ValidationRequired = createType("AAMFEcjT0TwkD2R4w1QA", "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final IAttributeType VersionLocked = createType("AAzRtEJXbjzR5jySOZgA", "Version Locked", "True if version artifact is locked.");
   public static final IAttributeType WeeklyBenefit = createType("AAMFEdEnEU9AecOHMOwA", "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final IAttributeType WorkflowDefinitionOld = createType("ADG50fkFrQIxmfZgk3gA", "Workflow Definition Old", "Used in 0.9.8 - Unused in 0.9.9 - Specific work flow definition id used by this Workflow artifact");
   public static final IAttributeType WorkflowDefinition = createType("AbksV06OrBP_ceKCeSQA", "Workflow Definition", "Specific work flow definition id used by this Workflow artifact");
   public static final IAttributeType RelatedTaskWorkflowDefinitionOld = createType("AdR02A0xcUq4arK58BAA", "Used in 0.9.8 - Unused in 0.9.9 - Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final IAttributeType RelatedTaskWorkflowDefinition = createType("AblApNMuhjVuyDRq6VgA", "Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final IAttributeType WorkPackage = createType("AAMFEdpJqRp2wvA2qvAA", "Work Package", "Designated accounting work package for completing workflow.");
   
   
   // @formatter:on

   private static IAttributeType createType(String guid, String name) {
      return TokenFactory.createAttributeType(guid, "ats." + name);
   }

   private static IAttributeType createType(String guid, String name, String description) {
      return TokenFactory.createAttributeType(guid, "ats." + name, description);
   }
}