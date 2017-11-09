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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsAttributeTypes {

   public static final Map<String, AttributeTypeToken> nameToTypeMap = new HashMap<>();
   // @formatter:off
   public static final AttributeTypeToken Actionable = createType(1152921504606847160L, "Actionable", "True if item can have Action written against or assigned to.");
   public static final AttributeTypeToken ActionableItemReference = createType(6780739363553225476L, "Actionable Item Reference", "Actionable Items that are impacted by this change.");
   public static final AttributeTypeToken ActionDetailsFormat = createType(1152921504606847199L, "Action Details Format", "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   public static final AttributeTypeToken Active = createType(1152921504606847153L, "Active", "Active ATS configuration object.");
   public static final AttributeTypeToken AllowCommitBranch = createType(1152921504606847162L, "Allow Commit Branch");
   public static final AttributeTypeToken AllowCreateBranch = createType(1152921504606847161L, "Allow Create Branch");
   public static final AttributeTypeToken AllowUserActionCreation = createType(1322118789779953012L, "Allow User Action Creation");
   public static final AttributeTypeToken ArtifactReference = createType(1153126013769613561L, "Artifact Reference");
   public static final AttributeTypeToken AtsId = createType(1152921504606847877L, "ATS Id", "ATS Generated Id");
   public static final AttributeTypeToken AtsIdPrefix = createType(1162773128791720837L, "ATS Id Prefix", "ATS Id Prefix");
   public static final AttributeTypeToken AtsIdSequenceName = createType(1163054603768431493L, "ATS Id Sequence Name", "ATS Id Sequence Name");
   public static final AttributeTypeToken AtsConfiguredBranch = createType(72063456936722683L, "ATS Configured Branch", "ATS Configured Branch");
   public static final AttributeTypeToken TaskToChangedArtifactReference = createType(1153126013769613562L, "Task To Changed Artifact Reference");
   public static final AttributeTypeToken BaselineBranchId = createType(1152932018686787753L, "Baseline Branch Id", "Baseline branch associated with ATS object.");

   public static final AttributeTypeToken Category1 = createType(1152921504606847212L, "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final AttributeTypeToken Category2 = createType(1152921504606847217L, "Category2", Category1.getDescription());
   public static final AttributeTypeToken Category3 = createType(1152921504606847218L, "Category3", Category1.getDescription());

   public static final AttributeTypeToken ChangeType = createType(1152921504606847180L, "Change Type", "Type of change.");

   public static final AttributeTypeToken CancelledDate = createType(1152921504606847169L, "Cancelled Date", "Date the workflow was cancelled.");
   public static final AttributeTypeToken CancelledBy = createType(1152921504606847170L, "Cancelled By", "UserId of the user who cancelled workflow.");
   public static final AttributeTypeToken CancelledReason = createType(1152921504606847171L, "Cancelled Reason", "Explanation of why worklfow was cancelled.");
   public static final AttributeTypeToken CancelledFromState = createType(1152921504606847172L, "Cancelled From State", "State workflow was in when cancelled.");
   public static final AttributeTypeToken CSCI = createType(72063457007112443L, "CSCI", "CSCI this Team is reponsible for.");

   public static final AttributeTypeToken CreatedDate = createType(1152921504606847173L, "Created Date", "Date the workflow was created.");
   public static final AttributeTypeToken CreatedBy = createType(1152921504606847174L, "Created By", "UserId of the user who created the workflow.");

   public static final AttributeTypeToken CompletedDate = createType(1152921504606847166L, "Completed Date", "Date the workflow was completed.");
   public static final AttributeTypeToken CompletedBy = createType(1152921504606847167L, "Completed By", "UserId of the user who completed workflow.");
   public static final AttributeTypeToken CompletedFromState = createType(1152921504606847168L, "Completed From State", "State workflow was in when completed.");

   public static final AttributeTypeToken CurrentState = createType(1152921504606847192L, "Current State", "Current state of workflow state machine.");
   public static final AttributeTypeToken CurrentStateType = createType(1152921504606847147L, "Current State Type", "Type of Current State: InWork, Completed or Cancelled.");
   public static final AttributeTypeToken Decision = createType(1152921504606847221L, "Decision", "Option selected during decision review.");

   public static final AttributeTypeToken DecisionReviewOptions = createType(1152921504606847220L, "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final AttributeTypeToken Default = createType(1152921875139002538L, "Default", "Default");
   public static final AttributeTypeToken Description = createType(1152921504606847196L, "Description", "Detailed explanation.");
   public static final AttributeTypeToken DslSheet = createType(1152921504606847197L, "DSL Sheet", "XText DSL Sheet for ATS");
   public static final AttributeTypeToken EstimatedCompletionDate = createType(1152921504606847165L, "Estimated Completion Date", "Date the changes will be completed.");
   public static final AttributeTypeToken EstimatedHours = createType(1152921504606847182L, "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final AttributeTypeToken EstimatedReleaseDate = createType(1152921504606847164L, "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final AttributeTypeToken FullName = createType(1152921504606847198L, "Full Name", "Expanded and descriptive name.");
   public static final AttributeTypeToken GoalOrderVote = createType(1152921504606847211L, "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final AttributeTypeToken HoursPerWorkDay = createType(1152921504606847187L, "Hours Per Work Day");
   public static final AttributeTypeToken IPT = createType(6025996821081174931L, "IPT", "Integrated Product Team");
   public static final AttributeTypeToken LegacyPcrId = createType(1152921504606847219L, "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final AttributeTypeToken Location = createType(1152921504606847223L, "Location", "Enter location of materials to review.");
   public static final AttributeTypeToken LocChanged= createType(1152921504606847207L, "LOC Changed", "Total Lines of Code Changed");
   public static final AttributeTypeToken LocReviewed = createType(1152921504606847208L, "LOC Reviewed", "Total Lines of Code Reviewed");
   public static final AttributeTypeToken Log = createType(1152921504606847202L, "Log");

   public static final AttributeTypeToken MeetingLocation = createType(1152921504606847224L, "Meeting Location", "Location meeting is held.");
   public static final AttributeTypeToken MeetingAttendee = createType(1152921504606847225L, "Meeting Attendee", "Attendee of meeting.");
   public static final AttributeTypeToken MeetingLength = createType(1152921504606847188L, "Meeting Length", "Length of meeting.");
   public static final AttributeTypeToken MeetingDate = createType(5605018543870805270L, "Meeting Date");
   public static final AttributeTypeToken VerificationCodeInspection = createType(3454966334779726518L, "Verification Code Inspection");

   public static final AttributeTypeToken NeedBy = createType(1152921504606847163L, "Need By", "Hard schedule date that workflow must be completed.");
   public static final AttributeTypeToken NextVersion = createType(1152921504606847157L, "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final AttributeTypeToken Numeric1 = createType(1152921504606847184L, "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final AttributeTypeToken Numeric2 = createType(1152921504606847185L, "Numeric2", Numeric1.getDescription());
   public static final AttributeTypeToken OperationalImpact = createType(1152921504606847213L, "Operational Impact");
   public static final AttributeTypeToken OperationalImpactDescription = createType(1152921504606847214L, "Operational Impact Description");
   public static final AttributeTypeToken OperationalImpactWorkaround = createType(1152921504606847215L, "Operational Impact Workaround");
   public static final AttributeTypeToken OperationalImpactWorkaroundDescription = createType(1152921504606847216L, "Operational Impact Workaround Description");
   public static final AttributeTypeToken PagesChanged= createType(1152921504606847209L, "Pages Changed", "Total Pages of Changed");
   public static final AttributeTypeToken PagesReviewed = createType(1152921504606847210L, "Pages Reviewed", "Total Pages Reviewed");
   public static final AttributeTypeToken PercentRework = createType(1152921504606847189L, "Percent Rework");
   public static final AttributeTypeToken PercentComplete = createType(1152921504606847183L, "Percent Complete");

   public static final AttributeTypeToken Points = createType(1152921504606847178L, "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final AttributeTypeToken PointsAttributeType = createType(1152921573057888257L, "Points Attribute Type", "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   public static final AttributeTypeToken PointsNumeric = createType(1728793301637070003L, "Points Numeric", "Abstract value that describes risk, complexity, and size of Actions as float.");
   public static final AttributeTypeToken PriorityType = createType(1152921504606847179L, "Priority", "1 = High; 5 = Low");
   public static final AttributeTypeToken Problem = createType(1152921504606847193L, "Problem", "Problem found during analysis.");
   public static final AttributeTypeToken ProposedResolution = createType(1152921504606847194L, "Proposed Resolution", "Recommended resolution.");
   public static final AttributeTypeToken QuickSearch = createType(72063457009467643L, "ATS Quick Search", "Saved ATS Quick Searches.");
   public static final AttributeTypeToken RelatedToState = createType(1152921504606847204L, "Related To State", "State of parent workflow this object is related to.");
   public static final AttributeTypeToken Released = createType(1152921504606847155L, "Released", "True if object is in a released state.");
   public static final AttributeTypeToken ReleaseDate = createType(1152921504606847175L, "Release Date", "Date the changes were made available to the users.");
   public static final AttributeTypeToken Resolution = createType(1152921504606847195L, "Resolution", "Implementation details.");
   public static final AttributeTypeToken ReviewBlocks = createType(1152921504606847176L, "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final AttributeTypeToken ReviewDefect = createType(1152921504606847222L, "Review Defect");
   public static final AttributeTypeToken ReviewFormalType = createType(1152921504606847177L, "Review Formal Type");
   public static final AttributeTypeToken Role = createType(1152921504606847226L, "Role");
   public static final AttributeTypeToken RuleDefinition = createType(1152921504606847150L, "Rule Definition");
   public static final AttributeTypeToken SmaNote = createType(1152921504606847205L, "SMA Note", "Notes applicable to ATS object");
   public static final AttributeTypeToken State = createType(1152921504606847191L, "State", "States of workflow state machine.");
   public static final AttributeTypeToken StateNotes = createType(1152921504606847203L, "State Notes");
   public static final AttributeTypeToken StartDate = createType(1152921504606847382L, "Start Date");
   public static final AttributeTypeToken Holiday = createType(72064629481881851L, "Holiday");
   public static final AttributeTypeToken UnPlannedPoints = createType(284254492767020802L, "Un-Planned Points");
   public static final AttributeTypeToken PlannedPoints = createType(232851836925913430L, "Planned Points");
   public static final AttributeTypeToken EndDate = createType(1152921504606847383L, "End Date");
   public static final AttributeTypeToken SwEnhancement = createType(1152921504606847227L, "SW Enhancement");
   public static final AttributeTypeToken TeamDefinitionReference = createType(4730961339090285773L, "Team Definition Reference");
   public static final AttributeTypeToken TestToSourceLocator = AttributeTypeToken.valueOf(130595201919637916L,
      "Test Run to Source Locator");
   public static final AttributeTypeToken Title = createType(CoreAttributeTypes.Name.getId(), CoreAttributeTypes.Name.getName(), "Enter clear and concise title that can be generally understood.");
   public static final AttributeTypeToken ValidationRequired = createType(1152921504606847146L, "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final AttributeTypeToken VersionLocked = createType(1152921504606847156L, "Version Locked", "True if version artifact is locked.");
   public static final AttributeTypeToken WeeklyBenefit = createType(1152921504606847186L, "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final AttributeTypeToken WorkflowDefinition = createType(1152921504606847149L, "Workflow Definition", "Specific work flow definition id used by this Workflow artifact");
   public static final AttributeTypeToken WorkType = createType(72063456955810043L, "Work Type", "Work Type of this Team.");
   public static final AttributeTypeToken TeamWorkflowArtifactType = createType(1152921504606847148L, "Team Workflow Artifact Type", "Specific Artifact Type to use in creation of Team Workflow");
   public static final AttributeTypeToken RelatedTaskWorkDefinition = createType(1152921504606847152L, "Related Task Workflow Definition", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final AttributeTypeToken WorkPackage = createType(1152921504606847206L, "Work Package", "Designated accounting work package for completing workflow.");
   public static final AttributeTypeToken WorkPackageReference = createType(473096133909456789L, "Work Package Reference", "Designated accounting work package for completing workflow.");
   public static final AttributeTypeToken RelatedPeerWorkflowDefinition = createType(1152921504606847870L, "Related Peer Workflow Definition", "Specific work flow definition id used by Peer To Peer Reviews for this Team");

   public static final AttributeTypeToken WorkPackageId = createType(1152921504606847872L, "Work Package ID");
   public static final AttributeTypeToken WorkPackageProgram = createType(1152921504606847873L, "Work Package Program");
   public static final AttributeTypeToken WorkPackageType = createType(72057594037928065L, "Work Package Type");
   public static final AttributeTypeToken ActivityId = createType(1152921504606847874L, "Activity ID");
   public static final AttributeTypeToken UnPlannedWork = createType(2421093774890249189L, "Unplanned Work");
   public static final AttributeTypeToken ActivityName = createType(1152921504606847875L, "Activity Name");

   public static final AttributeTypeToken ClosureActive = createType(1152921875139002555L, "Closure Active status of Program");
   public static final AttributeTypeToken ClosureState = createType(1152921504606847452L, "Closure Status of Build");

   // Applicability Feature
   public static final AttributeTypeToken ApplicabilityWorkflow = createType(1152922022510067882L, "Applicability Workflow");
   public static final AttributeTypeToken ApplicableToProgram = createType(1152921949227188394L, "Applicable To Program");
   public static final AttributeTypeToken DuplicatedPcrId = createType(1152922093378076842L, "Duplicated PCR Id");
   public static final AttributeTypeToken OriginatingPcrId = createType(1152922093379125418L, "Originating PCR Id");
   public static final AttributeTypeToken PcrToolId = createType(1152922093370736810L, "PCR Tool Id");
   public static final AttributeTypeToken ProgramId = createType(1152922093377028266L, "Program Id");
   public static final AttributeTypeToken Rationale = createType(1152922093379715242L, "Rationale");

   public static final AttributeTypeToken ColorTeam = createType(1364016837443371647L, "Color Team");
   public static final AttributeTypeToken IptTeam = createType(1364016887343371647L, "IPT Team");

   // Agile
   public static final AttributeTypeToken KanbanStoryName = createType(72645877009467643L, "kb.Story Name");
   public static final AttributeTypeToken KanbanIgnoreStates = createType(726700946264587643L, "kb.Ignore State");

   // Program
   public static final AttributeTypeToken Namespace = createType(4676151691645786526L, "Namespace");

   // @formatter:on

   public static AttributeTypeToken createType(Long guid, String name) {
      AttributeTypeToken type = AttributeTypeToken.valueOf(guid, "ats." + name);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   public static AttributeTypeToken createType(Long guid, String name, String description) {
      AttributeTypeToken type = AttributeTypeToken.valueOf(guid, "ats." + name, description);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   public static AttributeTypeToken getTypeByName(String name) {
      return nameToTypeMap.get(name);
   }
}
