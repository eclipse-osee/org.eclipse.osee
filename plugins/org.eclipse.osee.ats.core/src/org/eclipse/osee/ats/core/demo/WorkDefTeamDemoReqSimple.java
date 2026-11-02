/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.EstimatedHours;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.WorkPackage;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCommitManagerWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXValidateReqChangesButtonWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;

/**
 * @author Jaden W. Puckett
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoReqSimple extends AbstractWorkDef {

   public WorkDefTeamDemoReqSimple() {
      super(DemoWorkDefinitions.WorkDefTeamDemoReqSimple);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(WorkPackage) //
            ));

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new CompositeLayoutItem(6, //
               new WidgetDef(XXWorkingBranchWfeWidget), //
               new WidgetDef(XXValidateReqChangesButtonWfeWidget), //
               new WidgetDef(XCommitManagerWfeWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(WorkPackage) //
            ));

      bld.andState(3, "Completed", StateType.Completed) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
