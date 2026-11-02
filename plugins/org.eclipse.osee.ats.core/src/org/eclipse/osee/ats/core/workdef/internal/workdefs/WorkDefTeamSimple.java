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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.ProposedResolution;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Resolution;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.ValidationRequired;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamSimple extends AbstractWorkDef {

   public WorkDefTeamSimple() {
      super(AtsWorkDefinitionTokens.WorkDefTeamSimple);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andCustomHeader() //
         .andLayout(bld.getChangeTypeComposite(), //
            new AttrWidgetDef(AtsAttributeTypes.WorkPackage) //
         ) //
         .showMetricsHeader(false); //

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.InWork, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(ValidationRequired));

      bld.andState(2, "InWork", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Resolution));

      bld.andState(3, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
