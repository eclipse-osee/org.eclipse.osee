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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
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
@SuppressWarnings("unused")
public class WorkDefReviewDecision extends AbstractWorkDef {

   public WorkDefReviewDecision() {
      super(AtsWorkDefinitionTokens.WorkDef_Review_Decision);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToStates(StateToken.Decision, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.DecisionReviewOptions, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, XComboEnumArtWidget, RFT, HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, XDateArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, XHyperlinkWfdForRelatedStateArtWidget, FILL_VERT) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget));

      bld.andState(2, "Decision", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Followup, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, XLabelWidget), //
            new WidgetDefinition(AtsAttributeTypes.Decision, XComboArtWidget, RFT, HORZ_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(3, "Followup", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition(CoreAttributeTypes.Name, XLabelWidget), //
            new WidgetDefinition(AtsAttributeTypes.Decision, XComboArtWidget, RFT, HORZ_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
