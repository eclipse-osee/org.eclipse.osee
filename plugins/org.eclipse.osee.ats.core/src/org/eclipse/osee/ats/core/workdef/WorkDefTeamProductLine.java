/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.core.workdef;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
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
         .andToStates(StateToken.Review, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andColor(StateColor.DARK_BLUE) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", "XCommitManager"));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andColor(StateColor.DARK_YELLOW) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(3, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new WidgetDefinition("PL ARB Approved", "XProductLineApprovalWidget", REQUIRED_FOR_TRANSITION) //
         );

      bld.andState(3, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(3, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(Improvement,Problem,Refinement,Support)"), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(1,2,3,4,5)"), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam") //
            ));

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }
}
