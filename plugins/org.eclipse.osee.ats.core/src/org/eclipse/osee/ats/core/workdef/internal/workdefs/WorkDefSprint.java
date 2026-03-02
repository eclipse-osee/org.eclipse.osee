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

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
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
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.StartDate, XDateArtWidget, HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.EndDate, XDateArtWidget, HORZ_LABEL) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Holiday, XDateArtWidget), //
            new WidgetDefinition(AtsAttributeTypes.KanbanStoryName, XXTextWidget, FILL_VERT), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.PlannedPoints, XIntegerArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.UnplannedPoints, XIntegerArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Open Sprint Summary", XOpenSprintSummaryArtWidget), //
               new WidgetDefinition("Open Sprint Data Table", XOpenSprintDataTableArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Open Sprint Burn-Down", XOpenSprintBurndownArtWidget), //
               new WidgetDefinition("Open Sprint Burn-Up", XOpenSprintBurnupArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition("Store Snapshot of Sprint Reports", XStoreSprintReportsArtWidget), //
               new WidgetDefinition("Open Stored Sprint Reports", XOpenStoredSprintReportsArtWidget) //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
