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

import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.enums.token.ApplicableToProgramAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.CancelReasonAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ChangeTypeAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ClosureStateAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ColorTeamAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.IptAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.IptTeamAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.PointAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.PriorityAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ReviewBlocksAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ReviewFormalTypeAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.WorkPackageTypeAttributeType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface AtsAttributeTypes {

   // @formatter:off
   AttributeTypeString ActionDetailsFormat = ats.createString(1152921504606847199L, "ats.Action Details Format", MediaType.TEXT_PLAIN, "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   AttributeTypeBoolean Actionable = ats.createBoolean(1152921504606847160L, "ats.Actionable", MediaType.TEXT_PLAIN, "True if item can have Action written against or assigned to.");
   AttributeTypeArtifactId ActionableItemReference = ats.createArtifactId(6780739363553225476L, "ats.Actionable Item Reference", MediaType.TEXT_PLAIN, "Actionable Items that are impacted by this change.");
   AttributeTypeBoolean Active = ats.createBoolean(1152921504606847153L, "ats.Active", MediaType.TEXT_PLAIN, "Active ATS configuration object.");
   AttributeTypeString ActivityId = ats.createString(1152921504606847874L, "ats.Activity Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ActivityName = ats.createString(1152921504606847875L, "ats.Activity Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean AllowCommitBranch = ats.createBoolean(1152921504606847162L, "ats.Allow Commit Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean AllowCreateBranch = ats.createBoolean(1152921504606847161L, "ats.Allow Create Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean AllowUserActionCreation = ats.createBoolean(1322118789779953012L, "ats.Allow User Action Creation", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean AllowWebExport = ats.createBoolean(1244831604424847172L, "ats.Allow Web Export", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean ApplicabilityWorkflow = ats.createBoolean(1152922022510067882L, "ats.Applicability Workflow", MediaType.TEXT_PLAIN, "");
   ApplicableToProgramAttributeType ApplicableToProgram = ats.createEnum(ApplicableToProgramAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeLong ApproveRequestedHoursBy = ats.createLong(224884848210198L, "ats.Approve Requested Hours By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate ApproveRequestedHoursDate = ats.createDate(83388338833828L, "ats.Approve Requested Hours Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString AtsConfiguredBranch = ats.createString(72063456936722683L, "ats.ATS Configured Branch", MediaType.TEXT_PLAIN, "ATS Configured Branch");
   AttributeTypeString AtsId = ats.createString(1152921504606847877L, "ats.Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AtsIdPrefix = ats.createString(1162773128791720837L, "ats.ATS Id Prefix", MediaType.TEXT_PLAIN, "ATS Id Prefix");
   AttributeTypeString AtsIdSequenceName = ats.createString(1163054603768431493L, "ats.ATS Id Sequence Name", MediaType.TEXT_PLAIN, "ATS Id Sequence Name");
   AttributeTypeString AtsQuickSearch = ats.createString(72063457009467643L, "ATS Quick Search", MediaType.TEXT_PLAIN, "Saved ATS Quick Searches.");
   AttributeTypeString AtsUserConfig = ats.createString(2348752981434455L, "ATS User Config", MediaType.TEXT_PLAIN, "Saved ATS Configures");
   AttributeTypeString BaselineBranchId = ats.createString(1152932018686787753L, "ats.Baseline Branch Id", MediaType.TEXT_PLAIN, "Baseline branch associated with ATS object.");
   AttributeTypeString BlockedReason = ats.createString(7797797474874870503L, "ats.Blocked Reason", MediaType.TEXT_PLAIN, "Reason for action being blocked");
   AttributeTypeString BranchMetrics = ats.createString(1152921504606847190L, "ats.Branch Metrics", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CAM = ats.createString(1152921596009727571L, "ats.CAM", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CSCI = ats.createString(72063457007112443L, "ats.CSCI", MediaType.TEXT_PLAIN, "CSCI this Team is reponsible for.");
   CancelReasonAttributeType CancelReason = ats.createEnum(CancelReasonAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString CancelledBy = ats.createString(1152921504606847170L, "ats.Cancelled By", MediaType.TEXT_PLAIN, "UserId of the user who cancelled workflow.");
   AttributeTypeDate CancelledDate = ats.createDate(1152921504606847169L, "ats.Cancelled Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was cancelled.");
   AttributeTypeString CancelledFromState = ats.createString(1152921504606847172L, "ats.Cancelled From State", MediaType.TEXT_PLAIN, "State workflow was in when cancelled.");
   AttributeTypeString CancelledReason = ats.createString(1152921504606847171L, "ats.Cancelled Reason", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled.");
   AttributeTypeString CancelledReasonDetails = ats.createString(8279626026752029322L, "ats.Cancelled Reason Details", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled.");
   AttributeTypeString Category1 = ats.createString(1152921504606847212L, "ats.Category1", MediaType.TEXT_PLAIN, "Open field for user to be able to enter text to use for categorizing/sorting.");
   AttributeTypeString Category2 = ats.createString(1152921504606847217L, "ats.Category2", MediaType.TEXT_PLAIN, Category1.getDescription());
   AttributeTypeString Category3 = ats.createString(1152921504606847218L, "ats.Category3", MediaType.TEXT_PLAIN, Category1.getDescription());
   ChangeTypeAttributeType ChangeType = ats.createEnum(ChangeTypeAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeBoolean ClosureActive = ats.createBoolean(1152921875139002555L, "ats.Closure Active", MediaType.TEXT_PLAIN, "Closure Active status of Program");
   ClosureStateAttributeType ClosureState = ats.createEnum(ClosureStateAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString CognosUniqueId = ats.createString(72063457009467630L, "ats.Cognos Unique Id", MediaType.TEXT_PLAIN, "");
   ColorTeamAttributeType ColorTeam = ats.createEnum(ColorTeamAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString CommitOverride = ats.createString(104739333325561L, "ats.Commit Override", MediaType.TEXT_PLAIN, "Commit was overridden by user.");
   AttributeTypeString CompletedBy = ats.createString(1152921504606847167L, "ats.Completed By", MediaType.TEXT_PLAIN, "UserId of the user who completed workflow.");
   AttributeTypeDate CompletedDate = ats.createDate(1152921504606847166L, "ats.Completed Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was completed.");
   AttributeTypeString CompletedFromState = ats.createString(1152921504606847168L, "ats.Completed From State", MediaType.TEXT_PLAIN, "State workflow was in when completed.");
   AttributeTypeString ControlAccount = ats.createString(3475568422796552185L, "ats.Control Account", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CreatedBy = ats.createString(1152921504606847174L, "ats.Created By", MediaType.TEXT_PLAIN, "UserId of the user who created the workflow.");
   AttributeTypeArtifactId CreatedByReference = ats.createArtifactId(32875234523958L, "ats.Created By Reference", MediaType.TEXT_PLAIN, "Id of the user artifact who created the workflow.");
   AttributeTypeDate CreatedDate = ats.createDate(1152921504606847173L, "ats.Created Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was created.");
   AttributeTypeString CurrentState = ats.createString(1152921504606847192L, "ats.Current State", MediaType.TEXT_PLAIN, "Current state of workflow state machine.");
   AttributeTypeString CurrentStateType = ats.createString(1152921504606847147L, "ats.Current State Type", MediaType.TEXT_PLAIN, "Type of Current State: InWork, Completed or Cancelled.");
   AttributeTypeString Decision = ats.createString(1152921504606847221L, "ats.Decision", MediaType.TEXT_PLAIN, "Option selected during decision review.");
   AttributeTypeString DecisionReviewOptions = ats.createString(1152921504606847220L, "ats.Decision Review Options", MediaType.TEXT_PLAIN, "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>\")");
   AttributeTypeBoolean Default = ats.createBoolean(1152921875139002538L, "ats.Default", MediaType.TEXT_PLAIN, "Default");
   AttributeTypeString Description = ats.createString(1152921504606847196L, "ats.Description", MediaType.TEXT_PLAIN, "Detailed explanation.");
   AttributeTypeString DslSheet = ats.createString(1152921504606847197L, "ats.DSL Sheet", MediaType.TEXT_PLAIN, ""); //TODO: In production but not used in code anymore, need to have a talk about how to deal with these
   AttributeTypeString DuplicatedPcrId = ats.createString(1152922093378076842L, "ats.Duplicated PCR Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate EndDate = ats.createDate(1152921504606847383L, "ats.End Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString EstimateAssumptions = ats.createString(7714952282787917834L, "ats.Estimate Assumptions", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate EstimatedCompletionDate = ats.createDate(1152921504606847165L, "ats.Estimated Completion Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be completed.");
   AttributeTypeDouble EstimatedHours = ats.createDouble(1152921504606847182L, "ats.Estimated Hours", MediaType.TEXT_PLAIN, "Hours estimated to implement the changes associated with this Action.\\nIncludes estimated hours for workflows, tasks and reviews.");
   AttributeTypeDate EstimatedReleaseDate = ats.createDate(1152921504606847164L, "ats.Estimated Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be made available to the users.");
   AttributeTypeString FullName = ats.createString(1152921504606847198L, "ats.Full Name", MediaType.TEXT_PLAIN, "Expanded and descriptive name.");
   AttributeTypeString GoalOrderVote = ats.createString(1152921504606847211L, "ats.Goal Order Vote", MediaType.TEXT_PLAIN, "Vote for order item belongs to within goal.");
   AttributeTypeDate Holiday = ats.createDate(72064629481881851L, "ats.Holiday", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeDouble HoursPerWorkDay = ats.createDouble(1152921504606847187L, "ats.Hours Per Work Day", MediaType.TEXT_PLAIN, "");
   IptAttributeType IPT = ats.createEnum(IptAttributeType::new, MediaType.TEXT_PLAIN);
   IptTeamAttributeType IptTeam = ats.createEnum(IptTeamAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString KanbanIgnoreStates = ats.createString(726700946264587643L, "ats.kb.Ignore States", MediaType.TEXT_PLAIN, "");
   AttributeTypeString KanbanStoryName = ats.createString(72645877009467643L, "ats.kb.Story Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeString LegacyPcrId = ats.createString(1152921504606847219L, "ats.Legacy PCR Id", MediaType.TEXT_PLAIN, "Field to register problem change report id from legacy items imported into ATS.");
   AttributeTypeInteger LocChanged = ats.createInteger(1152921504606847207L, "ats.LOC Changed", MediaType.TEXT_PLAIN, "Total Lines of Code Changed");
   AttributeTypeInteger LocReviewed = ats.createInteger(1152921504606847208L, "ats.LOC Reviewed", MediaType.TEXT_PLAIN, "Total Lines of Code Reviewed");
   AttributeTypeString Location = ats.createString(1152921504606847223L, "ats.Location", MediaType.TEXT_PLAIN, "Enter location of materials to review.");
   AttributeTypeString Log = ats.createString(1152921504606847202L, "ats.Log", MediaType.TEXT_XML, "");
   AttributeTypeString MeetingAttendee = ats.createString(1152921504606847225L, "ats.Meeting Attendee", MediaType.TEXT_PLAIN, "Attendee of meeting.");
   AttributeTypeDate MeetingDate = ats.createDate(5605018543870805270L, "ats.Meeting Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeDouble MeetingLength = ats.createDouble(1152921504606847188L, "ats.Meeting Length", MediaType.TEXT_PLAIN, "Length of meeting.");
   AttributeTypeString MeetingLocation = ats.createString(1152921504606847224L, "ats.Meeting Location", MediaType.TEXT_PLAIN, "Location meeting is held.");
   AttributeTypeString Namespace = ats.createString(4676151691645786526L, "ats.Namespace", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate NeedBy = ats.createDate(1152921504606847163L, "ats.Need By", AttributeTypeToken.TEXT_CALENDAR, "Hard schedule date that workflow must be completed.");
   AttributeTypeBoolean NextVersion = ats.createBoolean(1152921504606847157L, "ats.Next Version", MediaType.TEXT_PLAIN, "True if version artifact is \"Next\" version to be released.");
   AttributeTypeDouble Numeric1 = ats.createDouble(1152921504606847184L, "ats.Numeric1", MediaType.TEXT_PLAIN, "Open field for user to be able to enter numbers for sorting.");
   AttributeTypeDouble Numeric2 = ats.createDouble(1152921504606847185L, "ats.Numeric2", MediaType.TEXT_PLAIN, Numeric1.getDescription());
   AttributeTypeString OperationalImpact = ats.createString(1152921504606847213L, "ats.Operational Impact", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OperationalImpactDescription = ats.createString(1152921504606847214L, "ats.Operational Impact Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OperationalImpactWorkaround = ats.createString(1152921504606847215L, "ats.Operational Impact Workaround", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OperationalImpactWorkaroundDescription = ats.createString(1152921504606847216L, "ats.Operational Impact Workaround Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OriginatingPcrId = ats.createString(1152922093379125418L, "ats.Originating PCR Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger PagesChanged = ats.createInteger(1152921504606847209L, "ats.Pages Changed", MediaType.TEXT_PLAIN, "Total Pages of Changed");
   AttributeTypeInteger PagesReviewed = ats.createInteger(1152921504606847210L, "ats.Pages Reviewed", MediaType.TEXT_PLAIN, "Total Pages of Reviewed");
   AttributeTypeString PcrToolId = ats.createString(1152922093370736810L, "ats.PCR Tool Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PeerReviewId = ats.createString(4231136442842667818L, "ats.Peer Review Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger PercentComplete = ats.createInteger(1152921504606847183L, "ats.Percent Complete", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger PercentRework = ats.createInteger(1152921504606847189L, "ats.Percent Rework", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger PlannedPoints = ats.createInteger(232851836925913430L, "ats.Planned Points", MediaType.TEXT_PLAIN, "");
   PointAttributeType Points = ats.createEnum(PointAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString PointsAttributeType = ats.createString(1152921573057888257L, "ats.Points Attribute Type", MediaType.TEXT_PLAIN, "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   AttributeTypeDouble PointsNumeric = ats.createDouble(1728793301637070003L, "ats.Points Numeric", MediaType.TEXT_PLAIN, "Abstract value that describes risk, complexity, and size of Actions as float.");
   PriorityAttributeType Priority = ats.createEnum(PriorityAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString Problem = ats.createString(1152921504606847193L, "ats.Problem", MediaType.TEXT_PLAIN, "Problem found during analysis.");
   AttributeTypeArtifactId ProgramId = ats.createArtifactId(1152922093377028266L, "ats.Program Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ProposedResolution = ats.createString(1152921504606847194L, "ats.Proposed Resolution", MediaType.TEXT_PLAIN, "Recommended resolution.");
   AttributeTypeString Rationale = ats.createString(1152922093379715242L, "ats.Rationale", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId RelatedPeerWorkflowDefinitionReference = ats.createArtifactId(6245695017677665082L, "ats.Related Peer Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team");
   AttributeTypeString RelatedTaskWorkflowDefinitionOld = ats.createString(1152921504606847151L, "ats.Related Task Workflow Definition Old", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId RelatedTaskWorkflowDefinitionReference = ats.createArtifactId(2492475839748929444L, "ats.Related Task Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow");
   AttributeTypeString RelatedToState = ats.createString(1152921504606847204L, "ats.Related To State", MediaType.TEXT_PLAIN, "State of parent workflow this object is related to.");
   AttributeTypeDate ReleaseDate = ats.createDate(1152921504606847175L, "ats.Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes were made available to the users.");
   AttributeTypeBoolean Released = ats.createBoolean(1152921504606847155L, "ats.Released", MediaType.TEXT_PLAIN, "True if object is in a released state.");
   AttributeTypeBoolean RequireTargetedVersion = ats.createBoolean(1152921504606847159L, "ats.Require Targeted Version", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Resolution = ats.createString(1152921504606847195L, "ats.Resolution", MediaType.TEXT_PLAIN, "Implementation details.");
   ReviewBlocksAttributeType ReviewBlocks = ats.createEnum(ReviewBlocksAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString ReviewDefect = ats.createString(1152921504606847222L, "ats.Review Defect", MediaType.TEXT_PLAIN, "");
   ReviewFormalTypeAttributeType ReviewFormalType = ats.createEnum(ReviewFormalTypeAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString Role = ats.createString(1152921504606847226L, "ats.Role", MediaType.TEXT_PLAIN, "");
   AttributeTypeString RuleDefinition = ats.createString(1152921504606847150L, "ats.Rule Definition", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate StartDate = ats.createDate(1152921504606847382L, "ats.Start Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString State = ats.createString(1152921504606847191L, "ats.State", MediaType.TEXT_PLAIN, "States of workflow state machine.");
   AttributeTypeString StateNotes = ats.createString(1152921504606847203L, "ats.State Notes", MediaType.TEXT_XML, "");
   AttributeTypeString SwEnhancement = ats.createString(1152921504606847227L, "ats.SW Enhancement", MediaType.TEXT_PLAIN, "");
   AttributeTypeLong TaskSetId = ats.createLong(2412431655932432L, "ats.Task Set Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId TaskToChangedArtifactReference = ats.createArtifactId(1153126013769613562L, "ats.Task To Changed Artifact Reference", MediaType.TEXT_PLAIN, "Task reference to the changed artifact");
   AttributeTypeArtifactId TeamDefinitionReference = ats.createArtifactId(4730961339090285773L, "ats.Team Definition Reference", MediaType.TEXT_PLAIN, "Team Workflow to Team Definition");
   AttributeTypeBoolean TeamUsesVersions = ats.createBoolean(1152921504606847158L, "ats.Team Uses Versions", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TeamWorkflowArtifactType = ats.createString(1152921504606847148L, "ats.Team Workflow Artifact Type", MediaType.TEXT_PLAIN, "Specific Artifact Type to use in creation of Team Workflow");
   AttributeTypeString TestRunToSourceLocator = ats.createString(130595201919637916L, "ats.Test Run To Source Locator", MediaType.TEXT_PLAIN, "Enter clear and concise title that can be generally understood.");
   AttributeTypeString Title = ats.createString(CoreAttributeTypes.Name.getId(), CoreAttributeTypes.Name.getName(), MediaType.TEXT_PLAIN, "Enter clear and concise title that can be generally understood.");
   AttributeTypeInteger UnplannedPoints = ats.createInteger(284254492767020802L, "ats.Unplanned Points", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean UnplannedWork = ats.createBoolean(2421093774890249189L, "ats.Unplanned Work", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean UsesResolutionOptions = ats.createBoolean(1152921504606847154L, "ats.Uses Resolution Options", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean ValidationRequired = ats.createBoolean(1152921504606847146L, "ats.Validation Required", MediaType.TEXT_PLAIN, "If selected, originator will be asked to validate the implementation.");
   AttributeTypeBoolean VerificationCodeInspection = ats.createBoolean(3454966334779726518L, "ats.Verification Code Inspection", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean VersionLocked = ats.createBoolean(1152921504606847156L, "ats.Version Locked", MediaType.TEXT_PLAIN, "True if version artifact is locked.");
   AttributeTypeDouble WeeklyBenefit = ats.createDouble(1152921504606847186L, "ats.Weekly Benefit", MediaType.TEXT_PLAIN, "Estimated number of hours that will be saved over a single year if this change is completed.");
   AttributeTypeString WorkPackage = ats.createString(1152921504606847206L, "ats.Work Package", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow.");
   AttributeTypeString WorkPackageGuid = ats.createString(1152921504606847876L, "ats.Work Package Guid", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPackageId = ats.createString(1152921504606847872L, "ats.Work Package Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPackageProgram = ats.createString(1152921504606847873L, "ats.Work Package Program", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId WorkPackageReference = ats.createArtifactId(473096133909456789L, "ats.Work Package Reference", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow.");
   WorkPackageTypeAttributeType WorkPackageType = ats.createEnum(WorkPackageTypeAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString WorkType = ats.createString(72063456955810043L, "ats.Work Type", MediaType.TEXT_PLAIN, "Work Type of this Team.");
   AttributeTypeArtifactId WorkflowDefinitionReference = ats.createArtifactId(53049621055799825L, "ats.Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact");
   AttributeTypeString WorkflowNotes = ats.createString(1152921504606847205L, "ats.Workflow Notes", MediaType.TEXT_PLAIN, "Notes applicable to ATS Workflow");

   // Remove static after 25.0
   AttributeTypeString ActionableItem = ats.createString(1152921504606847200L, "ats.Actionable Item", MediaType.TEXT_PLAIN, "Actionable Items that are impacted by this change.");
   AttributeTypeString TeamDefinition = ats.createString(1152921504606847201L, "ats.Team Definition", MediaType.TEXT_PLAIN, "");

   // Remove static after 26.0
   AttributeTypeString WorkflowDefinition = ats.createString(1152921504606847149L, "ats.Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact");
   AttributeTypeString RelatedPeerWorkflowDefinition = ats.createString(1152921504606847870L, "ats.Related Peer Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team");
   AttributeTypeString RelatedTaskWorkflowDefinition = ats.createString(1152921504606847152L, "ats.Related Task Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow");

   // Leave this attribute definition for other OSEE sites to convert
   AttributeTypeString BaselineBranchGuid = ats.createString(1152921504606847145L, "ats.Baseline Branch Guid", MediaType.TEXT_PLAIN, "");

   // @formatter:on

}