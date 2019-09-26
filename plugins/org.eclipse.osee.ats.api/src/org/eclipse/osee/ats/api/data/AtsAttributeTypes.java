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

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsAttributeTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();
   // @formatter:off
   public static final NamespaceToken ATS = NamespaceToken.valueOf(2, "ats", "Namespace for ats system and content management types");

   public static final AttributeTypeString ActionDetailsFormat = tokens.add(AttributeTypeToken.createString(1152921504606847199L, ATS, "ats.Action Details Format", MediaType.TEXT_PLAIN, "Format of string when push Action Details Copy button on SMA Workflow Editor."));
   public static final AttributeTypeBoolean Actionable = tokens.add(AttributeTypeToken.createBoolean(1152921504606847160L, ATS, "ats.Actionable", MediaType.TEXT_PLAIN, "True if item can have Action written against or assigned to."));
   public static final AttributeTypeArtifactId ActionableItemReference = tokens.add(AttributeTypeToken.createArtifactId(6780739363553225476L, ATS, "ats.Actionable Item Reference", MediaType.TEXT_PLAIN, "Actionable Items that are impacted by this change."));
   public static final AttributeTypeBoolean Active = tokens.add(AttributeTypeToken.createBoolean(1152921504606847153L, ATS, "ats.Active", MediaType.TEXT_PLAIN, "Active ATS configuration object."));
   public static final AttributeTypeString ActivityId = tokens.add(AttributeTypeToken.createString(1152921504606847874L, ATS, "ats.Activity Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ActivityName = tokens.add(AttributeTypeToken.createString(1152921504606847875L, ATS, "ats.Activity Name", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean AllowCommitBranch = tokens.add(AttributeTypeToken.createBoolean(1152921504606847162L, ATS, "ats.Allow Commit Branch", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean AllowCreateBranch = tokens.add(AttributeTypeToken.createBoolean(1152921504606847161L, ATS, "ats.Allow Create Branch", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean AllowUserActionCreation = tokens.add(AttributeTypeToken.createBoolean(1322118789779953012L, ATS, "ats.Allow User Action Creation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean AllowWebExport = tokens.add(AttributeTypeToken.createBoolean(1244831604424847172L, ATS, "ats.Allow Web Export", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean ApplicabilityWorkflow = tokens.add(AttributeTypeToken.createBoolean(1152922022510067882L, ATS, "ats.Applicability Workflow", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum ApplicableToProgram = tokens.add(AttributeTypeToken.createEnum(1152921949227188394L, ATS, "ats.Applicable to Program", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeLong ApproveRequestedHoursBy = tokens.add(AttributeTypeToken.createLong(224884848210198L, ATS, "ats.Approve Requested Hours By", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate ApproveRequestedHoursDate = tokens.add(AttributeTypeToken.createDate(83388338833828L, ATS, "ats.Approve Requested Hours Date", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeString AtsConfiguredBranch = tokens.add(AttributeTypeToken.createString(72063456936722683L, ATS, "ats.ATS Configured Branch", MediaType.TEXT_PLAIN, "ATS Configured Branch"));
   public static final AttributeTypeString AtsId = tokens.add(AttributeTypeToken.createString(1152921504606847877L, ATS, "ats.Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString AtsIdPrefix = tokens.add(AttributeTypeToken.createString(1162773128791720837L, ATS, "ats.ATS Id Prefix", MediaType.TEXT_PLAIN, "ATS Id Prefix"));
   public static final AttributeTypeString AtsIdSequenceName = tokens.add(AttributeTypeToken.createString(1163054603768431493L, ATS, "ats.ATS Id Sequence Name", MediaType.TEXT_PLAIN, "ATS Id Sequence Name"));
   public static final AttributeTypeString AtsQuickSearch = tokens.add(AttributeTypeToken.createString(72063457009467643L, ATS, "ATS Quick Search", MediaType.TEXT_PLAIN, "Saved ATS Quick Searches."));
   public static final AttributeTypeString AtsUserConfig = tokens.add(AttributeTypeToken.createString(2348752981434455L, ATS, "ATS User Config", MediaType.TEXT_PLAIN, "Saved ATS Configures"));
   public static final AttributeTypeString BaselineBranchId = tokens.add(AttributeTypeToken.createString(1152932018686787753L, ATS, "ats.Baseline Branch Id", MediaType.TEXT_PLAIN, "Baseline branch associated with ATS object."));
   public static final AttributeTypeString BlockedReason = tokens.add(AttributeTypeToken.createString(7797797474874870503L, ATS, "ats.Blocked Reason", MediaType.TEXT_PLAIN, "Reason for action being blocked"));
   public static final AttributeTypeString BranchMetrics = tokens.add(AttributeTypeToken.createString(1152921504606847190L, ATS, "ats.Branch Metrics", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CAM = tokens.add(AttributeTypeToken.createString(1152921596009727571L, ATS, "ats.CAM", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CSCI = tokens.add(AttributeTypeToken.createString(72063457007112443L, ATS, "ats.CSCI", MediaType.TEXT_PLAIN, "CSCI this Team is reponsible for."));
   public static final AttributeTypeEnum CancelReason = tokens.add(AttributeTypeToken.createEnum(5718762723487704057L, ATS, "ats.Cancel Reason", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CancelledBy = tokens.add(AttributeTypeToken.createString(1152921504606847170L, ATS, "ats.Cancelled By", MediaType.TEXT_PLAIN, "UserId of the user who cancelled workflow."));
   public static final AttributeTypeDate CancelledDate = tokens.add(AttributeTypeToken.createDate(1152921504606847169L, ATS, "ats.Cancelled Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was cancelled."));
   public static final AttributeTypeString CancelledFromState = tokens.add(AttributeTypeToken.createString(1152921504606847172L, ATS, "ats.Cancelled From State", MediaType.TEXT_PLAIN, "State workflow was in when cancelled."));
   public static final AttributeTypeString CancelledReason = tokens.add(AttributeTypeToken.createString(1152921504606847171L, ATS, "ats.Cancelled Reason", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled."));
   public static final AttributeTypeString CancelledReasonDetails = tokens.add(AttributeTypeToken.createString(8279626026752029322L, ATS, "ats.Cancelled Reason Details", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled."));
   public static final AttributeTypeString Category1 = tokens.add(AttributeTypeToken.createString(1152921504606847212L, ATS, "ats.Category1", MediaType.TEXT_PLAIN, "Open field for user to be able to enter text to use for categorizing/sorting."));
   public static final AttributeTypeString Category2 = tokens.add(AttributeTypeToken.createString(1152921504606847217L, ATS, "ats.Category2", MediaType.TEXT_PLAIN, Category1.getDescription()));
   public static final AttributeTypeString Category3 = tokens.add(AttributeTypeToken.createString(1152921504606847218L, ATS, "ats.Category3", MediaType.TEXT_PLAIN, Category1.getDescription()));
   public static final AttributeTypeEnum ChangeType = tokens.add(AttributeTypeToken.createEnum(1152921504606847180L, ATS, "ats.Change Type", MediaType.TEXT_PLAIN, "Type of change."));
   public static final AttributeTypeBoolean ClosureActive = tokens.add(AttributeTypeToken.createBoolean(1152921875139002555L, ATS, "ats.Closure Active", MediaType.TEXT_PLAIN, "Closure Active status of Program"));
   public static final AttributeTypeEnum ClosureState = tokens.add(AttributeTypeToken.createEnum(1152921504606847452L, ATS, "ats.closure.Closure State", MediaType.TEXT_PLAIN, "Closure Status of Build"));
   public static final AttributeTypeString CognosUniqueId = tokens.add(AttributeTypeToken.createString(72063457009467630L, ATS, "ats.Cognos Unique Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum ColorTeam = tokens.add(AttributeTypeToken.createEnum(1364016837443371647L, ATS, "ats.Color Team", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CommitOverride = tokens.add(AttributeTypeToken.createString(104739333325561L, ATS, "ats.Commit Override", MediaType.TEXT_PLAIN, "Commit was overridden by user."));
   public static final AttributeTypeString CompletedBy = tokens.add(AttributeTypeToken.createString(1152921504606847167L, ATS, "ats.Completed By", MediaType.TEXT_PLAIN, "UserId of the user who completed workflow."));
   public static final AttributeTypeDate CompletedDate = tokens.add(AttributeTypeToken.createDate(1152921504606847166L, ATS, "ats.Completed Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was completed."));
   public static final AttributeTypeString CompletedFromState = tokens.add(AttributeTypeToken.createString(1152921504606847168L, ATS, "ats.Completed From State", MediaType.TEXT_PLAIN, "State workflow was in when completed."));
   public static final AttributeTypeString ControlAccount = tokens.add(AttributeTypeToken.createString(3475568422796552185L, ATS, "ats.Control Account", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CreatedBy = tokens.add(AttributeTypeToken.createString(1152921504606847174L, ATS, "ats.Created By", MediaType.TEXT_PLAIN, "UserId of the user who created the workflow."));
   public static final AttributeTypeArtifactId CreatedByReference = tokens.add(AttributeTypeToken.createArtifactId(32875234523958L, ATS, "ats.Created By Reference", MediaType.TEXT_PLAIN, "Id of the user artifact who created the workflow."));
   public static final AttributeTypeDate CreatedDate = tokens.add(AttributeTypeToken.createDate(1152921504606847173L, ATS, "ats.Created Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was created."));
   public static final AttributeTypeString CurrentState = tokens.add(AttributeTypeToken.createString(1152921504606847192L, ATS, "ats.Current State", MediaType.TEXT_PLAIN, "Current state of workflow state machine."));
   public static final AttributeTypeString CurrentStateType = tokens.add(AttributeTypeToken.createString(1152921504606847147L, ATS, "ats.Current State Type", MediaType.TEXT_PLAIN, "Type of Current State: InWork, Completed or Cancelled."));
   public static final AttributeTypeString Decision = tokens.add(AttributeTypeToken.createString(1152921504606847221L, ATS, "ats.Decision", MediaType.TEXT_PLAIN, "Option selected during decision review."));
   public static final AttributeTypeString DecisionReviewOptions = tokens.add(AttributeTypeToken.createString(1152921504606847220L, ATS, "ats.Decision Review Options", MediaType.TEXT_PLAIN, "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>\")"));
   public static final AttributeTypeBoolean Default = tokens.add(AttributeTypeToken.createBoolean(1152921875139002538L, ATS, "ats.Default", MediaType.TEXT_PLAIN, "Default"));
   public static final AttributeTypeString Description = tokens.add(AttributeTypeToken.createString(1152921504606847196L, ATS, "ats.Description", MediaType.TEXT_PLAIN, "Detailed explanation."));
   public static final AttributeTypeString DslSheet = tokens.add(AttributeTypeToken.createString(1152921504606847197L, ATS, "ats.DSL Sheet", MediaType.TEXT_PLAIN, "")); //TODO: In production but not used in code anymore, need to have a talk about how to deal with these
   public static final AttributeTypeString DuplicatedPcrId = tokens.add(AttributeTypeToken.createString(1152922093378076842L, ATS, "ats.Duplicated PCR Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate EndDate = tokens.add(AttributeTypeToken.createDate(1152921504606847383L, ATS, "ats.End Date", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeString EstimateAssumptions = tokens.add(AttributeTypeToken.createString(7714952282787917834L, ATS, "ats.Estimate Assumptions", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate EstimatedCompletionDate = tokens.add(AttributeTypeToken.createDate(1152921504606847165L, ATS, "ats.Estimated Completion Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be completed."));
   public static final AttributeTypeDouble EstimatedHours = tokens.add(AttributeTypeToken.createDouble(1152921504606847182L, ATS, "ats.Estimated Hours", MediaType.TEXT_PLAIN, "Hours estimated to implement the changes associated with this Action.\\nIncludes estimated hours for workflows, tasks and reviews."));
   public static final AttributeTypeDate EstimatedReleaseDate = tokens.add(AttributeTypeToken.createDate(1152921504606847164L, ATS, "ats.Estimated Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be made available to the users."));
   public static final AttributeTypeString FullName = tokens.add(AttributeTypeToken.createString(1152921504606847198L, ATS, "ats.Full Name", MediaType.TEXT_PLAIN, "Expanded and descriptive name."));
   public static final AttributeTypeString GoalOrderVote = tokens.add(AttributeTypeToken.createString(1152921504606847211L, ATS, "ats.Goal Order Vote", MediaType.TEXT_PLAIN, "Vote for order item belongs to within goal."));
   public static final AttributeTypeDate Holiday = tokens.add(AttributeTypeToken.createDate(72064629481881851L, ATS, "ats.Holiday", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeDouble HoursPerWorkDay = tokens.add(AttributeTypeToken.createDouble(1152921504606847187L, ATS, "ats.Hours Per Work Day", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum IPT = tokens.add(AttributeTypeToken.createEnum(6025996821081174931L, ATS, "ats.IPT", MediaType.TEXT_PLAIN, "Integrated Product Team"));
   public static final AttributeTypeEnum IptTeam = tokens.add(AttributeTypeToken.createEnum(1364016887343371647L, ATS, "ats.IPT Team", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString KanbanIgnoreStates = tokens.add(AttributeTypeToken.createString(726700946264587643L, ATS, "ats.kb.Ignore States", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString KanbanStoryName = tokens.add(AttributeTypeToken.createString(72645877009467643L, ATS, "ats.kb.Story Name", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString LegacyPcrId = tokens.add(AttributeTypeToken.createString(1152921504606847219L, ATS, "ats.Legacy PCR Id", MediaType.TEXT_PLAIN, "Field to register problem change report id from legacy items imported into ATS."));
   public static final AttributeTypeInteger LocChanged = tokens.add(AttributeTypeToken.createInteger(1152921504606847207L, ATS, "ats.LOC Changed", MediaType.TEXT_PLAIN, "Total Lines of Code Changed"));
   public static final AttributeTypeInteger LocReviewed = tokens.add(AttributeTypeToken.createInteger(1152921504606847208L, ATS, "ats.LOC Reviewed", MediaType.TEXT_PLAIN, "Total Lines of Code Reviewed"));
   public static final AttributeTypeString Location = tokens.add(AttributeTypeToken.createString(1152921504606847223L, ATS, "ats.Location", MediaType.TEXT_PLAIN, "Enter location of materials to review."));
   public static final AttributeTypeString Log = tokens.add(AttributeTypeToken.createString(1152921504606847202L, ATS, "ats.Log", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString MeetingAttendee = tokens.add(AttributeTypeToken.createString(1152921504606847225L, ATS, "ats.Meeting Attendee", MediaType.TEXT_PLAIN, "Attendee of meeting."));
   public static final AttributeTypeDate MeetingDate = tokens.add(AttributeTypeToken.createDate(5605018543870805270L, ATS, "ats.Meeting Date", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeDouble MeetingLength = tokens.add(AttributeTypeToken.createDouble(1152921504606847188L, ATS, "ats.Meeting Length", MediaType.TEXT_PLAIN, "Length of meeting."));
   public static final AttributeTypeString MeetingLocation = tokens.add(AttributeTypeToken.createString(1152921504606847224L, ATS, "ats.Meeting Location", MediaType.TEXT_PLAIN, "Location meeting is held."));
   public static final AttributeTypeString Namespace = tokens.add(AttributeTypeToken.createString(4676151691645786526L, ATS, "ats.Namespace", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate NeedBy = tokens.add(AttributeTypeToken.createDate(1152921504606847163L, ATS, "ats.Need By", AttributeTypeToken.TEXT_CALENDAR, "Hard schedule date that workflow must be completed."));
   public static final AttributeTypeBoolean NextVersion = tokens.add(AttributeTypeToken.createBoolean(1152921504606847157L, ATS, "ats.Next Version", MediaType.TEXT_PLAIN, "True if version artifact is \"Next\" version to be released."));
   public static final AttributeTypeDouble Numeric1 = tokens.add(AttributeTypeToken.createDouble(1152921504606847184L, ATS, "ats.Numeric1", MediaType.TEXT_PLAIN, "Open field for user to be able to enter numbers for sorting."));
   public static final AttributeTypeDouble Numeric2 = tokens.add(AttributeTypeToken.createDouble(1152921504606847185L, ATS, "ats.Numeric2", MediaType.TEXT_PLAIN, Numeric1.getDescription()));
   public static final AttributeTypeString OperationalImpact = tokens.add(AttributeTypeToken.createString(1152921504606847213L, ATS, "ats.Operational Impact", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OperationalImpactDescription = tokens.add(AttributeTypeToken.createString(1152921504606847214L, ATS, "ats.Operational Impact Description", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OperationalImpactWorkaround = tokens.add(AttributeTypeToken.createString(1152921504606847215L, ATS, "ats.Operational Impact Workaround", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OperationalImpactWorkaroundDescription = tokens.add(AttributeTypeToken.createString(1152921504606847216L, ATS, "ats.Operational Impact Workaround Description", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OriginatingPcrId = tokens.add(AttributeTypeToken.createString(1152922093379125418L, ATS, "ats.Originating PCR Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger PagesChanged = tokens.add(AttributeTypeToken.createInteger(1152921504606847209L, ATS, "ats.Pages Changed", MediaType.TEXT_PLAIN, "Total Pages of Changed"));
   public static final AttributeTypeInteger PagesReviewed = tokens.add(AttributeTypeToken.createInteger(1152921504606847210L, ATS, "ats.Pages Reviewed", MediaType.TEXT_PLAIN, "Total Pages of Reviewed"));
   public static final AttributeTypeString PcrToolId = tokens.add(AttributeTypeToken.createString(1152922093370736810L, ATS, "ats.PCR Tool Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString PeerReviewId = tokens.add(AttributeTypeToken.createString(4231136442842667818L, ATS, "ats.Peer Review Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger PercentComplete = tokens.add(AttributeTypeToken.createInteger(1152921504606847183L, ATS, "ats.Percent Complete", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger PercentRework = tokens.add(AttributeTypeToken.createInteger(1152921504606847189L, ATS, "ats.Percent Rework", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger PlannedPoints = tokens.add(AttributeTypeToken.createInteger(232851836925913430L, ATS, "ats.Planned Points", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum Points = tokens.add(AttributeTypeToken.createEnum(1152921504606847178L, ATS, "ats.Points", MediaType.TEXT_PLAIN, "Abstract value that describes risk, complexity, and size of Actions."));
   public static final AttributeTypeString PointsAttributeType = tokens.add(AttributeTypeToken.createString(1152921573057888257L, ATS, "ats.Points Attribute Type", MediaType.TEXT_PLAIN, "Used to store the agile points type name (ats.Points or ats.Points Numeric)."));
   public static final AttributeTypeDouble PointsNumeric = tokens.add(AttributeTypeToken.createDouble(1728793301637070003L, ATS, "ats.Points Numeric", MediaType.TEXT_PLAIN, "Abstract value that describes risk, complexity, and size of Actions as float."));
   public static final AttributeTypeEnum Priority = tokens.add(AttributeTypeToken.createEnum(1152921504606847179L, ATS, "ats.Priority", MediaType.TEXT_PLAIN, "Priority\", \"1 = High; 5 = Low"));
   public static final AttributeTypeString Problem = tokens.add(AttributeTypeToken.createString(1152921504606847193L, ATS, "ats.Problem", MediaType.TEXT_PLAIN, "Problem found during analysis."));
   public static final AttributeTypeArtifactId ProgramId = tokens.add(AttributeTypeToken.createArtifactId(1152922093377028266L, ATS, "ats.Program Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ProposedResolution = tokens.add(AttributeTypeToken.createString(1152921504606847194L, ATS, "ats.Proposed Resolution", MediaType.TEXT_PLAIN, "Recommended resolution."));
   public static final AttributeTypeString Rationale = tokens.add(AttributeTypeToken.createString(1152922093379715242L, ATS, "ats.Rationale", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId RelatedPeerWorkflowDefinitionReference = tokens.add(AttributeTypeToken.createArtifactId(6245695017677665082L, ATS, "ats.Related Peer Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team"));
   public static final AttributeTypeString RelatedTaskWorkflowDefinitionOld = tokens.add(AttributeTypeToken.createString(1152921504606847151L, ATS, "ats.Related Task Workflow Definition Old", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId RelatedTaskWorkflowDefinitionReference = tokens.add(AttributeTypeToken.createArtifactId(2492475839748929444L, ATS, "ats.Related Task Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow"));
   public static final AttributeTypeString RelatedToState = tokens.add(AttributeTypeToken.createString(1152921504606847204L, ATS, "ats.Related To State", MediaType.TEXT_PLAIN, "State of parent workflow this object is related to."));
   public static final AttributeTypeDate ReleaseDate = tokens.add(AttributeTypeToken.createDate(1152921504606847175L, ATS, "ats.Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes were made available to the users."));
   public static final AttributeTypeBoolean Released = tokens.add(AttributeTypeToken.createBoolean(1152921504606847155L, ATS, "ats.Released", MediaType.TEXT_PLAIN, "True if object is in a released state."));
   public static final AttributeTypeBoolean RequireTargetedVersion = tokens.add(AttributeTypeToken.createBoolean(1152921504606847159L, ATS, "ats.Require Targeted Version", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Resolution = tokens.add(AttributeTypeToken.createString(1152921504606847195L, ATS, "ats.Resolution", MediaType.TEXT_PLAIN, "Implementation details."));
   public static final AttributeTypeEnum ReviewBlocks = tokens.add(AttributeTypeToken.createEnum(1152921504606847176L, ATS, "ats.Review Blocks", MediaType.TEXT_PLAIN, "Review Completion will block it's parent workflow in this manner."));
   public static final AttributeTypeString ReviewDefect = tokens.add(AttributeTypeToken.createString(1152921504606847222L, ATS, "ats.Review Defect", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum ReviewFormalType = tokens.add(AttributeTypeToken.createEnum(1152921504606847177L, ATS, "ats.Review Formal Type", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Role = tokens.add(AttributeTypeToken.createString(1152921504606847226L, ATS, "ats.Role", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString RuleDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847150L, ATS, "ats.Rule Definition", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate StartDate = tokens.add(AttributeTypeToken.createDate(1152921504606847382L, ATS, "ats.Start Date", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeString State = tokens.add(AttributeTypeToken.createString(1152921504606847191L, ATS, "ats.State", MediaType.TEXT_PLAIN, "States of workflow state machine."));
   public static final AttributeTypeString StateNotes = tokens.add(AttributeTypeToken.createString(1152921504606847203L, ATS, "ats.State Notes", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString SwEnhancement = tokens.add(AttributeTypeToken.createString(1152921504606847227L, ATS, "ats.SW Enhancement", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeLong TaskSetId = tokens.add(AttributeTypeToken.createLong(2412431655932432L, ATS, "ats.Task Set Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId TaskToChangedArtifactReference = tokens.add(AttributeTypeToken.createArtifactId(1153126013769613562L, ATS, "ats.Task To Changed Artifact Reference", MediaType.TEXT_PLAIN, "Task reference to the changed artifact"));
   public static final AttributeTypeArtifactId TeamDefinitionReference = tokens.add(AttributeTypeToken.createArtifactId(4730961339090285773L, ATS, "ats.Team Definition Reference", MediaType.TEXT_PLAIN, "Team Workflow to Team Definition"));
   public static final AttributeTypeBoolean TeamUsesVersions = tokens.add(AttributeTypeToken.createBoolean(1152921504606847158L, ATS, "ats.Team Uses Versions", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString TeamWorkflowArtifactType = tokens.add(AttributeTypeToken.createString(1152921504606847148L, ATS, "ats.Team Workflow Artifact Type", MediaType.TEXT_PLAIN, "Specific Artifact Type to use in creation of Team Workflow"));
   public static final AttributeTypeString TestRunToSourceLocator = tokens.add(AttributeTypeToken.createString(130595201919637916L, ATS, "ats.Test Run To Source Locator", MediaType.TEXT_PLAIN, "Enter clear and concise title that can be generally understood."));
   public static final AttributeTypeString Title = tokens.add(AttributeTypeToken.createString(CoreAttributeTypes.Name.getId(), ATS, CoreAttributeTypes.Name.getName(), MediaType.TEXT_PLAIN, "Enter clear and concise title that can be generally understood."));
   public static final AttributeTypeInteger UnplannedPoints = tokens.add(AttributeTypeToken.createInteger(284254492767020802L, ATS, "ats.Unplanned Points", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean UnplannedWork = tokens.add(AttributeTypeToken.createBoolean(2421093774890249189L, ATS, "ats.Unplanned Work", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean UsesResolutionOptions = tokens.add(AttributeTypeToken.createBoolean(1152921504606847154L, ATS, "ats.Uses Resolution Options", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean ValidationRequired = tokens.add(AttributeTypeToken.createBoolean(1152921504606847146L, ATS, "ats.Validation Required", MediaType.TEXT_PLAIN, "If selected, originator will be asked to validate the implementation."));
   public static final AttributeTypeBoolean VerificationCodeInspection = tokens.add(AttributeTypeToken.createBoolean(3454966334779726518L, ATS, "ats.Verification Code Inspection", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean VersionLocked = tokens.add(AttributeTypeToken.createBoolean(1152921504606847156L, ATS, "ats.Version Locked", MediaType.TEXT_PLAIN, "True if version artifact is locked."));
   public static final AttributeTypeDouble WeeklyBenefit = tokens.add(AttributeTypeToken.createDouble(1152921504606847186L, ATS, "ats.Weekly Benefit", MediaType.TEXT_PLAIN, "Estimated number of hours that will be saved over a single year if this change is completed."));
   public static final AttributeTypeString WorkPackage = tokens.add(AttributeTypeToken.createString(1152921504606847206L, ATS, "ats.Work Package", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow."));
   public static final AttributeTypeString WorkPackageGuid = tokens.add(AttributeTypeToken.createString(1152921504606847876L, ATS, "ats.Work Package Guid", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkPackageId = tokens.add(AttributeTypeToken.createString(1152921504606847872L, ATS, "ats.Work Package Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkPackageProgram = tokens.add(AttributeTypeToken.createString(1152921504606847873L, ATS, "ats.Work Package Program", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId WorkPackageReference = tokens.add(AttributeTypeToken.createArtifactId(473096133909456789L, ATS, "ats.Work Package Reference", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow."));
   public static final AttributeTypeEnum WorkPackageType = tokens.add(AttributeTypeToken.createEnum(72057594037928065L, ATS, "ats.Work Package Type", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkType = tokens.add(AttributeTypeToken.createString(72063456955810043L, ATS, "ats.Work Type", MediaType.TEXT_PLAIN, "Work Type of this Team."));
   public static final AttributeTypeArtifactId WorkflowDefinitionReference = tokens.add(AttributeTypeToken.createArtifactId(53049621055799825L, ATS, "ats.Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact"));
   public static final AttributeTypeString WorkflowNotes = tokens.add(AttributeTypeToken.createString(1152921504606847205L, ATS, "ats.Workflow Notes", MediaType.TEXT_PLAIN, "Notes applicable to ATS Workflow"));

   // Remove static after 25.0
   public static final AttributeTypeString ActionableItem = tokens.add(AttributeTypeToken.createString(1152921504606847200L, ATS, "ats.Actionable Item", MediaType.TEXT_PLAIN, "Actionable Items that are impacted by this change."));
   public static final AttributeTypeString TeamDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847201L, ATS, "ats.Team Definition", MediaType.TEXT_PLAIN, ""));

   // Remove static after 26.0
   public static final AttributeTypeString WorkflowDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847149L, ATS, "ats.Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact"));
   public static final AttributeTypeString RelatedPeerWorkflowDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847870L, ATS, "ats.Related Peer Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team"));
   public static final AttributeTypeString RelatedTaskWorkflowDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847152L, ATS, "ats.Related Task Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow"));

   // Leave this attribute definition for other OSEE sites to convert
   public static final AttributeTypeString BaselineBranchGuid = tokens.add(AttributeTypeToken.createString(1152921504606847145L, NamespaceToken.OSEE, "ats.Baseline Branch Guid", MediaType.TEXT_PLAIN, ""));

   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }
}