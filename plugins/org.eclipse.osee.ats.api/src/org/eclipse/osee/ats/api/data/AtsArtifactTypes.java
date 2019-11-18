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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractAccessControlled;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Notes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public interface AtsArtifactTypes {

   // @formatter:off
   ArtifactTypeToken AtsArtifact = ats.add(ats.artifactType(63L, "ats.Ats Artifact", true, Artifact)
      .zeroOrOne(Description, "")
      .zeroOrOne(GoalOrderVote, ""));
   ArtifactTypeToken Action = ats.add(ats.artifactType(67L, "Action", false, AtsArtifact)
      .any(AtsAttributeTypes.ActionableItem, "")
      .any(ActionableItemReference, "")
      .exactlyOne(AtsId, "0")
      .zeroOrOne(ChangeType, "", 3458764513820541326L)
      .zeroOrOne(NeedBy, "")
      .zeroOrOne(Priority, "", 3458764513820541325L)
      .zeroOrOne(ValidationRequired, "false"));
   ArtifactTypeToken AtsConfigArtifact = ats.add(ats.artifactType(801L, "ats.Ats Config Artifact", true, Artifact)
      .exactlyOne(Active, "true"));
   ArtifactTypeToken AgileFeatureGroup = ats.add(ats.artifactType(560322181883393633L, "Agile Feature Group", false, AtsConfigArtifact)
      .zeroOrOne(Description, ""));
   ArtifactTypeToken AgileProgram = ats.add(ats.artifactType(7844993694062372L, "Agile Program", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramBacklog = ats.add(ats.artifactType(7844994687943135L, "Agile Program Backlog", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramBacklogItem = ats.add(ats.artifactType(11221316461321645L, "Agile Program Backlog Item", false, AtsConfigArtifact));
   ArtifactTypeToken AgileProgramFeature = ats.add(ats.artifactType(99876313545914L, "Agile Program Feature", false, AtsConfigArtifact));
   ArtifactTypeToken AgileStory = ats.add(ats.artifactType(33216462134454L, "Agile Story", false, AtsConfigArtifact));
   ArtifactTypeToken AgileTeam = ats.add(ats.artifactType(7553778770333667393L, "Agile Team", false, AtsConfigArtifact)
      .zeroOrOne(Description, "")
      .zeroOrOne(KanbanIgnoreStates, "")
      .zeroOrOne(PointsAttributeType, ""));
   ArtifactTypeToken AtsTeamDefinitionOrAi = ats.add(ats.artifactType(803L, "ats.Ats Team Definition or AI", true, AtsConfigArtifact)
      .zeroOrOne(Actionable, "true")
      .any(AtsAttributeTypes.RuleDefinition, ""));
   ArtifactTypeToken ActionableItem = ats.add(ats.artifactType(69L, "Actionable Item", false, AbstractAccessControlled, AtsTeamDefinitionOrAi)
      .zeroOrOne(AllowUserActionCreation, "")
      .any(CSCI, "")
      .zeroOrOne(ProgramId, "")
      .zeroOrOne(WorkType, ""));
   ArtifactTypeToken AtsUser = ats.add(ats.artifactType(58889929L, "User", false, CoreArtifactTypes.User)
      .any(AtsQuickSearch, "")
      .any(AtsUserConfig, ""));
   ArtifactTypeToken Configuration = ats.add(ats.artifactType(93802085744703L, "Configuration", false, Artifact)
      .zeroOrOne(AtsConfiguredBranch, "")
      .zeroOrOne(Default, "")
      .zeroOrOne(Description, ""));
   ArtifactTypeToken Country = ats.add(ats.artifactType(4955822638391722788L, "Country", false, AtsConfigArtifact)
      .zeroOrOne(Description, ""));
   ArtifactTypeToken Insertion = ats.add(ats.artifactType(1735587136604728792L, "Insertion", false, AtsConfigArtifact)
      .zeroOrOne(ColorTeam, "Unspecified", 5000740273963153015L)
      .zeroOrOne(Description, "")
      .zeroOrOne(EndDate, "")
      .zeroOrOne(PointsNumeric, "0.0")
      .zeroOrOne(StartDate, ""));
   ArtifactTypeToken InsertionActivity = ats.add(ats.artifactType(3943415539127781884L, "Insertion Activity", false, AtsConfigArtifact)
      .zeroOrOne(Description, ""));
   ArtifactTypeToken Program = ats.add(ats.artifactType(52374361342017540L, "Program", false, AtsConfigArtifact)
      .any(CSCI, "")
      .zeroOrOne(ClosureState, "", 3458764513820541340L)
      .zeroOrOne(Description, "")
      .zeroOrOne(Namespace, "")
      .zeroOrOne(AtsAttributeTypes.TeamDefinition, "")
      .zeroOrOne(TeamDefinitionReference, ""));
   ArtifactTypeToken ResponsibleTeam = ats.add(ats.artifactType(8943243743202487405L, "Responsible Team", false, AtsTeamDefinitionOrAi));
   ArtifactTypeToken RuleDefinition = ats.add(ats.artifactType(6370402109038303278L, "Rule Definition", false, Artifact)
      .zeroOrOne(DslSheet, ""));
   ArtifactTypeToken AbstractWorkflowArtifact = ats.add(ats.artifactType(71L, "ats.State Machine", true, AtsArtifact)
      .exactlyOne(AtsId, "0")
      .zeroOrOne(BlockedReason, "")
      .zeroOrOne(CancelReason, "", 571876272454604057L)
      .zeroOrOne(CancelledBy, "")
      .zeroOrOne(CancelledDate, "")
      .zeroOrOne(CancelledFromState, "")
      .zeroOrOne(CancelledReason, "")
      .zeroOrOne(CancelledReasonDetails, "")
      .zeroOrOne(Category1, "")
      .zeroOrOne(Category2, "")
      .zeroOrOne(Category3, "")
      .zeroOrOne(CompletedBy, "")
      .zeroOrOne(CompletedDate, "")
      .zeroOrOne(CompletedFromState, "")
      .zeroOrOne(CreatedBy, "")
      .zeroOrOne(CreatedDate, "")
      .zeroOrOne(CurrentState, "")
      .zeroOrOne(CurrentStateType, "")
      .zeroOrOne(EndDate, "")
      .zeroOrOne(EstimatedCompletionDate, "")
      .zeroOrOne(EstimatedHours, "0.0")
      .zeroOrOne(EstimatedReleaseDate, "")
      .zeroOrOne(Log, "")
      .zeroOrOne(Numeric1, "0.0")
      .zeroOrOne(Numeric2, "0.0")
      .zeroOrOne(PeerReviewId, "")
      .zeroOrOne(PercentComplete, "0")
      .zeroOrOne(Points, "", 3458764513820541324L)
      .zeroOrOne(PointsNumeric, "0.0")
      .zeroOrOne(ReleaseDate, "")
      .zeroOrOne(Resolution, "")
      .zeroOrOne(StartDate, "")
      .any(State, "")
      .zeroOrOne(StateNotes, "")
      .zeroOrOne(UnplannedWork, "")
      .zeroOrOne(AtsAttributeTypes.WorkPackage, "")
      .zeroOrOne(WorkPackageGuid, "")
      .zeroOrOne(WorkPackageReference, "")
      .zeroOrOne(WorkflowDefinition, "")
      .zeroOrOne(WorkflowDefinitionReference, "")
      .zeroOrOne(WorkflowNotes, ""));
   ArtifactTypeToken AgileSprint = ats.add(ats.artifactType(9088615648290692675L, "Agile Sprint", false, AbstractWorkflowArtifact)
      .any(Holiday, "")
      .any(KanbanStoryName, "")
      .zeroOrOne(PlannedPoints, "")
      .zeroOrOne(UnplannedPoints, ""));
   ArtifactTypeToken Goal = ats.add(ats.artifactType(72L, "Goal", false, AbstractWorkflowArtifact)
      .zeroOrOne(ChangeType, "", 3458764513820541326L)
      .zeroOrOne(NeedBy, "")
      .zeroOrOne(Priority, "", 3458764513820541325L));
   ArtifactTypeToken AgileBacklog = ats.add(ats.artifactType(7553335770333667393L, "Agile Backlog", false, Goal));
   ArtifactTypeToken AbstractReview = ats.add(ats.artifactType(64L, "ats.Review", true, AbstractWorkflowArtifact)
      .any(AtsAttributeTypes.ActionableItem, "")
      .any(ActionableItemReference, "")
      .zeroOrOne(RelatedToState, "")
      .zeroOrOne(ReviewBlocks, "", 3458764513820541322L));
   ArtifactTypeToken DecisionReview = ats.add(ats.artifactType(66L, "Decision Review", false, AbstractReview)
      .zeroOrOne(Decision, "")
      .zeroOrOne(DecisionReviewOptions, ""));
   ArtifactTypeToken PeerToPeerReview = ats.add(ats.artifactType(65L, "PeerToPeer Review", false, AbstractReview)
      .any(CSCI, "")
      .zeroOrOne(ChangeType, "", 3458764513820541326L)
      .zeroOrOne(LocChanged, "")
      .zeroOrOne(LocReviewed, "")
      .zeroOrOne(Location, "")
      .any(MeetingAttendee, "")
      .zeroOrOne(MeetingDate, "")
      .zeroOrOne(MeetingLength, "0.0")
      .zeroOrOne(MeetingLocation, "")
      .zeroOrOne(PagesChanged, "")
      .zeroOrOne(PagesReviewed, "")
      .any(ReviewDefect, "")
      .zeroOrOne(ReviewFormalType, "", 3458764513820541323L)
      .any(Role, "")
      .zeroOrOne(VerificationCodeInspection, ""));
   ArtifactTypeToken ReleaseArtifact = ats.add(ats.artifactType(61L, "ats.Release Artifact", true, Artifact)
      .zeroOrOne(Released, "false"));
   ArtifactTypeToken Task = ats.add(ats.artifactType(74L, "Task", false, AbstractWorkflowArtifact)
      .zeroOrOne(RelatedToState, "")
      .zeroOrOne(TaskToChangedArtifactReference, "")
      .zeroOrOne(UsesResolutionOptions, "false"));
   ArtifactTypeToken TeamDefinition = ats.add(ats.artifactType(68L, "Team Definition", false, AbstractAccessControlled, ResponsibleTeam)
      .zeroOrOne(ActionDetailsFormat, "")
      .zeroOrOne(AllowCommitBranch, "true")
      .zeroOrOne(AllowCreateBranch, "true")
      .zeroOrOne(AtsIdPrefix, "")
      .zeroOrOne(AtsIdSequenceName, "")
      .zeroOrOne(BaselineBranchGuid, "")
      .zeroOrOne(BaselineBranchId, "")
      .any(CSCI, "")
      .zeroOrOne(ClosureActive, "")
      .zeroOrOne(FullName, "")
      .zeroOrOne(HoursPerWorkDay, "0.0")
      .zeroOrOne(ProgramId, "")
      .any(RelatedPeerWorkflowDefinition, "")
      .any(RelatedPeerWorkflowDefinitionReference, "")
      .any(RelatedTaskWorkflowDefinition, "")
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld, "")
      .any(RelatedTaskWorkflowDefinitionReference, "")
      .zeroOrOne(RequireTargetedVersion, "false")
      .any(TaskSetId, "")
      .zeroOrOne(TeamUsesVersions, "false")
      .zeroOrOne(TeamWorkflowArtifactType, "")
      .zeroOrOne(WorkType, "")
      .zeroOrOne(WorkflowDefinition, "")
      .zeroOrOne(WorkflowDefinitionReference, ""));
   ArtifactTypeToken TeamWorkflow = ats.add(ats.artifactType(73L, "Team Workflow", false, AbstractAccessControlled, AbstractWorkflowArtifact)
      .any(AtsAttributeTypes.ActionableItem, "")
      .any(ActionableItemReference, "")
      .zeroOrOne(ApplicabilityWorkflow, "")
      .zeroOrOne(ApplicableToProgram, "", 1152921949229285546L)
      .zeroOrOne(ApproveRequestedHoursBy, "")
      .zeroOrOne(ApproveRequestedHoursDate, "")
      .zeroOrOne(BaselineBranchGuid, "")
      .zeroOrOne(BaselineBranchId, "")
      .zeroOrOne(BranchMetrics, "")
      .zeroOrOne(ChangeType, "", 3458764513820541326L)
      .zeroOrOne(ColorTeam, "Unspecified", 5000740273963153015L)
      .any(CommitOverride, "")
      .zeroOrOne(DuplicatedPcrId, "")
      .zeroOrOne(EstimateAssumptions, "")
      .zeroOrOne(IPT, "", 2695446918879429118L)
      .zeroOrOne(LegacyPcrId, "")
      .zeroOrOne(NeedBy, "")
      .zeroOrOne(OperationalImpact, "")
      .zeroOrOne(OperationalImpactDescription, "")
      .zeroOrOne(OperationalImpactWorkaround, "")
      .zeroOrOne(OperationalImpactWorkaroundDescription, "")
      .zeroOrOne(OriginatingPcrId, "")
      .zeroOrOne(PcrToolId, "")
      .zeroOrOne(PercentRework, "")
      .zeroOrOne(Priority, "", 3458764513820541325L)
      .zeroOrOne(Problem, "")
      .zeroOrOne(ProgramId, "")
      .zeroOrOne(ProposedResolution, "")
      .zeroOrOne(Rationale, "")
      .any(RelatedTaskWorkflowDefinition, "")
      .zeroOrOne(RelatedTaskWorkflowDefinitionOld, "")
      .any(RelatedTaskWorkflowDefinitionReference, "")
      .zeroOrOne(AtsAttributeTypes.TeamDefinition, "")
      .zeroOrOne(TeamDefinitionReference, "")
      .zeroOrOne(ValidationRequired, "false")
      .zeroOrOne(WeeklyBenefit, "0"));
   ArtifactTypeToken Version = ats.add(ats.artifactType(70L, "Version", false, AtsArtifact)
      .zeroOrOne(AllowCommitBranch, "true")
      .zeroOrOne(AllowCreateBranch, "true")
      .zeroOrOne(AllowWebExport, "false")
      .zeroOrOne(BaselineBranchGuid, "")
      .zeroOrOne(BaselineBranchId, "")
      .zeroOrOne(ClosureState, "", 3458764513820541340L)
      .zeroOrOne(EstimatedReleaseDate, "")
      .zeroOrOne(FullName, "")
      .zeroOrOne(NextVersion, "false")
      .zeroOrOne(ReleaseDate, "")
      .zeroOrOne(Released, "false")
      .zeroOrOne(TestRunToSourceLocator, "")
      .zeroOrOne(VersionLocked, "false"));
   ArtifactTypeToken WorkDefinition = ats.add(ats.artifactType(62L, "Work Definition", false, Artifact)
      .zeroOrOne(DslSheet, ""));
   ArtifactTypeToken WorkPackage = ats.add(ats.artifactType(802L, "Work Package", false, Artifact)
      .exactlyOne(Active, "true")
      .zeroOrOne(ActivityId, "")
      .zeroOrOne(ActivityName, "")
      .zeroOrOne(CAM, "")
      .zeroOrOne(CognosUniqueId, "")
      .zeroOrOne(ColorTeam, "Unspecified", 5000740273963153015L)
      .zeroOrOne(ControlAccount, "")
      .zeroOrOne(Description, "")
      .zeroOrOne(EndDate, "")
      .zeroOrOne(EstimatedHours, "0.0")
      .zeroOrOne(IPT, "", 2695446918879429118L)
      .any(Notes, "")
      .zeroOrOne(PercentComplete, "0")
      .zeroOrOne(PointsNumeric, "0.0")
      .zeroOrOne(StartDate, "")
      .zeroOrOne(WorkPackageId, "")
      .zeroOrOne(WorkPackageProgram, "")
      .zeroOrOne(WorkPackageType, "", 3458764513820541333L));
   // @formatter:on
}