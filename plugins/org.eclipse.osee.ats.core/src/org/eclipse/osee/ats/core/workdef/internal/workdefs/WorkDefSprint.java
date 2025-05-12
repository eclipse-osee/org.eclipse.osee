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
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORZ_LABEL;
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

/**
 * @author Donald G. Dunne
 */
public class WorkDefSprint extends AbstractWorkDef {

   public WorkDefSprint() {
      super(AtsWorkDefinitionTokens.WorkDef_Sprint);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.StartDate, "XDateDam", HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.EndDate, "XDateDam", HORZ_LABEL) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Holiday, "XDateDam"), //
            new WidgetDefinition(AtsAttributeTypes.KanbanStoryName, "XTextDam", FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.PlannedPoints, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.UnplannedPoints, "XIntegerDam") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Open Sprint Summary", "XOpenSprintSummaryButton"), //
               new WidgetDefinition("Open Sprint Data Table", "XOpenSprintDataTableButton") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Open Sprint Burn-Down", "XOpenSprintBurndownButton"), //
               new WidgetDefinition("Open Sprint Burn-Up", "XOpenSprintBurnupButton") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Store Snapshot of Sprint Reports", "XStoreSprintReportsButton"), //
               new WidgetDefinition("Open Stored Sprint Reports", "XOpenStoredSprintReportsButton") //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
