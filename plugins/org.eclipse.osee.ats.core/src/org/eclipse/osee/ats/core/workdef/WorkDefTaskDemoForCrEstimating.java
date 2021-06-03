/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Assumptions;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Category1;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.EstimatedCompletionDate;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.WorkflowNotes;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.AUTO_SAVE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTaskDemoForCrEstimating extends WorkDefTaskDefault {

   public WorkDefTaskDemoForCrEstimating(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   public WorkDefTaskDemoForCrEstimating() {
      this(DemoWorkDefinitions.WorkDef_Task_Demo_For_CR_Estimating);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(Description, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //
            new WidgetDefinition(Assumptions, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Estimated Points", "XPointsWidget", AUTO_SAVE), //
               new WidgetDefinition(EstimatedCompletionDate, "XDateDam", AUTO_SAVE) //
            ), //
            new WidgetDefinition("TLE Reviewed Estimate", AtsAttributeTypes.TleReviewedDate, "XTleReviewedWidget",
               REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(WorkflowNotes, "XTextDam", AUTO_SAVE), //
               new WidgetDefinition(Category1, "XTextDam", AUTO_SAVE) //
            ) //
         );

      bld.andState(2, "Completed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }

}
