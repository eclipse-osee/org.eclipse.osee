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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractAccessControlled;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Notes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface AtsArtifactTypes {

   // @formatter:off
   ArtifactTypeToken AtsArtifact = ats.add(ats.artifactType(63L, "ats.Ats Artifact", true, Artifact)
      .zeroOrOne(Description)
      .zeroOrOne(GoalOrderVote));
   ArtifactTypeToken Action = ats.add(ats.artifactType(67L, "Action", false, AtsArtifact)
      .any(AtsAttributeTypes.ActionableItem)
      .any(ActionableItemReference)
      .exactlyOne(AtsId, "0")
      .zeroOrOne(ChangeType, null)
      .zeroOrOne(NeedBy)
      .zeroOrOne(Priority, null)
      .zeroOrOne(ValidationRequired));
   ArtifactTypeToken AtsConfigArtifact = ats.add(ats.artifactType(801L, "ats.Ats Config Artifact", true, Artifact)
      .exactlyOne(Active, Boolean.TRUE));
   ArtifactTypeToken AgileFeatureGroup = ats.add(ats.artifactType(560322181883393633L, "Agile Feature Group", false, AtsConfigArtifact)
      .zeroOrOne(Description));
   ArtifactTypeToken AgileProgram = ats.add(ats.artifactType(7844993694062372L, "Agile Program", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramBacklog = ats.add(ats.artifactType(7844994687943135L, "Agile Program Backlog", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramBacklogItem = ats.add(ats.artifactType(11221316461321645L, "Agile Program Backlog Item", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramFeature = ats.add(ats.artifactType(99876313545914L, "Agile Program Feature", false, AtsConfigArtifact));
   ArtifactTypeToken AgileStory = ats.add(ats.artifactType(33216462134454L, "Agile Story", false, AtsConfigArtifact));
   ArtifactTypeToken AgileTeam = ats.add(ats.artifactType(7553778770333667393L, "Agile Team", false, AtsConfigArtifact)
      .zeroOrOne(Description)
      .zeroOrOne(KanbanIgnoreStates)
      .zeroOrOne(PointsAttributeType));
   ArtifactTypeToken AtsTeamDefinitionOrAi = ats.add(ats.artifactType(803L, "ats.Ats Team Definition or AI", true, AtsConfigArtifact)
      .zeroOrOne(Actionable, Boolean.TRUE)
      .any(AtsAttributeTypes.RuleDefinition));
   ArtifactTypeToken ActionableItem = ats.add(ats.artifactType(69L, "Actionable Item", false, AbstractAccessControlled, AtsTeamDefinitionOrAi)
      .zeroOrOne(AllowUserActionCreation)
      .any(CSCI)
      .zeroOrOne(ProgramId)
      .any(WorkType));
   ArtifactTypeToken Configuration = ats.add(ats.artifactType(93802085744703L, "Configuration", false, Artifact)
      .zeroOrOne(AtsConfiguredBranch)
      .zeroOrOne(Default)
      .zeroOrOne(Description));
   ArtifactTypeToken Country = ats.add(ats.artifactType(4955822638391722788L, "Country", false, AtsConfigArtifact));
   ArtifactTypeToken Insertion = ats.add(ats.artifactType(1735587136604728792L, "Insertion", false, AtsConfigArtifact)
      .zeroOrOne(Description)
      .zeroOrOne(EndDate)
      .zeroOrOne(PointsNumeric)
      .zeroOrOne(StartDate));
   ArtifactTypeToken InsertionActivity = ats.add(ats.artifactType(3943415539127781884L, "Insertion Activity", false, AtsConfigArtifact)
      .zeroOrOne(Description));
   ArtifactTypeToken Program = ats.add(ats.artifactType(52374361342017540L, "Program", false, AtsConfigArtifact)
      .any(CSCI)
      .zeroOrOne(ClosureState, null)
      .zeroOrOne(Description)
      .zeroOrOne(Namespace)
      .zeroOrOne(AtsAttributeTypes.TeamDefinition)
      .zeroOrOne(TeamDefinitionReference));
   ArtifactTypeToken ResponsibleTeam = ats.add(ats.artifactType(8943243743202487405L, "Responsible Team", false, AtsTeamDefinitionOrAi));
   ArtifactTypeToken RuleDefinition = ats.add(ats.artifactType(6370402109038303278L, "Rule Definition", false, Artifact)
      .zeroOrOne(DslSheet));
   ArtifactTypeToken AbstractWorkflowArtifact = ats.add(ats.artifactType(71L, "ats.State Machine", true, AtsArtifact)
      .exactlyOne(AtsId, "0")
      .zeroOrOne(BlockedReason)
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
      .zeroOrOne(CompletedBy)
      .zeroOrOne(CompletedDate)
      .zeroOrOne(CompletedFromState)
      .zeroOrOne(CreatedBy)
      .zeroOrOne(CreatedDate)
      .zeroOrOne(CurrentState)
      .zeroOrOne(CurrentStateType)
      .zeroOrOne(EndDate)
      .zeroOrOne(EstimatedCompletionDate)
      .zeroOrOne(EstimatedHours)
      .zeroOrOne(EstimatedReleaseDate)
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
      .zeroOrOne(StateNotes)
      .zeroOrOne(UnplannedWork)
      .zeroOrOne(AtsAttributeTypes.WorkPackage)
      .zeroOrOne(WorkPackageGuid)
      .zeroOrOne(WorkPackageReference)
      .zeroOrOne(WorkflowDefinition)
      .zeroOrOne(WorkflowDefinitionReference)
      .zeroOrOne(WorkflowNotes));
   ArtifactTypeToken AgileSprint = ats.add(ats.artifactType(9088615648290692675L, "Agile Sprint", false, AbstractWorkflowArtifact)
      .any(Holiday)
      .any(KanbanStoryName)
      .zeroOrOne(PlannedPoints)
      .zeroOrOne(UnplannedPoints));
   ArtifactTypeToken Goal = ats.add(ats.artifactType(72L, "Goal", false, AbstractWorkflowArtifact)
      .zeroOrOne(ChangeType, ChangeType.Improvement)
      .zeroOrOne(NeedBy)
      .zeroOrOne(Priority, Priority.Priority5));
   ArtifactTypeToken AgileBacklog = ats.add(ats.artifactType(7553335770333667393L, "Agile Backlog", false, Goal));
   ArtifactTypeToken AbstractReview = ats.add(ats.artifactType(64L, "ats.Review", true, AbstractWorkflowArtifact)
      .any(AtsAttributeTypes.ActionableItem)
      .any(ActionableItemReference)
      .zeroOrOne(RelatedToState)
      .zeroOrOne(ReviewBlocks, ReviewBlocks.None));
   ArtifactTypeToken DecisionReview = ats.add(ats.artifactType(66L, "Decision Review", false, AbstractReview)
      .zeroOrOne(Decision)
      .zeroOrOne(DecisionReviewOptions));
   ArtifactTypeToken PeerToPeerReview = ats.add(ats.artifactType(65L, "Peer-To-Peer Review", false, AbstractReview)
      .zeroOrOne(ChangeType, null)
      .zeroOrOne(LocChanged)
      .zeroOrOne(LocReviewed)
      .zeroOrOne(Location)
      .any(MeetingAttendee)
      .zeroOrOne(MeetingDate)
      .zeroOrOne(MeetingLength)
      .zeroOrOne(MeetingLocation)
      .zeroOrOne(PagesChanged)
      .zeroOrOne(PagesReviewed)
      .any(ReviewDefect)
      .zeroOrOne(ReviewFormalType, ReviewFormalType.Formal)
      .any(Role)
      .zeroOrOne(VerificationCodeInspection));
   ArtifactTypeToken ReleaseArtifact = ats.add(ats.artifactType(61L, "ats.Release Artifact", true, Artifact)
      .zeroOrOne(Released));
   ArtifactTypeToken Task = ats.add(ats.artifactType(74L, "Task", false, AbstractWorkflowArtifact)
      .zeroOrOne(RelatedToState)
      .zeroOrOne(SignalImpact)
      .zeroOrOne(TaskToChangedArtifactReference)
      .zeroOrOne(TaskToChangedArtifactName)
      .zeroOrOne(TaskToChangedArtifactDeleted)
      .zeroOrOne(TaskAutoGen)
      .zeroOrOne(TaskAutoGenVersion)
      .zeroOrOne(WcafeImpact)
      .zeroOrOne(UsesResolutionOptions));
   ArtifactTypeToken TeamDefinition = ats.add(ats.artifactType(68L, "Team Definition", false, AbstractAccessControlled, ResponsibleTeam)
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
      .any(RelatedPeerWorkflowDefinition)
      .any(RelatedPeerWorkflowDefinitionReference)
      .any(RelatedTaskWorkflowDefinition)
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld)
      .any(RelatedTaskWorkflowDefinitionReference)
      .zeroOrOne(RequireTargetedVersion)
      .any(TaskSetId)
      .zeroOrOne(TeamUsesVersions)
      .zeroOrOne(TeamWorkflowArtifactType)
      .any(WorkType)
      .zeroOrOne(WorkflowDefinition)
      .zeroOrOne(WorkflowDefinitionReference));
   ArtifactTypeToken TeamWorkflow = ats.add(ats.artifactType(73L, "Team Workflow", false, AbstractAccessControlled, AbstractWorkflowArtifact)
      .any(AtsAttributeTypes.ActionableItem)
      .any(ActionableItemReference)
      .zeroOrOne(AgileChangeType, AgileChangeType.Improvement)
      .zeroOrOne(ApplicabilityWorkflow)
      .zeroOrOne(ApplicableToProgram, ApplicableToProgram.No)
      .zeroOrOne(ApproveRequestedHoursBy)
      .zeroOrOne(ApproveRequestedHoursDate)
      .zeroOrOne(BaselineBranchId)
      .zeroOrOne(BranchMetrics)
      .zeroOrOne(ChangeType, ChangeType.Improvement)
      .any(CommitOverride)
      .zeroOrOne(Condition)
      .zeroOrOne(DuplicatedPcrId)
      .zeroOrOne(EstimateAssumptions)
      .zeroOrOne(Information)
      .zeroOrOne(LegacyPcrId)
      .zeroOrOne(NeedBy)
      .zeroOrOne(OperationalImpact)
      .zeroOrOne(OperationalImpactDescription)
      .zeroOrOne(OperationalImpactWorkaround)
      .zeroOrOne(OperationalImpactWorkaroundDescription)
      .zeroOrOne(OriginatingPcrId)
      .zeroOrOne(PcrToolId)
      .zeroOrOne(PercentRework)
      .zeroOrOne(PointsAttributeType)
      .zeroOrOne(Priority, Priority.Priority5)
      .zeroOrOne(Problem)
      .zeroOrOne(ProductLineApprovedBy)
      .zeroOrOne(ProductLineApprovedDate)
      .zeroOrOne(ProgramId)
      .zeroOrOne(ProposedResolution)
      .zeroOrOne(Rank)
      .zeroOrOne(Rationale)
      .any(RelatedTaskWorkflowDefinition)
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld)
      .any(RelatedTaskWorkflowDefinitionReference)
      .zeroOrOne(AtsAttributeTypes.TeamDefinition)
      .zeroOrOne(TeamDefinitionReference)
      .zeroOrOne(ValidationRequired)
      .zeroOrOne(WeeklyBenefit)
      .any(GitChangeId));
   ArtifactTypeToken Version = ats.add(ats.artifactType(70L, "Version", false, AtsArtifact)
      .zeroOrOne(AllowCommitBranch, Boolean.TRUE)
      .zeroOrOne(AllowCreateBranch, Boolean.TRUE)
      .zeroOrOne(AllowWebExport)
      .zeroOrOne(BaselineBranchId)
      .zeroOrOne(ClosureState, null)
      .zeroOrOne(EstimatedReleaseDate)
      .zeroOrOne(FullName)
      .zeroOrOne(NextVersion)
      .zeroOrOne(ReleaseDate)
      .zeroOrOne(Released)
      .zeroOrOne(TestRunToSourceLocator)
      .zeroOrOne(VersionLocked)
      .zeroOrOne(SignalDbSystemId)
      .zeroOrOne(IsDcs)
      .zeroOrOne(LegacyBuildId));
   ArtifactTypeToken WorkDefinition = ats.add(ats.artifactType(62L, "Work Definition", false, Artifact)
      .zeroOrOne(DslSheet));
   ArtifactTypeToken WorkPackage = ats.add(ats.artifactType(802L, "Work Package", false, Artifact)
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

   //iCTeam Types
   ArtifactTypeToken Project = ats.add(ats.artifactType(250L, "Project", false, Artifact)
      .zeroOrOne(BaselineBranchGuid)
      .zeroOrOne(Shortname)
      .zeroOrOne(TaskCountForProject));
   ArtifactTypeToken AgileProject = ats.add(ats.artifactType(8517L, "Agile Project", false, Project));


   // @formatter:on
}