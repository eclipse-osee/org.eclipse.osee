/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.DecisionReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.PeerReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

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
         .andToDefaultState(StateToken.Analyze) //
         .andToStates(StateToken.Cancelled, StateToken.Analyze) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", REQUIRED_FOR_TRANSITION, FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, "XComboBooleanDam"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToDefaultState(StateToken.Authorize) //
         .andToStates(StateToken.Cancelled, StateToken.Authorize, StateToken.Endorse) //
         .andOverrideValidationStates(StateToken.Endorse) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(analyzeTransitionToDecRev) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToDefaultState(StateToken.Implement) //
         .andToStates(StateToken.Cancelled, StateToken.Implement, StateToken.Analyze) //
         .andOverrideValidationStates(StateToken.Analyze) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andPeerReviewBuilder(authorizeTransitionTo) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"));

      bld.andState(4, "Implement", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Cancelled, StateToken.Completed, StateToken.Analyze, StateToken.Authorize) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Authorize) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(implementCreateBranch) //
         .andPeerReviewBuilder(implementCommitBranch) //
         .andLayout( //
            getWorkingBranchWidgetComposite(), new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(5, "Completed", StateType.Completed) //
         .andToStates(StateToken.Implement) //
         .andOverrideValidationStates(StateToken.Implement) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Analyze, StateToken.Endorse, StateToken.Authorize, StateToken.Implement) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Endorse, StateToken.Authorize,
            StateToken.Implement) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
