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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCommitManagerWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.xx.PointsNumericWidgetDefinition;
import org.eclipse.osee.ats.core.workdef.builder.DecisionReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.PeerReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoSwDesign extends AbstractWorkDef {

   public WorkDefTeamDemoSwDesign() {
      super(DemoWorkDefinitions.WorkDefTeamDemoSwDesign);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andCustomHeader() //
         .isShowSiblingLinks(false) //
         .showMetricsHeader(false); //

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
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(ValidationRequired), //
            new AttrWidgetDef(WorkPackage), //
            new PointsNumericWidgetDefinition("Estimated Points") //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(analyzeTransitionToDecRev) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(Problem), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(EstimatedHours));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andPeerReviewBuilder(authorizeTransitionTo) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(implementCreateBranch) //
         .andPeerReviewBuilder(implementCommitBranch) //
         .andLayout( //
            new WidgetDef(XXWorkingBranchWfeWidget), //
            new WidgetDef(XCommitManagerWfeWidget), //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate), //
            new AttrWidgetDef(Resolution));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
