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
public class WorkDefTaskDefault extends AbstractWorkDef {

   public WorkDefTaskDefault() {
      super(AtsWorkDefinitionTokens.WorkDef_Task_Default);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andOverrideValidationStates(StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, "XStateCombo", FILL_VERTICALLY) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.SmaNote, "XTextDam"), //
               new WidgetDefinition(AtsAttributeTypes.Category1, "XTextDam") //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andToStates(StateToken.InWork) //
         .andOverrideValidationStates(StateToken.InWork) //
         .andColor(StateColor.BLACK);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
