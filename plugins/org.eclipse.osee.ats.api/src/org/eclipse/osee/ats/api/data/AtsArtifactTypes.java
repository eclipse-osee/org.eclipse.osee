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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.atsDemo;
import static org.eclipse.osee.ats.api.util.AtsImage.ACTION;
import static org.eclipse.osee.ats.api.util.AtsImage.ACTIONABLE_ITEM;
import static org.eclipse.osee.ats.api.util.AtsImage.BUILD_IMPACT;
import static org.eclipse.osee.ats.api.util.AtsImage.CHANGE_REQUEST;
import static org.eclipse.osee.ats.api.util.AtsImage.DECISION_REVIEW;
import static org.eclipse.osee.ats.api.util.AtsImage.GOAL;
import static org.eclipse.osee.ats.api.util.AtsImage.INSERTION;
import static org.eclipse.osee.ats.api.util.AtsImage.INSERTION_ACTIVITY;
import static org.eclipse.osee.ats.api.util.AtsImage.PEER_REVIEW;
import static org.eclipse.osee.ats.api.util.AtsImage.PROGRAM;
import static org.eclipse.osee.ats.api.util.AtsImage.TASK;
import static org.eclipse.osee.ats.api.util.AtsImage.TEAM_DEFINITION;
import static org.eclipse.osee.ats.api.util.AtsImage.VERSION;
import static org.eclipse.osee.ats.api.util.AtsImage.WORKFLOW_DEFINITION;
import static org.eclipse.osee.ats.api.util.AtsImage.WORK_PACKAGE;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractAccessControlled;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.BranchDiffData;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitBranchName;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitRepoName;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Notes;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.MaterialIcon;

/**
 * @author Donald G. Dunne
 */
public interface AtsArtifactTypes {

   // @formatter:off

   // Build Impact Data
   ArtifactTypeToken BuildImpactData = ats.add(ats.artifactType(2629918707103L, "Build Impact Data", false, BUILD_IMPACT, Artifact)
      .zeroOrOne(VersionReference)
      .any(BitConfig)
      .zeroOrOne(ChangeType)
      .zeroOrOne(Priority)
      .zeroOrOne(Workaround)
      .zeroOrOne(ImpactToMissionOrCrew)
      .zeroOrOne(BitState));

   // Base ATS type
   ArtifactTypeToken AtsArtifact = ats.add(ats.artifactType(63L, "ats.Ats Artifact", true, Artifact)
      .zeroOrOne(Description)
      .zeroOrOne(GoalOrderVote));

   // ATS Config Objects
   ArtifactTypeToken AtsConfigArtifact = ats.add(ats.artifactType(801L, "ats.Ats Config Artifact", true, Artifact)
      .exactlyOne(Active, Boolean.TRUE));

   ArtifactTypeToken AtsTeamDefinitionOrAi = ats.add(ats.artifactType(803L, "ats.Ats Team Definition or AI", true, AtsConfigArtifact)
      .zeroOrOne(Actionable, Boolean.TRUE)
      .any(AtsAttributeTypes.RuleDefinition));

   ArtifactTypeToken ActionableItem = ats.add(ats.artifactType(69L, "Actionable Item", false, ACTIONABLE_ITEM, new MaterialIcon("call_to_action"), AbstractAccessControlled, AtsTeamDefinitionOrAi)
      .zeroOrOne(AllowUserActionCreation)
      .any(CSCI)
      .zeroOrOne(ProgramId)
      .any(WorkType));

   ArtifactTypeToken Country = ats.add(ats.artifactType(4955822638391722788L, "Country", false, new MaterialIcon("map"), AtsConfigArtifact));

   ArtifactTypeToken Insertion = ats.add(ats.artifactType(1735587136604728792L, "Insertion", false, INSERTION,  AtsConfigArtifact)
      .zeroOrOne(Description)
      .zeroOrOne(EndDate)
      .zeroOrOne(PointsNumeric)
      .zeroOrOne(StartDate));

