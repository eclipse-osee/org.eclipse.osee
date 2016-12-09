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
   public static final IAttributeType Actionable = createType(1152921504606847160L, "Actionable", "True if item can have Action written against or assigned to.");
   public static final IAttributeType ActionableItem = createType(1152921504606847200L, "Actionable Item", "Actionable Items that are impacted by this change.");
   public static final IAttributeType ActionDetailsFormat = createType(1152921504606847199L, "Action Details Format", "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   public static final IAttributeType Active = createType(1152921504606847153L, "Active", "Active ATS configuration object.");
   public static final IAttributeType AllowCommitBranch = createType(1152921504606847162L, "Allow Commit Branch");
   public static final IAttributeType AllowCreateBranch = createType(1152921504606847161L, "Allow Create Branch");
   public static final IAttributeType AllowUserActionCreation = createType(1322118789779953012L, "Allow User Action Creation");
   public static final IAttributeType ArtifactReference = createType(1153126013769613561L, "Artifact Reference");
   public static final IAttributeType AtsId = createType(1152921504606847877L, "ATS Id", "ATS Generated Id");
   public static final IAttributeType AtsIdPrefix = createType(1162773128791720837L, "ATS Id Prefix", "ATS Id Prefix");
   public static final IAttributeType AtsIdSequenceName = createType(1163054603768431493L, "ATS Id Sequence Name", "ATS Id Sequence Name");
   public static final IAttributeType AtsConfiguredBranch = createType(72063456936722683L, "ATS Configured Branch", "ATS Configured Branch");
   public static final IAttributeType TaskToChangedArtifactReference = createType(1153126013769613562L, "Task To Changed Artifact Reference");
   public static final IAttributeType BaselineBranchUuid = createType(1152932018686787753L, "Baseline Branch Uuid", "Baseline branch associated with ATS object.");

   public static final IAttributeType Category1 = createType(1152921504606847212L, "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final IAttributeType Category2 = createType(1152921504606847217L, "Category2", Category1.getDescription());
   public static final IAttributeType Category3 = createType(1152921504606847218L, "Category3", Category1.getDescription());

   public static final IAttributeType ChangeType = createType(1152921504606847180L, "Change Type", "Type of change.");

   public static final IAttributeType CancelledDate = createType(1152921504606847169L, "Cancelled Date", "Date the workflow was cancelled.");
   public static final IAttributeType CancelledBy = createType(1152921504606847170L, "Cancelled By", "UserId of the user who cancelled workflow.");
   public static final IAttributeType CancelledReason = createType(1152921504606847171L, "Cancelled Reason", "Explanation of why worklfow was cancelled.");
   public static final IAttributeType CancelledFromState = createType(1152921504606847172L, "Cancelled From State", "State workflow was in when cancelled.");
   public static final IAttributeType CSCI = createType(72063457007112443L, "CSCI", "CSCI this Team is reponsible for.");

   public static final IAttributeType CreatedDate = createType(1152921504606847173L, "Created Date", "Date the workflow was created.");
   public static final IAttributeType CreatedBy = createType(1152921504606847174L, "Created By", "UserId of the user who created the workflow.");

   public static final IAttributeType CompletedDate = createType(1152921504606847166L, "Completed Date", "Date the workflow was completed.");
   public static final IAttributeType CompletedBy = createType(1152921504606847167L, "Completed By", "UserId of the user who completed workflow.");
   public static final IAttributeType CompletedFromState = createType(1152921504606847168L, "Completed From State", "State workflow was in when completed.");

   public static final IAttributeType CurrentState = createType(1152921504606847192L, "Current State", "Current state of workflow state machine.");
   public static final IAttributeType CurrentStateType = createType(1152921504606847147L, "Current State Type", "Type of Current State: InWork, Completed or Cancelled.");
   public static final IAttributeType Decision = createType(1152921504606847221L, "Decision", "Option selected during decision review.");

   public static final IAttributeType DecisionReviewOptions = createType(1152921504606847220L, "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final IAttributeType Default = createType(1152921875139002538L, "Default", "Default");
   public static final IAttributeType Description = createType(1152921504606847196L, "Description", "Detailed explanation.");
   public static final IAttributeType DslSheet = createType(1152921504606847197L, "DSL Sheet", "XText DSL Sheet for ATS");
   public static final IAttributeType EstimatedCompletionDate = createType(1152921504606847165L, "Estimated Completion Date", "Date the changes will be completed.");
   public static final IAttributeType EstimatedHours = createType(1152921504606847182L, "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final IAttributeType EstimatedReleaseDate = createType(1152921504606847164L, "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final IAttributeType FullName = createType(1152921504606847198L, "Full Name", "Expanded and descriptive name.");
   public static final IAttributeType GoalOrderVote = createType(1152921504606847211L, "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final IAttributeType HoursPerWorkDay = createType(1152921504606847187L, "Hours Per Work Day");
   public static final IAttributeType IPT = createType(6025996821081174931L, "IPT", "Integrated Product Team");
   public static final IAttributeType LegacyPcrId = createType(1152921504606847219L, "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final IAttributeType Location = createType(1152921504606847223L, "Location", "Enter location of materials to review.");
   public static final IAttributeType LocChanged= createType(1152921504606847207L, "LOC Changed", "Total Lines of Code Changed");
   public static final IAttributeType LocReviewed = createType(1152921504606847208L, "LOC Reviewed", "Total Lines of Code Reviewed");
   public static final IAttributeType Log = createType(1152921504606847202L, "Log");

   public static final IAttributeType MeetingLocation = createType(1152921504606847224L, "Meeting Location", "Location meeting is held.");
   public static final IAttributeType MeetingAttendee = createType(1152921504606847225L, "Meeting Attendee", "Attendee of meeting.");
   public static final IAttributeType MeetingLength = createType(1152921504606847188L, "Meeting Length", "Length of meeting.");
   public static final IAttributeType MeetingDate = createType(5605018543870805270L, "Meeting Date");
   public static final IAttributeType VerificationCodeInspection = createType(3454966334779726518L, "Verification Code Inspection");

   public static final IAttributeType NeedBy = createType(1152921504606847163L, "Need By", "Hard schedule date that workflow must be completed.");
   public static final IAttributeType NextVersion = createType(1152921504606847157L, "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final IAttributeType Numeric1 = createType(1152921504606847184L, "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final IAttributeType Numeric2 = createType(1152921504606847185L, "Numeric2", Numeric1.getDescription());
   public static final IAttributeType OperationalImpact = createType(1152921504606847213L, "Operational Impact");
   public static final IAttributeType OperationalImpactDescription = createType(1152921504606847214L, "Operational Impact Description");
   public static final IAttributeType OperationalImpactWorkaround = createType(1152921504606847215L, "Operational Impact Workaround");
   public static final IAttributeType OperationalImpactWorkaroundDescription = createType(1152921504606847216L, "Operational Impact Workaround Description");
   public static final IAttributeType PagesChanged= createType(1152921504606847209L, "Pages Changed", "Total Pages of Changed");
   public static final IAttributeType PagesReviewed = createType(1152921504606847210L, "Pages Reviewed", "Total Pages Reviewed");
   public static final IAttributeType PercentRework = createType(1152921504606847189L, "Percent Rework");
   public static final IAttributeType PercentComplete = createType(1152921504606847183L, "Percent Complete");

   public static final IAttributeType Points = createType(1152921504606847178L, "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final IAttributeType PointsAttributeType = createType(1152921573057888257L, "Points Attribute Type", "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   public static final IAttributeType PointsNumeric = createType(1728793301637070003L, "Points Numeric", "Abstract value that describes risk, complexity, and size of Actions as float.");
   public static final IAttributeType PriorityType = createType(1152921504606847179L, "Priority", "1 = High; 5 = Low");
   public static final IAttributeType Problem = createType(1152921504606847193L, "Problem", "Problem found during analysis.");
   public static final IAttributeType ProposedResolution = createType(1152921504606847194L, "Proposed Resolution", "Recommended resolution.");
   public static final IAttributeType QuickSearch = createType(72063457009467643L, "ATS Quick Search", "Saved ATS Quick Searches.");
   public static final IAttributeType RelatedToState = createType(1152921504606847204L, "Related To State", "State of parent workflow this object is related to.");
   public static final IAttributeType Released = createType(1152921504606847155L, "Released", "True if object is in a released state.");
   public static final IAttributeType ReleaseDate = createType(1152921504606847175L, "Release Date", "Date the changes were made available to the users.");
   public static final IAttributeType Resolution = createType(1152921504606847195L, "Resolution", "Implementation details.");
   public static final IAttributeType ReviewBlocks = createType(1152921504606847176L, "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final IAttributeType ReviewDefect = createType(1152921504606847222L, "Review Defect");
   public static final IAttributeType ReviewFormalType = createType(1152921504606847177L, "Review Formal Type");
   public static final IAttributeType Role = createType(1152921504606847226L, "Role");
   public static final IAttributeType RuleDefinition = createType(1152921504606847150L, "Rule Definition");
   public static final IAttributeType SmaNote = createType(1152921504606847205L, "SMA Note", "Notes applicable to ATS object");
   public static final IAttributeType State = createType(1152921504606847191L, "State", "States of workflow state machine.");
   public static final IAttributeType StateNotes = createType(1152921504606847203L, "State Notes");
   public static final IAttributeType StartDate = createType(1152921504606847382L, "Start Date");
   public static final IAttributeType Holiday = createType(72064629481881851L, "Holiday");
   public static final IAttributeType UnPlannedPoints = createType(284254492767020802L, "Un-Planned Points");
   public static final IAttributeType PlannedPoints = createType(232851836925913430L, "Planned Points");
   public static final IAttributeType EndDate = createType(1152921504606847383L, "End Date");
   public static final IAttributeType SwEnhancement = createType(1152921504606847227L, "SW Enhancement");
   public static final IAttributeType TeamDefinition = createType(1152921504606847201L, "Team Definition");
   public static final IAttributeType TestToSourceLocator = TokenFactory.createAttributeType(130595201919637916L,
      "Test Run to Source Locator");
   public static final IAttributeType Title = createType(CoreAttributeTypes.Name.getId(), CoreAttributeTypes.Name.getName(), "Enter clear and concise title that can be generally understood.");
   public static final IAttributeType ValidationRequired = createType(1152921504606847146L, "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final IAttributeType VersionLocked = createType(1152921504606847156L, "Version Locked", "True if version artifact is locked.");
   public static final IAttributeType WeeklyBenefit = createType(1152921504606847186L, "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final IAttributeType WorkflowDefinition = createType(1152921504606847149L, "Workflow Definition", "Specific work flow definition id used by this Workflow artifact");
   public static final IAttributeType WorkType = createType(72063456955810043L, "Work Type", "Work Type of this Team.");
   public static final IAttributeType TeamWorkflowArtifactType = createType(1152921504606847148L, "Team Workflow Artifact Type", "Specific Artifact Type to use in creation of Team Workflow");
   public static final IAttributeType RelatedTaskWorkDefinition = createType(1152921504606847152L, "Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final IAttributeType WorkPackage = createType(1152921504606847206L, "Work Package", "Designated accounting work package for completing workflow.");
   public static final IAttributeType RelatedPeerWorkflowDefinition = createType(1152921504606847870L, "Related Peer Workflow Definition", "Specific work flow definition id used by Peer To Peer Reviews for this Team");

   public static final IAttributeType WorkPackageId = createType(1152921504606847872L, "Work Package ID");
   public static final IAttributeType WorkPackageProgram = createType(1152921504606847873L, "Work Package Program");
   public static final IAttributeType WorkPackageType = createType(72057594037928065L, "Work Package Type");
   public static final IAttributeType ActivityId = createType(1152921504606847874L, "Activity ID");
   public static final IAttributeType UnPlannedWork = createType(2421093774890249189L, "Unplanned Work");
   public static final IAttributeType ActivityName = createType(1152921504606847875L, "Activity Name");
   public static final IAttributeType WorkPackageGuid = createType(1152921504606847876L, "Work Package Guid", "Work Package for this Team Workflow, Review, Task or Goal");

   public static final IAttributeType ClosureActive = createType(1152921875139002555L, "Closure Active status of Program");
   public static final IAttributeType ClosureState = createType(1152921504606847452L, "Closure Status of Build");

   // Applicability Feature
   public static final IAttributeType ApplicabilityWorkflow = createType(1152922022510067882L, "Applicability Workflow");
   public static final IAttributeType ApplicableToProgram = createType(1152921949227188394L, "Applicable To Program");
   public static final IAttributeType DuplicatedPcrId = createType(1152922093378076842L, "Duplicated PCR Id");
   public static final IAttributeType OriginatingPcrId = createType(1152922093379125418L, "Originating PCR Id");
   public static final IAttributeType PcrToolId = createType(1152922093370736810L, "PCR Tool Id");
   public static final IAttributeType ProgramUuid = createType(1152922093377028266L, "Program Uuid");
   public static final IAttributeType Rationale = createType(1152922093379715242L, "Rationale");

   public static final IAttributeType ColorTeam = createType(1364016837443371647L, "Color Team");

   // Program
   public static final IAttributeType Namespace = createType(4676151691645786526L, "Namespace");

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
