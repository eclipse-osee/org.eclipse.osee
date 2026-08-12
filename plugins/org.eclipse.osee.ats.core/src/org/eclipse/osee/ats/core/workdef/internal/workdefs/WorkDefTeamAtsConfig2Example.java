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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamAtsConfig2Example extends AbstractWorkDef {

   public WorkDefTeamAtsConfig2Example() {
      super(AtsWorkDefinitionTokens.WorkDefTeamAtsConfig2Example);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andHeader() //
         .andLayout(getChangeTypeComposite()) //
         .isShowMetricsHeader(false); //

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT), //
            new WidgetDef(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERT), //
            new WidgetDef(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDef(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new WidgetDef(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERT));

      bld.andState(3, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
