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
package org.eclipse.osee.ats.api.data;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsAttributeTypes {

   public static final Map<String, IAttributeType> nameToTypeMap = new HashMap<>();
   // @formatter:off
   public static final IAttributeType Actionable = createType(0x10000000000000B8L, "Actionable", "True if item can have Action written against or assigned to.");
   public static final IAttributeType ActionableItem = createType(0x10000000000000E0L, "Actionable Item", "Actionable Items that are impacted by this change.");
   public static final IAttributeType ActionDetailsFormat = createType(0x10000000000000DFL, "Action Details Format", "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   public static final IAttributeType Active = createType(0x10000000000000B1L, "Active", "Active ATS configuration object.");
   public static final IAttributeType AllowCommitBranch = createType(0x10000000000000BAL, "Allow Commit Branch");
   public static final IAttributeType AllowCreateBranch = createType(0x10000000000000B9L, "Allow Create Branch");
   public static final IAttributeType AllowUserActionCreation = createType(0x12591C08CFB41574L, "Allow User Action Creation");
   public static final IAttributeType ArtifactReference = createType(0x1000BA00000000F9L, "Artifact Reference");
   public static final IAttributeType AtsId = createType(0x1000000000000385L, "ATS Id", "ATS Generated Id");
   public static final IAttributeType AtsIdPrefix = createType(0x1023000000000385L, "ATS Id Prefix", "ATS Id Prefix");
   public static final IAttributeType AtsIdSequenceName = createType(0x1024000000000385L, "ATS Id Sequence Name", "ATS Id Sequence Name");
   public static final IAttributeType AtsConfiguredBranch = createType(0x1000555100000FBL, "ATS Configured Branch", "ATS Configured Branch");
   public static final IAttributeType TaskToChangedArtifactReference = createType(0x1000BA00000000FAL, "Task To Changed Artifact Reference");
   public static final IAttributeType BaselineBranchUuid = createType(0x10000990000000A9L, "Baseline Branch Uuid", "Baseline branch associated with ATS object.");

   public static final IAttributeType Category1 = createType(0x10000000000000ECL, "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final IAttributeType Category2 = createType(0x10000000000000F1L, "Category2", Category1.getDescription());
   public static final IAttributeType Category3 = createType(0x10000000000000F2L, "Category3", Category1.getDescription());

   public static final IAttributeType ChangeType = createType(0x10000000000000CCL, "Change Type", "Type of change.");

   public static final IAttributeType CancelledDate = createType(0x10000000000000C1L, "Cancelled Date", "Date the workflow was cancelled.");
   public static final IAttributeType CancelledBy = createType(0x10000000000000C2L, "Cancelled By", "UserId of the user who cancelled workflow.");
   public static final IAttributeType CancelledReason = createType(0x10000000000000C3L, "Cancelled Reason", "Explanation of why worklfow was cancelled.");
   public static final IAttributeType CancelledFromState = createType(0x10000000000000C4L, "Cancelled From State", "State workflow was in when cancelled.");
   public static final IAttributeType CSCI = createType(0x1000555143210FBL, "CSCI", "CSCI this Team is reponsible for.");

   public static final IAttributeType CreatedDate = createType(0x10000000000000C5L, "Created Date", "Date the workflow was created.");
   public static final IAttributeType CreatedBy = createType(0x10000000000000C6L, "Created By", "UserId of the user who created the workflow.");

   public static final IAttributeType CompletedDate = createType(0x10000000000000BEL, "Completed Date", "Date the workflow was completed.");
   public static final IAttributeType CompletedBy = createType(0x10000000000000BFL, "Completed By", "UserId of the user who completed workflow.");
   public static final IAttributeType CompletedFromState = createType(0x10000000000000C0L, "Completed From State", "State workflow was in when completed.");

   public static final IAttributeType CurrentState = createType(0x10000000000000D8L, "Current State", "Current state of workflow state machine.");
   public static final IAttributeType CurrentStateType = createType(0x10000000000000ABL, "Current State Type", "Type of Current State: InWork, Completed or Cancelled.");
   public static final IAttributeType Decision = createType(0x10000000000000F5L, "Decision", "Option selected during decision review.");

   public static final IAttributeType DecisionReviewOptions = createType(0x10000000000000F4L, "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final IAttributeType Default = createType(0x10000056457000AAL, "Default", "Default");
   public static final IAttributeType Description = createType(0x10000000000000DCL, "Description", "Detailed explanation.");
   public static final IAttributeType DslSheet = createType(0x10000000000000DDL, "DSL Sheet", "XText DSL Sheet for ATS");
   public static final IAttributeType EstimatedCompletionDate = createType(0x10000000000000BDL, "Estimated Completion Date", "Date the changes will be completed.");
   public static final IAttributeType EstimatedHours = createType(0x10000000000000CEL, "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final IAttributeType EstimatedReleaseDate = createType(0x10000000000000BCL, "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final IAttributeType FullName = createType(0x10000000000000DEL, "Full Name", "Expanded and descriptive name.");
   public static final IAttributeType GoalOrderVote = createType(0x10000000000000EBL, "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final IAttributeType HoursPerWorkDay = createType(0x10000000000000D3L, "Hours Per Work Day");
   public static final IAttributeType IPT = createType(0x53A0A42E822D3393L, "IPT", "Integrated Product Team");
   public static final IAttributeType LegacyPcrId = createType(0x10000000000000F3L, "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final IAttributeType Location = createType(0x10000000000000F7L, "Location", "Enter location of materials to review.");
   public static final IAttributeType LocChanged= createType(0x10000000000000E7L, "LOC Changed", "Total Lines of Code Changed");
   public static final IAttributeType LocReviewed = createType(0x10000000000000E8L, "LOC Reviewed", "Total Lines of Code Reviewed");
   public static final IAttributeType Log = createType(0x10000000000000E2L, "Log");

   public static final IAttributeType MeetingLocation = createType(0x10000000000000F8L, "Meeting Location", "Location meeting is held.");
   public static final IAttributeType MeetingAttendee = createType(0x10000000000000F9L, "Meeting Attendee", "Attendee of meeting.");
   public static final IAttributeType MeetingLength = createType(0x10000000000000D4L, "Meeting Length", "Length of meeting.");
   public static final IAttributeType MeetingDate = createType(0x4DC906AB42E58516L, "Meeting Date");
   public static final IAttributeType VerificationCodeInspection = createType(0x2FF28193877A9AB6L, "Verification Code Inspection");

   public static final IAttributeType NeedBy = createType(0x10000000000000BBL, "Need By", "Hard schedule date that workflow must be completed.");
   public static final IAttributeType NextVersion = createType(0x10000000000000B5L, "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final IAttributeType Numeric1 = createType(0x10000000000000D0L, "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final IAttributeType Numeric2 = createType(0x10000000000000D1L, "Numeric2", Numeric1.getDescription());
   public static final IAttributeType OperationalImpact = createType(0x10000000000000EDL, "Operational Impact");
   public static final IAttributeType OperationalImpactDescription = createType(0x10000000000000EEL, "Operational Impact Description");
   public static final IAttributeType OperationalImpactWorkaround = createType(0x10000000000000EFL, "Operational Impact Workaround");
   public static final IAttributeType OperationalImpactWorkaroundDescription = createType(0x10000000000000F0L, "Operational Impact Workaround Description");
   public static final IAttributeType PagesChanged= createType(0x10000000000000E9L, "Pages Changed", "Total Pages of Changed");
   public static final IAttributeType PagesReviewed = createType(0x10000000000000EAL, "Pages Reviewed", "Total Pages Reviewed");
   public static final IAttributeType PercentRework = createType(0x10000000000000D5L, "Percent Rework");
   public static final IAttributeType PercentComplete = createType(0x10000000000000CFL, "Percent Complete");

   public static final IAttributeType Points = createType(0x10000000000000CAL, "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final IAttributeType PointsAttributeType = createType(0x1000000FF0000001L, "Points Attribute Type", "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   public static final IAttributeType PointsNumeric = createType(0x17FDE8592A26FCB3L, "Points Numeric", "Abstract value that describes risk, complexity, and size of Actions as float.");
   public static final IAttributeType PriorityType = createType(0x10000000000000CBL, "Priority", "1 = High; 5 = Low");
   public static final IAttributeType Problem = createType(0x10000000000000D9L, "Problem", "Problem found during analysis.");
   public static final IAttributeType ProposedResolution = createType(0x10000000000000DAL, "Proposed Resolution", "Recommended resolution.");
   public static final IAttributeType QuickSearch = createType(0x1000555145600FBL, "ATS Quick Search", "Saved ATS Quick Searches.");
   public static final IAttributeType RelatedToState = createType(0x10000000000000E4L, "Related To State", "State of parent workflow this object is related to.");
   public static final IAttributeType Released = createType(0x10000000000000B3L, "Released", "True if object is in a released state.");
   public static final IAttributeType ReleaseDate = createType(0x10000000000000C7L, "Release Date", "Date the changes were made available to the users.");
   public static final IAttributeType Resolution = createType(0x10000000000000DBL, "Resolution", "Implementation details.");
   public static final IAttributeType ReviewBlocks = createType(0x10000000000000C8L, "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final IAttributeType ReviewDefect = createType(0x10000000000000F6L, "Review Defect");
   public static final IAttributeType ReviewFormalType = createType(0x10000000000000C9L, "Review Formal Type");
   public static final IAttributeType Role = createType(0x10000000000000FAL, "Role");
   public static final IAttributeType RuleDefinition = createType(0x10000000000000AEL, "Rule Definition");
   public static final IAttributeType SmaNote = createType(0x10000000000000E5L, "SMA Note", "Notes applicable to ATS object");
   public static final IAttributeType State = createType(0x10000000000000D7L, "State", "States of workflow state machine.");
   public static final IAttributeType StateNotes = createType(0x10000000000000E3L, "State Notes");
   public static final IAttributeType StartDate = createType(0x1000000000000196L, "Start Date");
   public static final IAttributeType EndDate = createType(0x1000000000000197L, "End Date");
   public static final IAttributeType SwEnhancement = createType(0x10000000000000FBL, "SW Enhancement");
   public static final IAttributeType TeamDefinition = createType(0x10000000000000E1L, "Team Definition");
   public static final IAttributeType TestToSourceLocator = TokenFactory.createAttributeType(0x01CFF7A4EBCA599CL,
      "Test Run to Source Locator");
   public static final IAttributeType Title = createType(CoreAttributeTypes.Name.getGuid(), CoreAttributeTypes.Name.getName(), "Enter clear and consise title that can be generally understood.");
   public static final IAttributeType ValidationRequired = createType(0x10000000000000AAL, "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final IAttributeType VersionLocked = createType(0x10000000000000B4L, "Version Locked", "True if version artifact is locked.");
   public static final IAttributeType WeeklyBenefit = createType(0x10000000000000D2L, "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final IAttributeType WorkflowDefinition = createType(0x10000000000000ADL, "Workflow Definition", "Specific work flow definition id used by this Workflow artifact");
   public static final IAttributeType WorkType = createType(0x1000555112340FBL, "Work Type", "Work Type of this Team.");
   public static final IAttributeType TeamWorkflowArtifactType = createType(0x10000000000000ACL, "Team Workflow Artifact Type", "Specific Artifact Type to use in creation of Team Workflow");
   public static final IAttributeType RelatedTaskWorkDefinition = createType(0x10000000000000B0L, "Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final IAttributeType WorkPackage = createType(0x10000000000000E6L, "Work Package", "Designated accounting work package for completing workflow.");
   public static final IAttributeType RelatedPeerWorkflowDefinition = createType(0x100000000000037EL, "Related Peer Workflow Definition", "Specific work flow definition id used by Peer To Peer Reviews for this Team");

   public static final IAttributeType WorkPackageId = createType(0x1000000000000380L, "Work Package ID");
   public static final IAttributeType WorkPackageProgram = createType(0x1000000000000381L, "Work Package Program");
   public static final IAttributeType WorkPackageType = createType(0x100000000000081L, "Work Package Type");
   public static final IAttributeType ActivityId = createType(0x1000000000000382L, "Activity ID");
   public static final IAttributeType UnPlannedWork = createType(0x219973F5F43E2BE5L, "Unplanned Work");
   public static final IAttributeType ActivityName = createType(0x1000000000000383L, "Activity Name");
   public static final IAttributeType WorkPackageGuid = createType(0x1000000000000384L, "Work Package Guid", "Work Package for this Team Workflow, Review, Task or Goal");

   public static final IAttributeType ClosureActive = createType(0x10000056457000BBL, "Closure Active status of Program");
   public static final IAttributeType ClosureState = createType(0x10000000000001DCL, "Closure Status of Build");

   // Applicability Feature
   public static final IAttributeType ApplicabilityWorkflow = createType(0x10000078957000AAL, "Applicability Workflow");
   public static final IAttributeType ApplicableToProgram = createType(0x10000067857000AAL, "Applicable To Program");
   public static final IAttributeType DuplicatedPcrId = createType(0x10000089158000AAL, "Duplicated PCR Id");
   public static final IAttributeType OriginatingPcrId = createType(0x10000089159000AAL, "Originating PCR Id");
   public static final IAttributeType PcrToolId = createType(0x10000089151000AAL, "PCR Tool Id");
   public static final IAttributeType ProgramUuid = createType(0x10000089157000AAL, "Program Uuid");
   public static final IAttributeType Rationale = createType(0x10000089159900AAL, "Rationale");

   public static final IAttributeType ColorTeam = createType(0x12EDF6163776C27FL, "Color Team");

   // Program
   public static final IAttributeType Namespace = createType(0x40E507303063999EL, "Namespace");

   // @formatter:on

   private static IAttributeType createType(Long guid, String name) {
      IAttributeType type = TokenFactory.createAttributeType(guid, "ats." + name);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   private static IAttributeType createType(Long guid, String name, String description) {
      IAttributeType type = TokenFactory.createAttributeType(guid, "ats." + name, description);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   public static IAttributeType getTypeByName(String name) {
      return nameToTypeMap.get(name);
   }
}
