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

package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.DecisionReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.enums.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDecisionReviewDefinitionManagerTestPrepare extends AbstractWorkDef {

   public WorkDefTeamDecisionReviewDefinitionManagerTestPrepare() {
      super(DemoWorkDefinitionTokens.WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      DecisionReviewDefinitionBuilder createNewOnImplement = bld.createDecisionReview("Create New on Implement") //
         .andTitle("This is the title") //
         .andDescription("the description") //
         .andRelatedToState(StateToken.Implement) //
         .andBlockingType(ReviewBlockType.Commit) //
         .andEvent(StateEventType.TransitionTo) //
         .andOption("Yes").toFollowup().andAssignees(DemoUsers.Joe_Smith).done() //
         .andOption("No").toCompleted().done();

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK);

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andDecisionReviewBuilder(createNewOnImplement);

      bld.andState(3, "Completed", StateType.Completed) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
