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
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORIZONTAL_LABEL;
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
public class WorkDefTeamDefault extends AbstractWorkDef {

   public WorkDefTeamDefault() {
      super(AtsWorkDefinitionTokens.WorkDef_Team_Default);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Endorse", StateType.Working) //
         .andToDefaultState(StateToken.Analyze) //
         .andToStates(StateToken.Cancelled, StateToken.Analyze) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION), //
               new WidgetDefinition(AtsAttributeTypes.PriorityType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam", HORIZONTAL_LABEL) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, "XCheckBoxDam", HORIZONTAL_LABEL));

      bld.andState(2, "Analyze", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Authorize) //
         .andToStates(StateToken.Cancelled, StateToken.Authorize, StateToken.Implement, StateToken.Endorse) //
         .andOverrideValidationStates(StateToken.Endorse) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION), //
               new WidgetDefinition(AtsAttributeTypes.PriorityType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam", HORIZONTAL_LABEL) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam", REQUIRED_FOR_TRANSITION));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToDefaultState(StateToken.Implement) //
         .andToStates(StateToken.Cancelled, StateToken.Implement, StateToken.Analyze) //
         .andOverrideValidationStates(StateToken.Analyze) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam", HORIZONTAL_LABEL));

      bld.andState(4, "Implement", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Cancelled, StateToken.Completed, StateToken.Analyze, StateToken.Authorize) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Authorize) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Working Branch", "XWorkingBranch"), //
            new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam", HORIZONTAL_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(5, "Completed", StateType.Completed) //
         .andToStates(StateToken.Implement) //
         .andOverrideValidationStates(StateToken.Implement) //
         .andRules(RuleDefinitionOption.AllowEditToAll, RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Analyze, StateToken.Authorize, StateToken.Implement, StateToken.Endorse) //
         .andOverrideValidationStates(StateToken.Analyze, StateToken.Authorize, StateToken.Implement,
            StateToken.Endorse) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
