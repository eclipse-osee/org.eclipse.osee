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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_COMPLETION;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamTransitionManagerTestWidgetRequiredCompletion extends AbstractWorkDef {

   public WorkDefTeamTransitionManagerTestWidgetRequiredCompletion() {
      super(DemoWorkDefinitionTokens.WorkDef_Team_TransitionManagerTest_WidgetRequiredCompletion);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam", REQUIRED_FOR_COMPLETION), //
               new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam", REQUIRED_FOR_COMPLETION) //
            ));

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam", REQUIRED_FOR_COMPLETION), //
               new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam", REQUIRED_FOR_COMPLETION) //
            ));

      bld.andState(3, "Completed", StateType.Completed) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