   ArtifactTypeToken InsertionActivity = ats.add(ats.artifactType(3943415539127781884L, "Insertion Activity", false, INSERTION_ACTIVITY, AtsConfigArtifact)
      .zeroOrOne(Description));

   ArtifactTypeToken Program = ats.add(ats.artifactType(52374361342017540L, "Program", false, PROGRAM, new MaterialIcon("code"), AtsConfigArtifact)
      .any(CSCI)
      .zeroOrOne(ClosureState, null)
      .zeroOrOne(Description)
      .zeroOrOne(Namespace)
      .zeroOrOne(ProductLineBranchId)
      .zeroOrOne(TeamDefinitionReference));

   ArtifactTypeToken ResponsibleTeam = ats.add(ats.artifactType(8943243743202487405L, "Responsible Team", false, AtsTeamDefinitionOrAi));

   ArtifactTypeToken ReleaseArtifact = ats.add(ats.artifactType(61L, "ats.Release Artifact", false, Artifact)
      .zeroOrOne(Released));

   ArtifactTypeToken TeamDefinition = ats.add(ats.artifactType(68L, "Team Definition", false, TEAM_DEFINITION, new MaterialIcon("description"), AbstractAccessControlled, ResponsibleTeam)
      .zeroOrOne(ActionDetailsFormat)
      .zeroOrOne(AllowCommitBranch, Boolean.TRUE)
      .zeroOrOne(AllowCreateBranch, Boolean.TRUE)
      .zeroOrOne(AtsIdPrefix)
      .zeroOrOne(AtsIdSequenceName)
      .zeroOrOne(BaselineBranchId)
      .any(CSCI)
      .zeroOrOne(ClosureActive)
      .zeroOrOne(FullName)
      .zeroOrOne(HoursPerWorkDay)
      .zeroOrOne(ProgramId)
      .any(RelatedPeerWorkflowDefinitionReference)
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld)
      .any(RelatedTaskWorkflowDefinitionReference)
      .zeroOrOne(RequireTargetedVersion)
      .any(TaskSetId)
      .zeroOrOne(TeamUsesVersions)
      .zeroOrOne(TeamWorkflowArtifactType)
      .any(WorkType)
      .zeroOrOne(WorkflowDefinitionReference));
   ArtifactTypeToken Version = ats.add(ats.artifactType(70L, "Version", false, VERSION, AtsArtifact)
      .zeroOrOne(AllowCommitBranch, Boolean.TRUE)
      .zeroOrOne(AllowCreateBranch, Boolean.TRUE)
      .zeroOrOne(AllowWebExport)
      .zeroOrOne(BaselineBranchId)
      .zeroOrOne(ClosureState, null)
      .zeroOrOne(EstimatedReleaseDate)
      .zeroOrOne(FullName)
      .zeroOrOne(NextVersion)
      .zeroOrOne(ReleaseDate)
      .zeroOrOne(StartDate)
      .zeroOrOne(Released)
      .zeroOrOne(TestRunToSourceLocator)
      .zeroOrOne(VersionLocked)
      .zeroOrOne(SignalDbSystemId)
      .zeroOrOne(IsDcs)
      .zeroOrOne(LegacyBuildId));

   ArtifactTypeToken WorkDefinition = ats.add(ats.artifactType(62L, "Work Definition", false, WORKFLOW_DEFINITION, Artifact)
      .zeroOrOne(DslSheet));

   /**
    *  This will be removed/purged after all work packages are converted to AtsAttributeTypes.WorkPackage
    *  (TW24503) and all attribute types and art type is purged
    */
   ArtifactTypeToken WorkPackage = ats.add(ats.artifactType(802L, "Work Package", false, WORK_PACKAGE, new MaterialIcon("cases"), Artifact)
      .exactlyOne(Active, Boolean.TRUE)
      .zeroOrOne(ActivityId)
      .zeroOrOne(ActivityName)
      .zeroOrOne(CAM)
      .zeroOrOne(CognosUniqueId)
      .zeroOrOne(ControlAccount)
      .zeroOrOne(Description)
      .zeroOrOne(EndDate)
      .zeroOrOne(EstimatedHours)
      .any(Notes)
      .zeroOrOne(PercentComplete)
      .zeroOrOne(PointsNumeric)
      .zeroOrOne(StartDate)
      .zeroOrOne(WorkPackageId)
      .zeroOrOne(WorkPackageProgram)
      .zeroOrOne(WorkPackageType, WorkPackageType.Discrete));


   // ATS Workflows
   ArtifactTypeToken Action = ats.add(ats.artifactType(67L, "Action", false, ACTION, AtsArtifact)
      .any(ActionableItemReference)
      .exactlyOne(AtsId, "0")
      .zeroOrOne(ChangeType, null)
      .zeroOrOne(NeedBy)
      .zeroOrOne(Priority, null)
      .zeroOrOne(ValidationRequired));

   ArtifactTypeToken AbstractWorkflowArtifact = ats.add(ats.artifactType(71L, "ats.Workflow Artifact", true, AtsArtifact)
      .any(JournalSubscriber)
      .exactlyOne(AtsId, "0")
      .zeroOrOne(Assumptions)
      .zeroOrOne(BlockedReason)
      .zeroOrOne(BranchDiffData)
      .zeroOrOne(HoldReason)
      .zeroOrOne(CancelledReasonEnum, null)
      .zeroOrOne(CancelledBy)
      .zeroOrOne(CancelledDate)
      .zeroOrOne(CancelledFromState)
      .zeroOrOne(CancelledReason)
      .zeroOrOne(CancelledReasonDetails)
      .zeroOrOne(Category1)
      .zeroOrOne(Category2)
      .zeroOrOne(Category3)
      .zeroOrOne(Journal)
      .zeroOrOne(CompletedBy)
      .zeroOrOne(CompletedDate)
      .zeroOrOne(CompletedFromState)
      .zeroOrOne(CreatedBy)
      .zeroOrOne(CreatedDate)
      .zeroOrOne(CurrentState)
      .zeroOrOne(CurrentStateName)
      .any(CurrentStateAssignee)
      .zeroOrOne(CurrentStateType)
      .zeroOrOne(EndDate)
      .zeroOrOne(EstimatedCompletionDate)
      .zeroOrOne(EstimatedHours)
      .zeroOrOne(EstimatedReleaseDate)
      .zeroOrOne(ExternalReference)
      .zeroOrOne(HoursSpent)
      .zeroOrOne(Log)
      .zeroOrOne(Numeric1)
      .zeroOrOne(Numeric2)
      .zeroOrOne(PeerReviewId)
      .zeroOrOne(PercentComplete)
      .zeroOrOne(Points, Points.P_1)
      .zeroOrOne(PointsNumeric)
      .zeroOrOne(ReleaseDate)
      .zeroOrOne(Resolution)
      .zeroOrOne(StartDate)
      .any(State)
      .any(StateNotes)
      .zeroOrOne(ReviewedBy)
      .zeroOrOne(ReviewedByDate)
      .zeroOrOne(UnplannedWork)
      .any(WebExportReviewed)
      .zeroOrOne(AtsAttributeTypes.WorkPackage)
      .zeroOrOne(WorkPackageReference)
      .zeroOrOne(WorkflowDefinitionReference)
      .zeroOrOne(WorkflowNotes));

   ArtifactTypeToken TeamWorkflow = ats.add(ats.artifactType(73L, "Team Workflow", false, AbstractAccessControlled, AbstractWorkflowArtifact)
      .any(ActionableItemReference)
      .zeroOrOne(ApplicabilityWorkflow)
      .zeroOrOne(ApplicableToProgram, ApplicableToProgram.No)
      .zeroOrOne(ApproveRequestedHoursBy)
      .zeroOrOne(ApproveRequestedHoursByDate)
      .zeroOrOne(BranchMetrics)
      .zeroOrOne(ChangeType)
      .any(CommitOverride)
      .zeroOrOne(CrashOrBlankDisplay)
      .zeroOrOne(DuplicatedPcrId)
      .zeroOrOne(EstimateAssumptions)
      .any(FeatureImpactReference)
      .zeroOrOne(FunctionalArea)
      .zeroOrOne(GitRepoName)
      .zeroOrOne(GitBranchName)
      .zeroOrOne(HowFound)
      .zeroOrOne(HowToReproduceProblem)
      .zeroOrOne(LegacyPcrId)
      .zeroOrOne(ImpactToMissionOrCrew)
      .zeroOrOne(NeedBy)
      .zeroOrOne(NonFunctionalProblem)
      .zeroOrOne(OperationalImpact)
      .zeroOrOne(OperationalImpactDescription)
      .zeroOrOne(OperationalImpactWorkaround)
      .zeroOrOne(OperationalImpactWorkaroundDescription)
      .zeroOrOne(OriginatingPcrId)
      .any(PcrId)
      .zeroOrOne(PercentRework)
      .zeroOrOne(PointsAttributeType)
      .zeroOrOne(Priority)
      .zeroOrOne(Problem)
      .zeroOrOne(ProductLineApprovedBy)
      .zeroOrOne(ProductLineApprovedDate)
      .zeroOrOne(ProgramId)
      .zeroOrOne(ProposedResolution)
      .zeroOrOne(ProposedResolutionDate)
      .zeroOrOne(ProblemFirstObserved)
      .zeroOrOne(Rationale)
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld)
      .any(RelatedTaskWorkflowDefinitionReference)
      .zeroOrOne(RevisitDate)
      .zeroOrOne(RiskAnalysis)
      .zeroOrOne(RootCause)
      .zeroOrOne(SignedOffBy)
      .zeroOrOne(SignedOffByDate)
      .zeroOrOne(TeamDefinitionReference)
      .zeroOrOne(ValidationRequired)
      .zeroOrOne(WeeklyBenefit)
      .zeroOrOne(Workaround)
      .any(GitChangeId)
      .zeroOrOne(ValidateChangesRanBy)
      .zeroOrOne(ValidateChangesRanDate)
      .zeroOrOne(ManagerSignedOffByDate)
      .zeroOrOne(ManagerSignedOffBy)
      .zeroOrOne(ActivityId)
   );

   ArtifactTypeToken Goal = ats.add(ats.artifactType(72L, "Goal", false, GOAL, AbstractWorkflowArtifact)
      .zeroOrOne(ChangeType)
      .zeroOrOne(NeedBy)
      .zeroOrOne(WorldResultsJson)
      .zeroOrOne(WorldResultsCustId)
      .zeroOrOne(Priority));

   ArtifactTypeToken Task = ats.add(ats.artifactType(74L, "Task", false, TASK, AbstractWorkflowArtifact)
      .zeroOrOne(RelatedToState)
      .zeroOrOne(RiskFactor)
      .zeroOrOne(SignalImpact)
      .zeroOrOne(TaskToChangedArtifactReference)
      .zeroOrOne(TaskToChangedArtifactName)
      .zeroOrOne(TaskToChangedArtifactDeleted)
      .zeroOrOne(TaskAutoGen)
      .zeroOrOne(TaskAutoGenVersion)
      .any(TaskAutoGenType)
      .zeroOrOne(WcafeImpact)
      .zeroOrOne(UsesResolutionOptions));

   // Change Request
   ArtifactTypeToken AbstractChangeRequestWorkflow = osee.add(osee.artifactType(458278L, "Abstract Change Request", true, Artifact));
   ArtifactTypeToken ChangeRequestTeamWorkflow = ats.add(ats.artifactType(4938L, "Change Request Workflow", false, CHANGE_REQUEST, TeamWorkflow, AbstractChangeRequestWorkflow));

   // Reviews
   ArtifactTypeToken AbstractReview = ats.add(ats.artifactType(64L, "ats.Review", true, AbstractWorkflowArtifact)
      .any(ActionableItemReference)
      .zeroOrOne(NeedBy)
      .zeroOrOne(RelatedToState)
      .zeroOrOne(ReviewBlocks, ReviewBlocks.None));

   ArtifactTypeToken DecisionReview = ats.add(ats.artifactType(66L, "Decision Review", false, DECISION_REVIEW, AbstractReview)
      .zeroOrOne(Decision)
      .zeroOrOne(DecisionReviewOptions));

   ArtifactTypeToken PeerToPeerReview = ats.add(ats.artifactType(65L, "Peer-To-Peer Review", false, PEER_REVIEW, AbstractReview)
      .zeroOrOne(ChangeType, null)
      .zeroOrOne(LocChanged)
      .zeroOrOne(LocReviewed)
      .zeroOrOne(Location)
      .any(MeetingAttendeeUserId)
      .any(MeetingAttendeeId)
      .zeroOrOne(MeetingDate)
      .zeroOrOne(MeetingLength)
      .zeroOrOne(MeetingLocation)
      .zeroOrOne(PagesChanged)
      .zeroOrOne(PagesReviewed)
      .zeroOrOne(QuantityUnderReview)
      .any(ReviewDefect)
      .zeroOrOne(ReviewFormalType, ReviewFormalType.Formal)
      .any(Role)
      .zeroOrOne(VerificationCodeInspection));


   // ATS Agile
   ArtifactTypeToken AgileFeatureGroup = ats.add(ats.artifactType(560322181883393633L, "Agile Feature Group", false, new MaterialIcon("group_work"), AtsConfigArtifact)
      .zeroOrOne(Description));

   ArtifactTypeToken AgileProgram = ats.add(ats.artifactType(7844993694062372L, "Agile Program", false, new MaterialIcon("code"), AtsConfigArtifact));

   ArtifactTypeToken AgileProgramBacklog = ats.add(ats.artifactType(7844994687943135L, "Agile Program Backlog", false, new MaterialIcon("list_alt"), AtsConfigArtifact));

   ArtifactTypeToken AgileProgramBacklogItem = ats.add(ats.artifactType(11221316461321645L, "Agile Program Backlog Item", false, new MaterialIcon("bookmark_border"), AtsConfigArtifact));

   ArtifactTypeToken AgileProgramFeature = ats.add(ats.artifactType(99876313545914L, "Agile Program Feature", false, new MaterialIcon("stars"), AtsConfigArtifact));

   ArtifactTypeToken AgileStory = ats.add(ats.artifactType(33216462134454L, "Agile Story", false, new MaterialIcon("history_edu"), AtsConfigArtifact));

   ArtifactTypeToken AgileTeam = ats.add(ats.artifactType(7553778770333667393L, "Agile Team", false, new MaterialIcon("groups"), AtsConfigArtifact)
      .zeroOrOne(Description)
      .zeroOrOne(KanbanIgnoreStates)
      .zeroOrOne(PointsAttributeType));

   ArtifactTypeToken AgileSprint = ats.add(ats.artifactType(9088615648290692675L, "Agile Sprint", false, new MaterialIcon("loop"), AbstractWorkflowArtifact)
      .any(Holiday)
      .any(KanbanStoryName)
      .zeroOrOne(PlannedPoints)
      .zeroOrOne(UnplannedPoints));

   ArtifactTypeToken AgileBacklog = ats.add(ats.artifactType(7553335770333667393L, "Agile Backlog", false, new MaterialIcon("list"), Goal));

   // Demo Db Only
   ArtifactTypeToken DemoChangeRequestTeamWorkflow = atsDemo.add(atsDemo.artifactType(3456L, "Demo Change Request", false,
      CHANGE_REQUEST, AtsArtifactTypes.ChangeRequestTeamWorkflow));
   ArtifactTypeToken DemoCodeTeamWorkflow = atsDemo.add(atsDemo.artifactType(79L, "Demo Code Team Workflow", false, TeamWorkflow));
   ArtifactTypeToken DemoReqTeamWorkflow = atsDemo.add(atsDemo.artifactType(80L, "Demo Req Team Workflow", false, TeamWorkflow));
   ArtifactTypeToken DemoTestTeamWorkflow = atsDemo.add(atsDemo.artifactType(81L, "Demo Test Team Workflow", false, TeamWorkflow));

   // @formatter:on
}
