/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.DecisionReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.PeerReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDemoSwDesign extends AbstractWorkDef {

   public WorkDefTeamDemoSwDesign() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andHeader() //
         .andLayout(getChangeTypeComposite()) //
         .isShowWorkPackageHeader(true) //
         .isShowMetricsHeader(false); //

      bld.andHeader() //
         .isShowWorkPackageHeader(false) //
         .isShowSiblingLinks(false) //
         .isShowMetricsHeader(false); //

      DecisionReviewDefinitionBuilder analyzeTransitionToDecRev = bld.createDecisionReview("Analyze.None.TransitionTo") //
         .andTitle(
            "Auto-created Decision Review from ruleId: atsAddDecisionReview.test.addDecisionReview.Analyze.None.TransitionTo") //
         .andDescription("This is a rule created to test the Review rules.") //
         .andRelatedToState(StateToken.Analyze) //
         .andBlockingType(ReviewBlockType.Transition) //
         .andEvent(StateEventType.TransitionTo) //
         .andAssignees(AtsCoreUsers.UNASSIGNED_USER) //
         .andAutoTransitionToDecision() //
         .andOption("Completed").toCompleted().done();

      DecisionReviewDefinitionBuilder implementCreateBranch = bld.createDecisionReview("Implement.None.CreateBranch") //
         .andTitle(
            "Auto-created Decision Review from ruleId: atsAddDecisionReview.test.addDecisionReview.Implement.None.CreateBranch") //
         .andDescription("This is a rule created to test the Review rules.") //
         .andRelatedToState(StateToken.Implement) //
         .andBlockingType(ReviewBlockType.Transition) //
         .andEvent(StateEventType.CreateBranch) //
         .andAssignees(AtsCoreUsers.UNASSIGNED_USER) //
         .andOption("Completed").toCompleted().done();

      PeerReviewDefinitionBuilder authorizeTransitionTo = bld.createPeerReview("Authorize.None.TransitionTo") //
         .andTitle(
            "Auto-created Peer Review from ruleId atsAddPeerToPeerReview.test.addPeerToPeerReview.Authorize.None.TransitionTo") //
         .andDescription("This is a rule created to test the Review rules.") //
         .andRelatedToState(StateToken.Authorize) //
         .andBlockingType(ReviewBlockType.Transition) //
         .andEvent(StateEventType.TransitionTo) //
         .andAssignees(AtsCoreUsers.UNASSIGNED_USER);

      PeerReviewDefinitionBuilder implementCommitBranch = bld.createPeerReview("Implement.None.CommitBranch") //
         .andTitle(
            "Auto-created Peer Review from ruleId atsAddPeerToPeerReview.test.addPeerToPeerReview.Implement.None.CommitBranch") //
         .andDescription("This is a rule created to test the Review rules.") //
         .andRelatedToState(StateToken.Implement) //
         .andBlockingType(ReviewBlockType.Transition) //
         .andEvent(StateEventType.CommitBranch) //
         .andAssignees(AtsCoreUsers.UNASSIGNED_USER);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", REQUIRED_FOR_TRANSITION, FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, "XComboBooleanDam"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(analyzeTransitionToDecRev) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andPeerReviewBuilder(authorizeTransitionTo) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(implementCreateBranch) //
         .andPeerReviewBuilder(implementCommitBranch) //
         .andLayout( //
            getWorkingBranchWidgetComposite(), new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
