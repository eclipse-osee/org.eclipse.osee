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
import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.framework.core.widget.XOption.HORZ_LABEL;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
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
            new AttrWidgetDef(Description), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(StartDate).set(HORZ_LABEL), //
               new AttrWidgetDef(EndDate).set(HORZ_LABEL) //
            ), //
            new AttrWidgetDef(Holiday), //
            new AttrWidgetDef(KanbanStoryName), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(PlannedPoints), //
               new AttrWidgetDef(UnplannedPoints) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Open Sprint Summary", XXOpenSprintSummaryArtWidget), //
               new WidgetDef("Open Sprint Data Table", XXOpenSprintDataTableArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Open Sprint Burn-Down", XXOpenSprintBurndownArtWidget), //
               new WidgetDef("Open Sprint Burn-Up", XXOpenSprintBurnupArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDef("Store Snapshot of Sprint Reports", XXStoreSprintReportsArtWidget), //
               new WidgetDef("Open Stored Sprint Reports", XXOpenStoredSprintReportsArtWidget) //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
