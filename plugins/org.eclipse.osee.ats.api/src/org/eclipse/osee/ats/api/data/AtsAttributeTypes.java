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

import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import javax.ws.rs.core.MediaType;
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
   ApplicableToProgramAttributeType ApplicableToProgram = ats.createEnum(new ApplicableToProgramAttributeType());
   AttributeTypeLong ApproveRequestedHoursBy = ats.createLong(224884848210198L, "ats.Approve Requested Hours By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate ApproveRequestedHoursDate = ats.createDate(83388338833828L, "ats.Approve Requested Hours Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString Assumptions = ats.createString(593196463063939110L, "ats.Assumptions", MediaType.TEXT_PLAIN, "", DisplayHint.MultiLine);
   AttributeTypeString AtsId = ats.createString(1152921504606847877L, "ats.Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AtsIdPrefix = ats.createString(1162773128791720837L, "ats.ATS Id Prefix", MediaType.TEXT_PLAIN, "ATS Id Prefix");
   AttributeTypeString AtsIdSequenceName = ats.createString(1163054603768431493L, "ats.ATS Id Sequence Name", MediaType.TEXT_PLAIN, "ATS Id Sequence Name");
   AttributeTypeString BaselineBranchId = ats.createString(1152932018686787753L, "ats.Baseline Branch Id", MediaType.TEXT_PLAIN, "Baseline branch associated with ATS object.");
   BitStateEnumAttributeType BitState = ats.createEnum(new BitStateEnumAttributeType());
   AttributeTypeString BlockedReason = ats.createString(7797797474874870503L, "ats.Blocked Reason", MediaType.TEXT_PLAIN, "Reason for action being blocked");
   AttributeTypeString BranchMetrics = ats.createString(1152921504606847190L, "ats.Branch Metrics", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId BitConfig = ats.createArtifactId(2382915711248L, "ats.BIT Config", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CAM = ats.createString(1152921596009727571L, "ats.CAM", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CSCI = ats.createString(72063457007112443L, "ats.CSCI", MediaType.TEXT_PLAIN, "CSCI this Team is reponsible for.");
   AttributeTypeString CancelledBy = ats.createString(1152921504606847170L, "ats.Cancelled By", MediaType.TEXT_PLAIN, "UserId of the user who cancelled workflow.");
   AttributeTypeDate CancelledDate = ats.createDate(1152921504606847169L, "ats.Cancelled Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was cancelled.");
   AttributeTypeString CancelledFromState = ats.createString(1152921504606847172L, "ats.Cancelled From State", MediaType.TEXT_PLAIN, "State workflow was in when cancelled.");
   AttributeTypeString CancelledReason = ats.createString(1152921504606847171L, "ats.Cancelled Reason", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled.");
   CancelledReasonEnumAttributeType CancelledReasonEnum = ats.createEnum(new CancelledReasonEnumAttributeType());
   AttributeTypeString CancelledReasonDetails = ats.createString(8279626026752029322L, "ats.Cancelled Reason Details", MediaType.TEXT_PLAIN, "Explanation of why worklfow was cancelled.");
   AttributeTypeString Category1 = ats.createString(1152921504606847212L, "ats.Category 1", MediaType.TEXT_PLAIN, "Open field for user to be able to enter text to use for categorizing/sorting.");
   AttributeTypeString Category2 = ats.createString(1152921504606847217L, "ats.Category 2", MediaType.TEXT_PLAIN, Category1.getDescription());
   AttributeTypeString Category3 = ats.createString(1152921504606847218L, "ats.Category 3", MediaType.TEXT_PLAIN, Category1.getDescription());
   AttributeTypeString ChangeType = ats.createString(1152921504606847180L, "ats.Change Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean ClosureActive = ats.createBoolean(1152921875139002555L, "ats.Closure Active", MediaType.TEXT_PLAIN, "Closure Active status of Program");
   ClosureStateAttributeType ClosureState = ats.createEnum(new ClosureStateAttributeType());
   AttributeTypeString CognosUniqueId = ats.createString(72063457009467630L, "ats.Cognos Unique Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CommitOverride = ats.createString(104739333325561L, "ats.Commit Override", MediaType.TEXT_PLAIN, "Commit was overridden by user.");
   AttributeTypeString CompletedBy = ats.createString(1152921504606847167L, "ats.Completed By", MediaType.TEXT_PLAIN, "UserId of the user who completed workflow.");
   AttributeTypeDate CompletedDate = ats.createDate(1152921504606847166L, "ats.Completed Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was completed.");
   AttributeTypeString CompletedFromState = ats.createString(1152921504606847168L, "ats.Completed From State", MediaType.TEXT_PLAIN, "State workflow was in when completed.");
   AttributeTypeString ControlAccount = ats.createString(3475568422796552185L, "ats.Control Account", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CreatedBy = ats.createString(1152921504606847174L, "ats.Created By", MediaType.TEXT_PLAIN, "UserId of the user who created the workflow.");
   AttributeTypeDate CreatedDate = ats.createDate(1152921504606847173L, "ats.Created Date", AttributeTypeToken.TEXT_CALENDAR, "Date the workflow was created.");
   AttributeTypeString CurrentState = ats.createString(1152921504606847192L, "ats.Current State", MediaType.TEXT_PLAIN, "Current state of workflow state machine.");
   AttributeTypeString CurrentStateName = ats.createString(4689644240272725681L, "ats.Current State Name", MediaType.TEXT_PLAIN, "Current state name of workflow state machine.");
   AttributeTypeString CurrentStateAssignees = ats.createString(1902418199157448550L, "ats.Current State Assignees", MediaType.TEXT_PLAIN, "Current state assignees as user art id.");
   AttributeTypeString CurrentStateType = ats.createString(1152921504606847147L, "ats.Current State Type", MediaType.TEXT_PLAIN, "Type of Current State: InWork, Completed or Cancelled.");
   AttributeTypeBoolean CrashOrBlankDisplay = ats.createBoolean(1601107563L, "ats.Crash or Blank Display", MediaType.TEXT_PLAIN, "Crash OR cockpit displays are blank", DisplayHint.YesNoBoolean);
   AttributeTypeString Decision = ats.createString(1152921504606847221L, "ats.Decision", MediaType.TEXT_PLAIN, "Option selected during decision review.");
   AttributeTypeString DecisionReviewOptions = ats.createString(1152921504606847220L, "ats.Decision Review Options", MediaType.TEXT_PLAIN, "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>\")");
   AttributeTypeBoolean Default = ats.createBoolean(1152921875139002538L, "ats.Default", MediaType.TEXT_PLAIN, "Default");
   AttributeTypeString Description = ats.createString(1152921504606847196L, "ats.Description", MediaType.TEXT_PLAIN, "Detailed explanation.", DisplayHint.MultiLine);
   AttributeTypeString DslSheet = ats.createString(1152921504606847197L, "ats.DSL Sheet", MediaType.TEXT_PLAIN, ""); //TODO: In production but not used in code anymore, need to have a talk about how to deal with these
   AttributeTypeString DuplicatedPcrId = ats.createString(1152922093378076842L, "ats.Duplicated PCR Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate EndDate = ats.createDate(1152921504606847383L, "ats.End Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString EstimateAssumptions = ats.createString(7714952282787917834L, "ats.Estimate Assumptions", MediaType.TEXT_PLAIN, "", DisplayHint.MultiLine);
   AttributeTypeDate EstimatedCompletionDate = ats.createDate(1152921504606847165L, "ats.Estimated Completion Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be completed.");
   AttributeTypeDouble EstimatedHours = ats.createDouble(1152921504606847182L, "ats.Estimated Hours", MediaType.TEXT_PLAIN, "Hours estimated to implement the changes associated with this Action.\\nIncludes estimated hours for workflows, tasks and reviews.", DisplayHint.SingleLine);
   AttributeTypeDate EstimatedReleaseDate = ats.createDate(1152921504606847164L, "ats.Estimated Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes will be made available to the users.");
   AttributeTypeString ExternalReference = ats.createString(52148954699L, "ats.External Reference", MediaType.TEXT_PLAIN, "Associated External PCR Number");
   AttributeTypeString FullName = ats.createString(1152921504606847198L, "ats.Full Name", MediaType.TEXT_PLAIN, "Expanded and descriptive name.");
   AttributeTypeArtifactId FeatureImpactReference = ats.createArtifactId(1148992834242L, "ats.Feature Impacted", MediaType.TEXT_PLAIN, "");
   AttributeTypeString GoalOrderVote = ats.createString(1152921504606847211L, "ats.Goal Order Vote", MediaType.TEXT_PLAIN, "Vote for order item belongs to within goal.");
   AttributeTypeString HoldReason = ats.createString(5465485151546987972L, "ats.Hold Reason", MediaType.TEXT_PLAIN, "Reason for action being held");
   AttributeTypeDate Holiday = ats.createDate(72064629481881851L, "ats.Holiday", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeDouble HoursPerWorkDay = ats.createDouble(1152921504606847187L, "ats.Hours Per Work Day", MediaType.TEXT_PLAIN, "");
   AttributeTypeString HowToReproduceProblem = ats.createString(836807199L, "ats.How to reproduce the problem", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean IsDcs = ats.createBoolean(3199233956221339044L, "ats.Is DCS", MediaType.TEXT_PLAIN, "Is Direct Commercial Sale");
   AttributeTypeString ImpactToMissionOrCrew = ats.createString(1442232314L, "ats.Impact to Mission or Crew", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Journal = ats.createString(4323598592300832478L, "ats.Journal", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId JournalSubscriber = ats.createArtifactId(42051756273953L, "ats.Journal Subscriber",MediaType.TEXT_PLAIN,  "Artifact Id of User Subscribed to Journal Notifications");
   AttributeTypeString KanbanIgnoreStates = ats.createString(726700946264587643L, "ats.kb.Ignore States", MediaType.TEXT_PLAIN, "");
   AttributeTypeString KanbanStoryName = ats.createString(72645877009467643L, "ats.kb.Story Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeString LegacyPcrId = ats.createString(1152921504606847219L, "ats.Legacy PCR Id", MediaType.TEXT_PLAIN, "Field to register problem change report id from legacy items imported into ATS.");
   AttributeTypeString LegacyBuildId = ats.createString(4636732132432803380L, "ats.Legacy Build Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger LocChanged = ats.createInteger(1152921504606847207L, "ats.LOC Changed", MediaType.TEXT_PLAIN, "Total Lines of Code Changed");
   AttributeTypeInteger LocReviewed = ats.createInteger(1152921504606847208L, "ats.LOC Reviewed", MediaType.TEXT_PLAIN, "Total Lines of Code Reviewed");
   AttributeTypeString Location = ats.createString(1152921504606847223L, "ats.Location", MediaType.TEXT_PLAIN, "Enter location of materials to review.", DisplayHint.MultiLine);
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
   AttributeTypeBoolean NonFunctionalProblem = ats.createBoolean(950275235L, "ats.Non Functional Problem", MediaType.TEXT_PLAIN, "");
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
   PointAttributeType Points = ats.createEnum(new PointAttributeType());
   AttributeTypeString PointsAttributeType = ats.createString(1152921573057888257L, "ats.Points Attribute Type", MediaType.TEXT_PLAIN, "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   AttributeTypeDouble PointsNumeric = ats.createDouble(1728793301637070003L, "ats.Points Numeric", MediaType.TEXT_PLAIN, "Abstract value that describes risk, complexity, and size of Actions as float.", DisplayHint.SingleLine);
   AttributeTypeString Priority = ats.createString(1152921504606847179L, "ats.Priority", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Problem = ats.createString(1152921504606847193L, "ats.Problem", MediaType.TEXT_PLAIN, "Problem found during analysis.", DisplayHint.MultiLine);
   AttributeTypeDate ProblemFirstObserved = ats.createDate(8431670117014503949L, "ats.Problem First Observed", AttributeTypeToken.TEXT_CALENDAR, "Date of Problem First Observed");
   AttributeTypeLong ProductLineApprovedBy = ats.createLong(7838821957985211888L, "ats.Product Line Approved By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate ProductLineApprovedDate = ats.createDate(735226602374161400L, "ats.Product Line Approved Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString ProductLineBranchId = ats.createString(8728667450560659060L, "ats.Product Line Branch Id", MediaType.TEXT_PLAIN, "PL branch associated with ATS object.");
   AttributeTypeArtifactId ProgramId = ats.createArtifactId(1152922093377028266L, "ats.Program Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ProposedResolution = ats.createString(1152921504606847194L, "ats.Proposed Resolution", MediaType.TEXT_PLAIN, "Recommended resolution.", DisplayHint.MultiLine);
   AttributeTypeString Rationale = ats.createString(1152922093379715242L, "ats.Rationale", MediaType.TEXT_PLAIN, "", DisplayHint.MultiLine);
   AttributeTypeArtifactId RelatedPeerWorkflowDefinitionReference = ats.createArtifactId(6245695017677665082L, "ats.Related Peer Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Peer To Peer Reviews for this Team");
   AttributeTypeString RelatedTaskWorkflowDefinitionOld = ats.createString(1152921504606847151L, "ats.Related Task Workflow Definition Old", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId RelatedTaskWorkflowDefinitionReference = ats.createArtifactId(2492475839748929444L, "ats.Related Task Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by Tasks related to this Workflow");
   AttributeTypeString RelatedToState = ats.createString(1152921504606847204L, "ats.Related To State", MediaType.TEXT_PLAIN, "State of parent workflow this object is related to.");
   AttributeTypeDate ReleaseDate = ats.createDate(1152921504606847175L, "ats.Release Date", AttributeTypeToken.TEXT_CALENDAR, "Date the changes were made available to the users.");
   AttributeTypeBoolean Released = ats.createBoolean(1152921504606847155L, "ats.Released", MediaType.TEXT_PLAIN, "True if object is in a released state.");
   AttributeTypeBoolean RequireTargetedVersion = ats.createBoolean(1152921504606847159L, "ats.Require Targeted Version", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Resolution = ats.createString(1152921504606847195L, "ats.Resolution", MediaType.TEXT_PLAIN, "Implementation details.", DisplayHint.MultiLine);
   ReviewBlocksAttributeType ReviewBlocks = ats.createEnum(new ReviewBlocksAttributeType());
   AttributeTypeString ReviewDefect = ats.createString(1152921504606847222L, "ats.Review Defect", MediaType.TEXT_PLAIN, "");
   ReviewFormalTypeAttributeType ReviewFormalType = ats.createEnum(new ReviewFormalTypeAttributeType());
   RiskAnalysisAttributeType RiskAnalysis = ats.createEnum(new RiskAnalysisAttributeType());
   RiskFactorAttributeType RiskFactor = ats.createEnum(new RiskFactorAttributeType());
   AttributeTypeDate RevisitDate = ats.createDate(8158028655410858717L, "ats.Re-Visit Date", AttributeTypeToken.TEXT_CALENDAR, "Date to revist this workflow");
   AttributeTypeString Role = ats.createString(1152921504606847226L, "ats.Role", MediaType.TEXT_PLAIN, "");
   AttributeTypeString RootCause = ats.createString(3624854321220981352L, "ats.Root Cause", MediaType.TEXT_PLAIN, "");
   AttributeTypeString RuleDefinition = ats.createString(1152921504606847150L, "ats.Rule Definition", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SignalDbSystemId = ats.createString(1153126013769613779L, "Signal Db System ID", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate StartDate = ats.createDate(1152921504606847382L, "ats.Start Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString State = ats.createString(1152921504606847191L, "ats.State", MediaType.TEXT_PLAIN, "States of workflow state machine.");
   AttributeTypeString StateNotes = ats.createString(1152921504606847203L, "ats.State Notes", MediaType.TEXT_XML, "", DisplayHint.MultiLine);
   AttributeTypeString SwEnhancement = ats.createString(1152921504606847227L, "ats.SW Enhancement", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean SignalImpact = ats.createBoolean(2380093348200994L, "ats.Signal Impact", MediaType.TEXT_PLAIN, "");
   AttributeTypeLong TaskSetId = ats.createLong(2412431655932432L, "ats.Task Set Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean TaskAutoGen = ats.createBoolean(395202732487784L, "ats.Task Auto Gen", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TaskAutoGenVersion = ats.createString(29374282544622L, "ats.Task Auto Gen Version", MediaType.TEXT_PLAIN, "Version of Task Auto Generation");
   AttributeTypeArtifactId TaskToChangedArtifactReference = ats.createArtifactId(1153126013769613562L, "ats.Task To Changed Artifact Reference", MediaType.TEXT_PLAIN, "Task reference to the changed artifact");
   AttributeTypeString TaskToChangedArtifactName = ats.createString(23524392992929L, "ats.Task To Changed Artifact Name", MediaType.TEXT_PLAIN, "Task reference to the changed artifact name");
   AttributeTypeBoolean TaskToChangedArtifactDeleted = ats.createBoolean(323852383249857L, "ats.Task To Changed Artifact Deleted", MediaType.TEXT_PLAIN, "Referenced artifact was deleted");
   AttributeTypeArtifactId TeamDefinitionReference = ats.createArtifactId(4730961339090285773L, "ats.Team Definition Reference", MediaType.TEXT_PLAIN, "Team Workflow to Team Definition");
   AttributeTypeBoolean TeamUsesVersions = ats.createBoolean(1152921504606847158L, "ats.Team Uses Versions", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TaskAutoGenType = ats.createString(175464975663435993L, "ats.Task Auto Gen Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TeamWorkflowArtifactType = ats.createString(1152921504606847148L, "ats.Team Workflow Artifact Type", MediaType.TEXT_PLAIN, "Specific Artifact Type to use in creation of Team Workflow");
   AttributeTypeString TestRunToSourceLocator = ats.createString(130595201919637916L, "ats.Test Run To Source Locator", MediaType.TEXT_PLAIN, "Enter clear and concise title that can be generally understood.");
   AttributeTypeString Title = CoreAttributeTypes.Name;
   AttributeTypeDate ReviewedByDate = ats.createDate(2436278456841462630L, "ats.Reviewed Date", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeLong ReviewedBy = ats.createLong(4020478495150345644L, "ats.Reviewed By", MediaType.TEXT_PLAIN, "");
   AttributeTypeString QuantityUnderReview = ats.createString(489717926240421171L, "ats.Quantity Under Review", MediaType.TEXT_PLAIN, "Total Pages, LOCs, Documents, etc Changed");
   AttributeTypeInteger UnplannedPoints = ats.createInteger(284254492767020802L, "ats.Unplanned Points", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean UnplannedWork = ats.createBoolean(2421093774890249189L, "ats.Unplanned Work", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean UsesResolutionOptions = ats.createBoolean(1152921504606847154L, "ats.Uses Resolution Options", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean ValidationRequired = ats.createBoolean(1152921504606847146L, "ats.Validation Required", MediaType.TEXT_PLAIN, "If selected, originator will be asked to validate the implementation.");
   AttributeTypeString ValidateChangesRanBy = ats.createString(5308411842127935014L, "ats.LBA Validate Changes Ran By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate ValidateChangesRanDate = ats.createDate(5754481976934354664L, "ats.LBA Validate Changes Ran Date", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean VerificationCodeInspection = ats.createBoolean(3454966334779726518L, "ats.Verification Code Inspection", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean VersionLocked = ats.createBoolean(1152921504606847156L, "ats.Version Locked", MediaType.TEXT_PLAIN, "True if version artifact is locked.");
   AttributeTypeArtifactId VersionReference = ats.createArtifactId(3865529427237311670L, "ats.Version Reference", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WcafeImpact = ats.createString(238328342584350L, "ats.WCAFE Impact", MediaType.TEXT_PLAIN, "Warning, Caution, Advisory, Fault or Exceedence Impact");
   AttributeTypeDouble WeeklyBenefit = ats.createDouble(1152921504606847186L, "ats.Weekly Benefit", MediaType.TEXT_PLAIN, "Estimated number of hours that will be saved over a single year if this change is completed.");
   AttributeTypeString Workaround = ats.createString(1311070965L, "ats.Workaround", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPackage = ats.createString(1152921504606847206L, "ats.Work Package", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow.", DisplayHint.SingleLine);
   AttributeTypeString WorkPackageId = ats.createString(1152921504606847872L, "ats.Work Package Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPackageProgram = ats.createString(1152921504606847873L, "ats.Work Package Program", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId WorkPackageReference = ats.createArtifactId(473096133909456789L, "ats.Work Package Reference", MediaType.TEXT_PLAIN, "Designated accounting work package for completing workflow.");
   WorkPackageTypeAttributeType WorkPackageType = ats.createEnum(new WorkPackageTypeAttributeType());
   AttributeTypeArtifactId WorkflowDefinitionReference = ats.createArtifactId(53049621055799825L, "ats.Workflow Definition Reference", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact");
   AttributeTypeString WorkflowNotes = ats.createString(1152921504606847205L, "ats.Workflow Notes", MediaType.TEXT_PLAIN, "Notes applicable to ATS Workflow", DisplayHint.MultiLine);
   AttributeTypeString WorkType = ats.createString(72063456955810043L, "ats.Work Type", MediaType.TEXT_PLAIN, "Work Type of this Team.");

   // IcTeam Types
   AgileChangeTypeAttributeType AgileChangeType  = ats.createEnum(new AgileChangeTypeAttributeType());
   AttributeTypeString Condition = ats.createString(1152921504606851666L, "req.Condition", MediaType.TEXT_PLAIN, "Stores the prerequsite data for workflow");
   AttributeTypeString GUID = ats.createString(1152921504606851425L, "task.guid", MediaType.TEXT_PLAIN, "Guid of the task");
   AttributeTypeString Information = ats.createString(1152921504606851665L, "tag.Information", MediaType.TEXT_PLAIN, "Addition of tags to workflow");
   AttributeTypeString Rank = ats.createString(1152921504606851480L, "agile.Rank", MediaType.TEXT_PLAIN, "Holds the Rank of workflow for prioritization");
   AttributeTypeString Shortname = ats.createStringNoTag(1152921504606847340L, "Shortname", MediaType.TEXT_PLAIN, "Shorter name of project");
   AttributeTypeString TaskCountForProject = ats.createString(1152921504606849831L, "ats.TaskCountForProject", MediaType.TEXT_PLAIN, "Count value to hold the number of tasks under a project");
   AttributeTypeString BurnDownData = ats.createString(1152921504606851667L, "burndown.data", MediaType.TEXT_PLAIN, "Stores the modified remaining time and date");   AttributeTypeString Prefix = ats.createString(1152921504606851670L, "prefix", MediaType.TEXT_PLAIN, "Prefix");
   AttributeTypeString Identifier = ats.createString(1152921504606851671L, "id", MediaType.TEXT_PLAIN, "Identifier");
   AttributeTypeString LastChange = ats.createString(1152921504606851672L, "last.change", MediaType.TEXT_PLAIN, "LastChange");
   AttributeTypeString MaxLength = ats.createString(1152921504606851673L, "max.length", MediaType.TEXT_PLAIN, "MaxLength");
   AttributeTypeString LongName = ats.createString(1152921504606851674L, "long.name", MediaType.TEXT_PLAIN, "LongName");

   // Remove after 1.0.0 full release
   AttributeTypeString WorkflowDefinition = ats.createString(1152921504606847149L, "ats.Workflow Definition", MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact");

   // Leave this attribute definition for other OSEE sites to convert
   AttributeTypeString BaselineBranchGuid = ats.createString(1152921504606847145L, "ats.Baseline Branch Guid", MediaType.TEXT_PLAIN, "");
   // @formatter:on

}