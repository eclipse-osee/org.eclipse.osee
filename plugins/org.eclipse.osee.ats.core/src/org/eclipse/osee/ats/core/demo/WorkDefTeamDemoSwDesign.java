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

package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
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
@SuppressWarnings("unused")
public class WorkDefTeamDemoSwDesign extends AbstractWorkDef {

   public WorkDefTeamDemoSwDesign() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andHeader() //
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
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, RFT, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, XComboBooleanArtWidget), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(analyzeTransitionToDecRev) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.Problem, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andPeerReviewBuilder(authorizeTransitionTo) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(implementCreateBranch) //
         .andPeerReviewBuilder(implementCommitBranch) //
         .andLayout( //
            getWorkingBranchWidgetComposite(), new WidgetDefinition("Commit Manager", XCommitManagerArtWidget), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
