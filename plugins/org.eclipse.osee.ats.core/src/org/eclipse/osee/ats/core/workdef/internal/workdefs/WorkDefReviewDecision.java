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
public class WorkDefReviewDecision extends AbstractWorkDef {

   public WorkDefReviewDecision() {
      super(AtsWorkDefinitionTokens.WorkDef_Review_Decision);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Decision) //
         .andToStates(StateToken.Cancelled, StateToken.Decision) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.DecisionReviewOptions, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, HORIZONTAL_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam"), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, "XStateCombo", FILL_VERTICALLY) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(2, "Decision", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Followup, StateToken.Cancelled, StateToken.Completed, StateToken.Prepare) //
         .andOverrideValidationStates(StateToken.Prepare) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, "XLabelDam"), //
            new WidgetDefinition(AtsAttributeTypes.Decision, "XComboDam(1,2,3)", REQUIRED_FOR_TRANSITION,
               HORIZONTAL_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(3, "Followup", StateType.Working) //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Cancelled, StateToken.Completed, StateToken.Decision) //
         .andOverrideValidationStates(StateToken.Decision) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(4, "Completed", StateType.Completed) //
         .andToStates(StateToken.Decision, StateToken.Followup) //
         .andOverrideValidationStates(StateToken.Decision, StateToken.Followup) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, "XLabelDam"), //
            new WidgetDefinition(AtsAttributeTypes.Decision, "XComboDam(1,2,3)", REQUIRED_FOR_TRANSITION,
               HORIZONTAL_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Decision, StateToken.Followup, StateToken.Prepare) //
         .andOverrideValidationStates(StateToken.Decision, StateToken.Followup, StateToken.Prepare) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
