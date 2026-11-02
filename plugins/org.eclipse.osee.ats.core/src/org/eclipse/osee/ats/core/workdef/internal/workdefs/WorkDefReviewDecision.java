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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXDecisionRevWidget;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.widget.XOption.HORZ_LABEL;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefReviewDecision extends AbstractWorkDef {

   public WorkDefReviewDecision() {
      super(AtsWorkDefinitionTokens.WorkDefReviewDecision);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToStates(StateToken.Decision, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(DecisionReviewOptions), //
            new AttrWidgetDef(Description), //
            new CompositeLayoutItem(6, //
               new AttrWidgetDef(ReviewBlocks).set(REQUIRED, HORZ_LABEL), //
               new AttrWidgetDef(NeedBy), //
               new AttrWidgetDef(RelatedToState) //
            ), //
            new AttrWidgetDef(EstimatedHours));

      bld.andState(2, "Decision", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Followup, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Name), //
            // Decision attr type must be specified so server-side transition validation enforces REQUIRED
            new AttrWidgetDef(Decision, XXDecisionRevWidget, REQUIRED), //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Resolution));

      bld.andState(3, "Followup", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Resolution));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andLayout( //
            new AttrWidgetDef(Name), //
            // Decision attr type must be specified so server-side transition validation enforces REQUIRED
            new AttrWidgetDef(Decision, XXDecisionRevWidget, REQUIRED), //
            new AttrWidgetDef(Resolution));

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
