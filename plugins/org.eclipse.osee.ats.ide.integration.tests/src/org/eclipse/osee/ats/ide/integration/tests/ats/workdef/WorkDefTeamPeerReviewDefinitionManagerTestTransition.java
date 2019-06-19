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
package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.PeerReviewDefinitionBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamPeerReviewDefinitionManagerTestTransition extends AbstractWorkDef {

   public WorkDefTeamPeerReviewDefinitionManagerTestTransition() {
      super(DemoWorkDefinitionTokens.WorkDef_Team_PeerReviewDefinitionManagerTest_Transition);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      PeerReviewDefinitionBuilder createNewOnImplement = bld.createPeerReview("Create New on Implement") //
         .andTitle("This is my review title") //
         .andDescription("the description") //
         .andRelatedToState(StateToken.Implement) //
         .andBlockingType(ReviewBlockType.Transition) //
         .andEvent(StateEventType.TransitionTo) //
         .andAssignees(AtsCoreUsers.UNASSIGNED_USER);

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Implement) //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andOverrideValidationStates(StateToken.Cancelled) //
         .andColor(StateColor.BLACK);

      bld.andState(2, "Implement", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Analyze, StateToken.Completed, StateToken.Cancelled) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andPeerReviewBuilder(createNewOnImplement);

      bld.andState(3, "Completed", StateType.Completed) //
         .andToStates(StateToken.Implement) //
         .andOverrideValidationStates(StateToken.Implement) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Analyze, StateToken.Implement) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Implement) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
