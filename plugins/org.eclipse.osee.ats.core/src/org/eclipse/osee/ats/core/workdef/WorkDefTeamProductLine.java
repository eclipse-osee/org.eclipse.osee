/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamProductLine extends AbstractWorkDef {

   public WorkDefTeamProductLine() {
      super(AtsWorkDefinitionTokens.WorkDef_Team_ProductLine);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Review) //
         .andToStates(StateToken.Review, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andColor(StateColor.DARK_BLUE) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Name, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", "XCommitManager"));

      bld.andState(2, "Review", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Completed, StateToken.Cancelled, StateToken.InWork) //
         .andOverrideValidationStates(StateToken.InWork) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.DARK_YELLOW) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Name, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(3, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new CompositeLayoutItem(2, //
               new WidgetDefinition(AtsAttributeTypes.ProductLineApprovedDate, "XDateDam"), //
               new WidgetDefinition(AtsAttributeTypes.ProductLineApprovedBy, "XTextDam") //
            ) //
         );

      bld.andState(3, "Completed", StateType.Completed) //
         .andToStates(StateToken.Review, StateToken.InWork, StateToken.Cancelled) //
         .andOverrideValidationStates(StateToken.Review) //
         .andOverrideValidationStates(StateToken.InWork) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Name, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(3, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ));

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Review, StateToken.InWork) //
         .andOverrideValidationStates(StateToken.InWork) //
         .andOverrideValidationStates(StateToken.Review) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }
}
