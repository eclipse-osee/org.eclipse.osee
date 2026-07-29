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
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefSprint extends AbstractWorkDef {

   public WorkDefSprint() {
      super(AtsWorkDefinitionTokens.WorkDefSprint);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(AtsAttributeTypes.Description, "XTextDam", FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDef(AtsAttributeTypes.StartDate, "XDateDam", HORZ_LABEL), //
               new WidgetDef(AtsAttributeTypes.EndDate, "XDateDam", HORZ_LABEL) //
            ), //
            new WidgetDef(AtsAttributeTypes.Holiday, "XDateDam"), //
            new WidgetDef(AtsAttributeTypes.KanbanStoryName, "XTextDam", FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDef(AtsAttributeTypes.PlannedPoints, "XIntegerDam"), //
               new WidgetDef(AtsAttributeTypes.UnplannedPoints, "XIntegerDam") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Open Sprint Summary", "XOpenSprintSummaryButton"), //
               new WidgetDef("Open Sprint Data Table", "XOpenSprintDataTableButton") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Open Sprint Burn-Down", "XOpenSprintBurndownButton"), //
               new WidgetDef("Open Sprint Burn-Up", "XOpenSprintBurnupButton") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Store Snapshot of Sprint Reports", "XStoreSprintReportsButton"), //
               new WidgetDef("Open Stored Sprint Reports", "XOpenStoredSprintReportsButton") //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
