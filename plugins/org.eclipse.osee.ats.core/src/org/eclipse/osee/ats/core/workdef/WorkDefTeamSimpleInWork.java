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

package org.eclipse.osee.ats.core.workdef;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamSimpleInWork extends AbstractWorkDef {

   public WorkDefTeamSimpleInWork() {
      super(AtsWorkDefinitionTokens.WorkDef_Team_Simple_InWork);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Analyze", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Description, "XTextDam", FILL_VERTICALLY));

      bld.andState(2, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Description", CoreAttributeTypes.Description, "XTextDam", FILL_VERTICALLY));

      bld.andState(3, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }
}
