/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.data;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.Config;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.Edit;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.Read;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.UserArtId;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.UserUserId;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.framework.core.data.AttributeTypeToken.TEXT_CALENDAR;
import static org.eclipse.osee.framework.core.data.DisplayHint.MultiLine;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ApplicableToProgramAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.BitStateEnumAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.CancelledReasonEnumAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ClosureStateAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.PointAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ReviewBlocksAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.ReviewFormalTypeAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.RiskAnalysisAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.RiskFactorAttributeType;
import org.eclipse.osee.ats.api.data.enums.token.WorkPackageTypeAttributeType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface AtsAttributeTypes {

   // @formatter:off

   // Sign-By attribute types
   AttributeTypeLong ApproveRequestedHoursBy = ats.createLong(224884848210198L, "ats.Approve Requested Hours By", TEXT_PLAIN, "", Read, UserArtId);
   AttributeTypeDate ApproveRequestedHoursByDate = ats.createDate(83388338833828L, "ats.Approve Requested Hours Date", TEXT_CALENDAR, "", Read);
   AttributeTypeLong ProductLineApprovedBy = ats.createLong(7838821957985211888L, "ats.Product Line Approved By", TEXT_PLAIN, "", Read, UserUserId);
   AttributeTypeDate ProductLineApprovedDate = ats.createDate(735226602374161400L, "ats.Product Line Approved Date", TEXT_CALENDAR, "", Read);
   AttributeTypeLong ReviewedBy = ats.createLong(4020478495150345644L, "ats.Reviewed By", TEXT_PLAIN, "", Read, UserArtId);
   AttributeTypeDate ReviewedByDate = ats.createDate(2436278456841462630L, "ats.Reviewed By Date", TEXT_CALENDAR, "", Read);
   AttributeTypeLong SignedOffBy = ats.createLong(8050674225588113897L, "ats.Signed Off By", TEXT_PLAIN, "", Read, UserArtId);
   AttributeTypeDate SignedOffByDate = ats.createDate(1939621262920722287L, "ats.Signed Off By Date", AttributeTypeToken.TEXT_CALENDAR, "", Read);


   AttributeTypeString ActionDetailsFormat = ats.createString(1152921504606847199L, "ats.Action Details Format", TEXT_PLAIN, "Format of string when push Action Details Copy button on SMA Workflow Editor.", Config);
   AttributeTypeBoolean Actionable = ats.createBoolean(1152921504606847160L, "ats.Actionable", TEXT_PLAIN, "True if item can have Action written against or assigned to.", Config);
   AttributeTypeArtifactId ActionableItemReference = ats.createArtifactId(6780739363553225476L, "ats.Actionable Item Reference", TEXT_PLAIN, "Actionable Items that are impacted by this change.", Read);
   AttributeTypeBoolean Active = ats.createBoolean(1152921504606847153L, "ats.Active", TEXT_PLAIN, "Active ATS configuration object.", Config);
   AttributeTypeString ActivityId = ats.createString(1152921504606847874L, "ats.Activity Id", TEXT_PLAIN, "", Edit);
   AttributeTypeString ActivityName = ats.createString(1152921504606847875L, "ats.Activity Name", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean AllowCommitBranch = ats.createBoolean(1152921504606847162L, "ats.Allow Commit Branch", TEXT_PLAIN, "", Config);
   AttributeTypeBoolean AllowCreateBranch = ats.createBoolean(1152921504606847161L, "ats.Allow Create Branch", TEXT_PLAIN, "", Config);
   AttributeTypeBoolean AllowUserActionCreation = ats.createBoolean(1322118789779953012L, "ats.Allow User Action Creation", TEXT_PLAIN, "", Config);
   AttributeTypeBoolean AllowWebExport = ats.createBoolean(1244831604424847172L, "ats.Allow Web Export", TEXT_PLAIN, "", Config);
   AttributeTypeBoolean ApplicabilityWorkflow = ats.createBoolean(1152922022510067882L, "ats.Applicability Workflow", TEXT_PLAIN, "", Read);
   ApplicableToProgramAttributeType ApplicableToProgram = ats.createEnum(new ApplicableToProgramAttributeType(), Read);
   AttributeTypeString Assumptions = ats.createString(593196463063939110L, "ats.Assumptions", TEXT_PLAIN, "", MultiLine, Edit);
   AttributeTypeString AtsId = ats.createString(1152921504606847877L, "ats.Id", TEXT_PLAIN, "", Read);
   AttributeTypeString AtsIdPrefix = ats.createString(1162773128791720837L, "ats.ATS Id Prefix", TEXT_PLAIN, "ATS Id Prefix", Config);
   AttributeTypeString AtsIdSequenceName = ats.createString(1163054603768431493L, "ats.ATS Id Sequence Name", TEXT_PLAIN, "ATS Id Sequence Name", Config);
   AttributeTypeString BaselineBranchId = ats.createString(1152932018686787753L, "ats.Baseline Branch Id", TEXT_PLAIN, "Baseline branch associated with ATS object.", Config);
   BitStateEnumAttributeType BitState = ats.createEnum(new BitStateEnumAttributeType(), Edit);
   AttributeTypeString BlockedReason = ats.createString(7797797474874870503L, "ats.Blocked Reason", TEXT_PLAIN, "Reason for action being blocked", Edit);
   AttributeTypeString BranchMetrics = ats.createString(1152921504606847190L, "ats.Branch Metrics", TEXT_PLAIN, "", Read);
   AttributeTypeArtifactId BitConfig = ats.createArtifactId(2382915711248L, "ats.BIT Config", TEXT_PLAIN, "", Read);
   AttributeTypeString CAM = ats.createString(1152921596009727571L, "ats.CAM", TEXT_PLAIN, "", Read);
   AttributeTypeString CSCI = ats.createString(72063457007112443L, "ats.CSCI", TEXT_PLAIN, "CSCI this Team is reponsible for.", Edit);
   AttributeTypeString CancelledBy = ats.createString(1152921504606847170L, "ats.Cancelled By", TEXT_PLAIN, "UserId of the user who cancelled workflow.", Read, UserUserId);
   AttributeTypeDate CancelledDate = ats.createDate(1152921504606847169L, "ats.Cancelled Date", TEXT_CALENDAR, "Date the workflow was cancelled.", Read);
   AttributeTypeString CancelledFromState = ats.createString(1152921504606847172L, "ats.Cancelled From State", TEXT_PLAIN, "State workflow was in when cancelled.", Read);
   AttributeTypeString CancelledReason = ats.createString(1152921504606847171L, "ats.Cancelled Reason", TEXT_PLAIN, "Explanation of why worklfow was cancelled.", Edit);
   CancelledReasonEnumAttributeType CancelledReasonEnum = ats.createEnum(new CancelledReasonEnumAttributeType(), Edit);
   AttributeTypeString CancelledReasonDetails = ats.createString(8279626026752029322L, "ats.Cancelled Reason Details", TEXT_PLAIN, "Explanation of why worklfow was cancelled.", Edit);
   AttributeTypeString Category1 = ats.createString(1152921504606847212L, "ats.Category 1", TEXT_PLAIN, "Open field for user to be able to enter text to use for categorizing/sorting.", Edit);
   AttributeTypeString Category2 = ats.createString(1152921504606847217L, "ats.Category 2", TEXT_PLAIN, Category1.getDescription(), Edit);
   AttributeTypeString Category3 = ats.createString(1152921504606847218L, "ats.Category 3", TEXT_PLAIN, Category1.getDescription(), Edit);
   AttributeTypeString ChangeType = ats.createString(1152921504606847180L, "ats.Change Type", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean ClosureActive = ats.createBoolean(1152921875139002555L, "ats.Closure Active", TEXT_PLAIN, "Closure Active status of Program", Config);
   ClosureStateAttributeType ClosureState = ats.createEnum(new ClosureStateAttributeType(), Config);
   AttributeTypeString CognosUniqueId = ats.createString(72063457009467630L, "ats.Cognos Unique Id", TEXT_PLAIN, "", Config);
   AttributeTypeString CommitOverride = ats.createString(104739333325561L, "ats.Commit Override", TEXT_PLAIN, "Commit was overridden by user.", Read);
   AttributeTypeString CompletedBy = ats.createString(1152921504606847167L, "ats.Completed By", TEXT_PLAIN, "UserId of the user who completed workflow.", Read, UserUserId);
   AttributeTypeDate CompletedDate = ats.createDate(1152921504606847166L, "ats.Completed Date", TEXT_CALENDAR, "Date the workflow was completed.", Read);
   AttributeTypeString CompletedFromState = ats.createString(1152921504606847168L, "ats.Completed From State", TEXT_PLAIN, "State workflow was in when completed.", Read);
   AttributeTypeString ControlAccount = ats.createString(3475568422796552185L, "ats.Control Account", TEXT_PLAIN, "", Edit);
   AttributeTypeString CreatedBy = ats.createString(1152921504606847174L, "ats.Created By", TEXT_PLAIN, "UserId of the user who created the workflow.", Read, UserUserId);
   AttributeTypeDate CreatedDate = ats.createDate(1152921504606847173L, "ats.Created Date", TEXT_CALENDAR, "Date the workflow was created.", Read);
   AttributeTypeString CurrentState = ats.createString(1152921504606847192L, "ats.Current State", TEXT_PLAIN, "Current state of workflow state machine.", Read);
   AttributeTypeString CurrentStateName = ats.createString(4689644240272725681L, "ats.Current State Name", TEXT_PLAIN, "Current state name of workflow state machine.", Read);
   AttributeTypeString CurrentStateAssignee = ats.createString(1902418199157448550L, "ats.Current State Assignee", TEXT_PLAIN, "Current state assignees as user art id.", Read);
   AttributeTypeString CurrentStateType = ats.createString(1152921504606847147L, "ats.Current State Type", TEXT_PLAIN, "Type of Current State: InWork, Completed or Cancelled.", Read);
   AttributeTypeBoolean CrashOrBlankDisplay = ats.createBoolean(1601107563L, "ats.Crash or Blank Display", TEXT_PLAIN, "Crash OR cockpit displays are blank", DisplayHint.YesNoBoolean, Edit);
   AttributeTypeString Decision = ats.createString(1152921504606847221L, "ats.Decision", TEXT_PLAIN, "Option selected during decision review.", Edit);
   AttributeTypeString DecisionReviewOptions = ats.createString(1152921504606847220L, "ats.Decision Review Options", TEXT_PLAIN, "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>\")", Read);
   AttributeTypeBoolean Default = ats.createBoolean(1152921875139002538L, "ats.Default", TEXT_PLAIN, "Default", Read);
   AttributeTypeString Description = ats.createString(1152921504606847196L, "ats.Description", TEXT_PLAIN, "Detailed explanation.", MultiLine, Edit);
   AttributeTypeString DslSheet = ats.createString(1152921504606847197L, "ats.DSL Sheet", TEXT_PLAIN, "", Config); //TODO: In production but not used in code anymore, need to have a talk about how to deal with these
   AttributeTypeString DuplicatedPcrId = ats.createString(1152922093378076842L, "ats.Duplicated PCR Id", TEXT_PLAIN, "", Read);
   AttributeTypeDate EndDate = ats.createDate(1152921504606847383L, "ats.End Date", TEXT_CALENDAR, "", Read);
   AttributeTypeString EstimateAssumptions = ats.createString(7714952282787917834L, "ats.Estimate Assumptions", TEXT_PLAIN, "", MultiLine, Edit);
   AttributeTypeDate EstimatedCompletionDate = ats.createDate(1152921504606847165L, "ats.Estimated Completion Date", TEXT_CALENDAR, "Date the changes will be completed.", Read);
   AttributeTypeDouble EstimatedHours = ats.createDouble(1152921504606847182L, "ats.Estimated Hours", TEXT_PLAIN, "Hours estimated to implement the changes associated with this Action.\\nIncludes estimated hours for workflows, tasks and reviews.", DisplayHint.SingleLine, Edit);
   AttributeTypeDate EstimatedReleaseDate = ats.createDate(1152921504606847164L, "ats.Estimated Release Date", TEXT_CALENDAR, "Date the changes will be made available to the users.", Edit);
   AttributeTypeString ExternalReference = ats.createString(52148954699L, "ats.External Reference", TEXT_PLAIN, "Associated External PCR Number", Edit);
   AttributeTypeString FullName = ats.createString(1152921504606847198L, "ats.Full Name", TEXT_PLAIN, "Expanded and descriptive name.", Edit);
   AttributeTypeString FunctionalArea = ats.createString(5540854390791380448L, "ats.Functional Area", TEXT_PLAIN, "", Read);
   AttributeTypeArtifactId FeatureImpactReference = ats.createArtifactId(1148992834242L, "ats.Feature Impacted", TEXT_PLAIN, "", Read);
   AttributeTypeString GoalOrderVote = ats.createString(1152921504606847211L, "ats.Goal Order Vote", TEXT_PLAIN, "Vote for order item belongs to within goal.", Edit);
   AttributeTypeString HoldReason = ats.createString(5465485151546987972L, "ats.Hold Reason", TEXT_PLAIN, "Reason for action being held", Edit);
   AttributeTypeDate Holiday = ats.createDate(72064629481881851L, "ats.Holiday", TEXT_CALENDAR, "", Edit);
   AttributeTypeDouble HoursPerWorkDay = ats.createDouble(1152921504606847187L, "ats.Hours Per Work Day", TEXT_PLAIN, "", Config);
   AttributeTypeDouble HoursSpent = ats.createDouble(2676491969719166786L, "ats.Hours Spent", TEXT_PLAIN, "",Edit);
   AttributeTypeString HowToReproduceProblem = ats.createString(836807199L, "ats.How to reproduce the problem", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean IsDcs = ats.createBoolean(3199233956221339044L, "ats.Is DCS", TEXT_PLAIN, "Is Direct Commercial Sale", Edit);
   AttributeTypeString ImpactToMissionOrCrew = ats.createString(1442232314L, "ats.Impact to Mission or Crew", TEXT_PLAIN, "", Edit);
   AttributeTypeString Journal = ats.createString(4323598592300832478L, "ats.Journal", TEXT_PLAIN, "", Read);
   AttributeTypeArtifactId JournalSubscriber = ats.createArtifactId(42051756273953L, "ats.Journal Subscriber",TEXT_PLAIN,  "Artifact Id of User Subscribed to Journal Notifications", Read);
   AttributeTypeString KanbanIgnoreStates = ats.createString(726700946264587643L, "ats.kb.Ignore States", TEXT_PLAIN, "", Config);
   AttributeTypeString KanbanStoryName = ats.createString(72645877009467643L, "ats.kb.Story Name", TEXT_PLAIN, "", Edit);
   AttributeTypeString LegacyPcrId = ats.createString(1152921504606847219L, "ats.Legacy PCR Id", TEXT_PLAIN, "Field to register problem change report id from legacy items imported into ATS.", Edit);
   AttributeTypeString LegacyBuildId = ats.createString(4636732132432803380L, "ats.Legacy Build Id", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger LocChanged = ats.createInteger(1152921504606847207L, "ats.LOC Changed", TEXT_PLAIN, "Total Lines of Code Changed", Edit);
   AttributeTypeInteger LocReviewed = ats.createInteger(1152921504606847208L, "ats.LOC Reviewed", TEXT_PLAIN, "Total Lines of Code Reviewed", Edit);
   AttributeTypeString Location = ats.createString(1152921504606847223L, "ats.Location", TEXT_PLAIN, "Enter location of materials to review.", MultiLine, Edit);
   AttributeTypeString Log = ats.createString(1152921504606847202L, "ats.Log", MediaType.TEXT_XML, "", Edit);
   AttributeTypeString MeetingAttendee = ats.createString(1152921504606847225L, "ats.Meeting Attendee", TEXT_PLAIN, "Attendee of meeting.", Read);
   AttributeTypeDate MeetingDate = ats.createDate(5605018543870805270L, "ats.Meeting Date", TEXT_CALENDAR, "", Edit);
   AttributeTypeDouble MeetingLength = ats.createDouble(1152921504606847188L, "ats.Meeting Length", TEXT_PLAIN, "Length of meeting.", Edit);
   AttributeTypeString MeetingLocation = ats.createString(1152921504606847224L, "ats.Meeting Location", TEXT_PLAIN, "Location meeting is held.", Edit);
   AttributeTypeString Namespace = ats.createString(4676151691645786526L, "ats.Namespace", TEXT_PLAIN, "", Config);
   AttributeTypeDate NeedBy = ats.createDate(1152921504606847163L, "ats.Need By", TEXT_CALENDAR, "Hard schedule date that workflow must be completed.", Edit);
   AttributeTypeBoolean NextVersion = ats.createBoolean(1152921504606847157L, "ats.Next Version", TEXT_PLAIN, "True if version artifact is \"Next\" version to be released.", Config);
   AttributeTypeDouble Numeric1 = ats.createDouble(1152921504606847184L, "ats.Numeric1", TEXT_PLAIN, "Open field for user to be able to enter numbers for sorting.", Edit);
   AttributeTypeDouble Numeric2 = ats.createDouble(1152921504606847185L, "ats.Numeric2", TEXT_PLAIN, Numeric1.getDescription(), Edit);
   AttributeTypeBoolean NonFunctionalProblem = ats.createBoolean(950275235L, "ats.Non Functional Problem", TEXT_PLAIN, "", Edit);
   AttributeTypeString OperationalImpact = ats.createString(1152921504606847213L, "ats.Operational Impact", TEXT_PLAIN, "Does this change have an operational impact to the product.", Edit);
   AttributeTypeString OperationalImpactDescription = ats.createString(1152921504606847214L, "ats.Operational Impact Description", TEXT_PLAIN, "What is the operational impact to the product", Edit);
   AttributeTypeString OperationalImpactWorkaround = ats.createString(1152921504606847215L, "ats.Operational Impact Workaround", TEXT_PLAIN, "", Edit);
   AttributeTypeString OperationalImpactWorkaroundDescription = ats.createString(1152921504606847216L, "ats.Operational Impact Workaround Description", TEXT_PLAIN, "", Edit);
   AttributeTypeString OriginatingPcrId = ats.createString(1152922093379125418L, "ats.Originating PCR Id", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger PagesChanged = ats.createInteger(1152921504606847209L, "ats.Pages Changed", TEXT_PLAIN, "Total Pages of Changed", Edit);
   AttributeTypeInteger PagesReviewed = ats.createInteger(1152921504606847210L, "ats.Pages Reviewed", TEXT_PLAIN, "Total Pages of Reviewed", Edit);
   AttributeTypeString PcrToolId = ats.createString(1152922093370736810L, "ats.PCR Tool Id", TEXT_PLAIN, "", Edit);
   AttributeTypeString PeerReviewId = ats.createString(4231136442842667818L, "ats.Peer Review Id", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger PercentComplete = ats.createInteger(1152921504606847183L, "ats.Percent Complete", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger PercentRework = ats.createInteger(1152921504606847189L, "ats.Percent Rework", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger PlannedPoints = ats.createInteger(232851836925913430L, "ats.Planned Points", TEXT_PLAIN, "", Edit);
   PointAttributeType Points = ats.createEnum(new PointAttributeType(), Edit);
   AttributeTypeString PointsAttributeType = ats.createString(1152921573057888257L, "ats.Points Attribute Type", TEXT_PLAIN, "Used to store the agile points type name (ats.Points or ats.Points Numeric).", Edit);
   AttributeTypeDouble PointsNumeric = ats.createDouble(1728793301637070003L, "ats.Points Numeric", TEXT_PLAIN, "Abstract value that describes risk, complexity, and size of Actions as float.", DisplayHint.SingleLine, Edit);
   AttributeTypeString Priority = ats.createString(1152921504606847179L, "ats.Priority", TEXT_PLAIN, "", Edit);
   AttributeTypeString Problem = ats.createString(1152921504606847193L, "ats.Problem", TEXT_PLAIN, "Problem found during analysis.", MultiLine, Edit);
   AttributeTypeDate ProblemFirstObserved = ats.createDate(8431670117014503949L, "ats.Problem First Observed", TEXT_CALENDAR, "Date of Problem First Observed", Edit);
   AttributeTypeString ProductLineBranchId = ats.createString(8728667450560659060L, "ats.Product Line Branch Id", TEXT_PLAIN, "PL branch associated with ATS object.", Read);
   AttributeTypeArtifactId ProgramId = ats.createArtifactId(1152922093377028266L, "ats.Program Id", TEXT_PLAIN, "", AtsDisplayHint.ReadConfig);
   AttributeTypeString ProposedResolution = ats.createString(1152921504606847194L, "ats.Proposed Resolution", TEXT_PLAIN, "Recommended resolution.", MultiLine, Edit);
   AttributeTypeDate ProposedResolutionDate = ats.createDate(5780824580881083976L, "ats.Proposed Resolution Date", TEXT_CALENDAR, "", Edit);
   AttributeTypeString Rationale = ats.createString(1152922093379715242L, "ats.Rationale", TEXT_PLAIN, "", MultiLine, Edit);
   AttributeTypeArtifactId RelatedPeerWorkflowDefinitionReference = ats.createArtifactId(6245695017677665082L, "ats.Related Peer Workflow Definition Reference", TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team", Config);
   AttributeTypeString RelatedTaskWorkflowDefinitionOld = ats.createString(1152921504606847151L, "ats.Related Task Workflow Definition Old", TEXT_PLAIN, "", Read);
   AttributeTypeArtifactId RelatedTaskWorkflowDefinitionReference = ats.createArtifactId(2492475839748929444L, "ats.Related Task Workflow Definition Reference", TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow", Read);
   AttributeTypeString RelatedToState = ats.createString(1152921504606847204L, "ats.Related To State", TEXT_PLAIN, "State of parent workflow this object is related to.", Edit);
   AttributeTypeDate ReleaseDate = ats.createDate(1152921504606847175L, "ats.Release Date", TEXT_CALENDAR, "Date the changes were made available to the users.", Edit);
   AttributeTypeBoolean Released = ats.createBoolean(1152921504606847155L, "ats.Released", TEXT_PLAIN, "True if object is in a released state.", Config);
   AttributeTypeBoolean RequireTargetedVersion = ats.createBoolean(1152921504606847159L, "ats.Require Targeted Version", TEXT_PLAIN, "", Config);
   AttributeTypeString Resolution = ats.createString(1152921504606847195L, "ats.Resolution", TEXT_PLAIN, "Implementation details.", MultiLine, Edit);
   ReviewBlocksAttributeType ReviewBlocks = ats.createEnum(new ReviewBlocksAttributeType(), Read);
   AttributeTypeString ReviewDefect = ats.createString(1152921504606847222L, "ats.Review Defect", TEXT_PLAIN, "", Read);
   ReviewFormalTypeAttributeType ReviewFormalType = ats.createEnum(new ReviewFormalTypeAttributeType(), Edit);
   RiskAnalysisAttributeType RiskAnalysis = ats.createEnum(new RiskAnalysisAttributeType(), Edit);
   RiskFactorAttributeType RiskFactor = ats.createEnum(new RiskFactorAttributeType(), Edit);
   AttributeTypeDate RevisitDate = ats.createDate(8158028655410858717L, "ats.Re-Visit Date", TEXT_CALENDAR, "Date to revist this workflow", Edit);
   AttributeTypeString Role = ats.createString(1152921504606847226L, "ats.Role", TEXT_PLAIN, "", Read);
   AttributeTypeString RootCause = ats.createString(3624854321220981352L, "ats.Root Cause", TEXT_PLAIN, "", Edit);
   AttributeTypeString RuleDefinition = ats.createString(1152921504606847150L, "ats.Rule Definition", TEXT_PLAIN, "", Config);
   AttributeTypeString SignalDbSystemId = ats.createString(1153126013769613779L, "Signal Db System ID", TEXT_PLAIN, "",Config );
   AttributeTypeDate StartDate = ats.createDate(1152921504606847382L, "ats.Start Date", TEXT_CALENDAR, "", Edit);
   AttributeTypeString State = ats.createString(1152921504606847191L, "ats.State", TEXT_PLAIN, "States of workflow state machine.", Read);
   AttributeTypeString StateNotes = ats.createString(1152921504606847203L, "ats.State Notes", MediaType.TEXT_XML, "", MultiLine, Read);
   AttributeTypeString SwEnhancement = ats.createString(1152921504606847227L, "ats.SW Enhancement", TEXT_PLAIN, "", Edit, Edit);
   AttributeTypeBoolean SignalImpact = ats.createBoolean(2380093348200994L, "ats.Signal Impact", TEXT_PLAIN, "", Edit);
   AttributeTypeLong TaskSetId = ats.createLong(2412431655932432L, "ats.Task Set Id", TEXT_PLAIN, "",Config);
   AttributeTypeBoolean TaskAutoGen = ats.createBoolean(395202732487784L, "ats.Task Auto Gen", TEXT_PLAIN, "", Read);
   AttributeTypeString TaskAutoGenVersion = ats.createString(29374282544622L, "ats.Task Auto Gen Version", TEXT_PLAIN, "Version of Task Auto Generation", Read);
   AttributeTypeArtifactId TaskToChangedArtifactReference = ats.createArtifactId(1153126013769613562L, "ats.Task To Changed Artifact Reference", TEXT_PLAIN, "Task reference to the changed artifact", Read);
   AttributeTypeString TaskToChangedArtifactName = ats.createString(23524392992929L, "ats.Task To Changed Artifact Name", TEXT_PLAIN, "Task reference to the changed artifact name", Read);
   AttributeTypeBoolean TaskToChangedArtifactDeleted = ats.createBoolean(323852383249857L, "ats.Task To Changed Artifact Deleted", TEXT_PLAIN, "Referenced artifact was deleted", Read);
   AttributeTypeArtifactId TeamDefinitionReference = ats.createArtifactId(4730961339090285773L, "ats.Team Definition Reference", TEXT_PLAIN, "Team Workflow to Team Definition", Read);
   AttributeTypeBoolean TeamUsesVersions = ats.createBoolean(1152921504606847158L, "ats.Team Uses Versions", TEXT_PLAIN, "", Config);
   AttributeTypeString TaskAutoGenType = ats.createString(175464975663435993L, "ats.Task Auto Gen Type", TEXT_PLAIN, "", Read);
   AttributeTypeString TeamWorkflowArtifactType = ats.createString(1152921504606847148L, "ats.Team Workflow Artifact Type", TEXT_PLAIN, "Specific Artifact Type to use in creation of Team Workflow", Config);
   AttributeTypeString TestRunToSourceLocator = ats.createString(130595201919637916L, "ats.Test Run To Source Locator", TEXT_PLAIN, "Enter clear and concise title that can be generally understood.", Edit);
   AttributeTypeString Title = CoreAttributeTypes.Name;
   AttributeTypeString QuantityUnderReview = ats.createString(489717926240421171L, "ats.Quantity Under Review", TEXT_PLAIN, "Total Pages, LOCs, Documents, etc Changed", Edit);
   AttributeTypeInteger UnplannedPoints = ats.createInteger(284254492767020802L, "ats.Unplanned Points", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean UnplannedWork = ats.createBoolean(2421093774890249189L, "ats.Unplanned Work", TEXT_PLAIN, "", DisplayHint.YesNoBoolean, Edit);
   AttributeTypeBoolean UsesResolutionOptions = ats.createBoolean(1152921504606847154L, "ats.Uses Resolution Options", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean ValidationRequired = ats.createBoolean(1152921504606847146L, "ats.Validation Required", TEXT_PLAIN, "If selected, originator will be asked to validate the implementation.", Edit);
   AttributeTypeString ValidateChangesRanBy = ats.createString(5308411842127935014L, "ats.LBA Validate Changes Ran By", TEXT_PLAIN, "", Read, UserUserId);
   AttributeTypeDate ValidateChangesRanDate = ats.createDate(5754481976934354664L, "ats.LBA Validate Changes Ran Date", TEXT_PLAIN, "", Read);
   AttributeTypeBoolean VerificationCodeInspection = ats.createBoolean(3454966334779726518L, "ats.Verification Code Inspection", TEXT_PLAIN, "", Edit);
   AttributeTypeBoolean VersionLocked = ats.createBoolean(1152921504606847156L, "ats.Version Locked", TEXT_PLAIN, "True if version artifact is locked.", Config);
   AttributeTypeArtifactId VersionReference = ats.createArtifactId(3865529427237311670L, "ats.Version Reference", TEXT_PLAIN, "", Config);
   AttributeTypeString WcafeImpact = ats.createString(238328342584350L, "ats.WCAFE Impact", TEXT_PLAIN, "Warning, Caution, Advisory, Fault or Exceedence Impact", Read);
   AttributeTypeDouble WeeklyBenefit = ats.createDouble(1152921504606847186L, "ats.Weekly Benefit", TEXT_PLAIN, "Estimated number of hours that will be saved over a single year if this change is completed.", Edit);
   AttributeTypeString Workaround = ats.createString(1311070965L, "ats.Workaround", TEXT_PLAIN, "", Edit);
   AttributeTypeString WorkPackage = ats.createString(1152921504606847206L, "ats.Work Package", TEXT_PLAIN, "Designated accounting work package for completing workflow.", DisplayHint.SingleLine, Edit);
   AttributeTypeString WorkPackageId = ats.createString(1152921504606847872L, "ats.Work Package Id", TEXT_PLAIN, "", Read);
   AttributeTypeString WorkPackageProgram = ats.createString(1152921504606847873L, "ats.Work Package Program", TEXT_PLAIN, "", Read);
   AttributeTypeArtifactId WorkPackageReference = ats.createArtifactId(473096133909456789L, "ats.Work Package Reference", TEXT_PLAIN, "Designated accounting work package for completing workflow.", Read);
   WorkPackageTypeAttributeType WorkPackageType = ats.createEnum(new WorkPackageTypeAttributeType(), Edit);
   AttributeTypeArtifactId WorkflowDefinitionReference = ats.createArtifactId(53049621055799825L, "ats.Workflow Definition Reference", TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact", Read);
   AttributeTypeString WorkflowNotes = ats.createString(1152921504606847205L, "ats.Notes", TEXT_PLAIN, "Notes applicable to ATS Workflow", MultiLine, Edit);
   AttributeTypeString WorkType = ats.createString(72063456955810043L, "ats.Work Type", TEXT_PLAIN, "Work Type of this Team.", Config);

   // IcTeam Types
   AgileChangeTypeAttributeType AgileChangeType  = ats.createEnum(new AgileChangeTypeAttributeType(), Read);
   AttributeTypeString Condition = ats.createString(1152921504606851666L, "req.Condition", TEXT_PLAIN, "Stores the prerequsite data for workflow", Read);
   AttributeTypeString GUID = ats.createString(1152921504606851425L, "task.guid", TEXT_PLAIN, "Guid of the task", Read);
   AttributeTypeString Information = ats.createString(1152921504606851665L, "tag.Information", TEXT_PLAIN, "Addition of tags to workflow", Read);
   AttributeTypeString Rank = ats.createString(1152921504606851480L, "agile.Rank", TEXT_PLAIN, "Holds the Rank of workflow for prioritization", Read);
   AttributeTypeString Shortname = ats.createStringNoTag(1152921504606847340L, "Shortname", TEXT_PLAIN, "Shorter name of project", Read);
   AttributeTypeString TaskCountForProject = ats.createString(1152921504606849831L, "ats.TaskCountForProject", TEXT_PLAIN, "Count value to hold the number of tasks under a project", Read);
   AttributeTypeString BurnDownData = ats.createString(1152921504606851667L, "burndown.data", TEXT_PLAIN, "Stores the modified remaining time and date", Read);   AttributeTypeString Prefix = ats.createString(1152921504606851670L, "prefix", TEXT_PLAIN, "Prefix", Read);
   AttributeTypeString Identifier = ats.createString(1152921504606851671L, "id", TEXT_PLAIN, "Identifier", Read);
   AttributeTypeString LastChange = ats.createString(1152921504606851672L, "last.change", TEXT_PLAIN, "LastChange", Read);
   AttributeTypeString MaxLength = ats.createString(1152921504606851673L, "max.length", TEXT_PLAIN, "MaxLength", Read);
   AttributeTypeString LongName = ats.createString(1152921504606851674L, "long.name", TEXT_PLAIN, "LongName", Read);

   // Remove after 1.0.0 full release
   AttributeTypeString WorkflowDefinition = ats.createString(1152921504606847149L, "ats.Workflow Definition", TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact", Read);

   // Leave this attribute definition for other OSEE sites to convert
   AttributeTypeString BaselineBranchGuid = ats.createString(1152921504606847145L, "ats.Baseline Branch Guid", TEXT_PLAIN, "", Read);
   // @formatter:on

}