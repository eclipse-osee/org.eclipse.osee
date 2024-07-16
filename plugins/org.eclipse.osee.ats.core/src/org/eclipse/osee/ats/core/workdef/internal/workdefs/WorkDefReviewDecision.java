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
         .andToStates(StateToken.Decision, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
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
         .andToStates(StateToken.Completed, StateToken.Followup, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, "XLabelDam"), //
            new WidgetDefinition(AtsAttributeTypes.Decision, "XComboDam(1,2,3)", REQUIRED_FOR_TRANSITION,
               HORIZONTAL_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(3, "Followup", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, "XLabelDam"), //
            new WidgetDefinition(AtsAttributeTypes.Decision, "XComboDam(1,2,3)", REQUIRED_FOR_TRANSITION,
               HORIZONTAL_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
