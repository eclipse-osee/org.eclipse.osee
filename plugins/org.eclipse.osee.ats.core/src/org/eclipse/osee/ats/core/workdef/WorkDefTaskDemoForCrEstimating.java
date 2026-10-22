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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.xx.PointsNumericWidgetDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTaskDemoForCrEstimating extends WorkDefTaskDefault {

   public WorkDefTaskDemoForCrEstimating(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   public WorkDefTaskDemoForCrEstimating() {
      this(DemoWorkDefinitions.WorkDefTaskDemoForCrEstimating);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Assumptions), //
            new AttrWidgetDef(RiskFactor), //
            new CompositeLayoutItem(4, //
               new PointsNumericWidgetDefinition("Estimated Points").andRequired(), //
               new AttrWidgetDef(EstimatedCompletionDate) //
            ), //
            new SignDateWidgetDef("Reviewed By", ReviewedBy, ReviewedByDate));

      bld.andState(2, "Completed", StateType.Completed);

      bld.andState(3, "Cancelled", StateType.Cancelled);

      return bld.getWorkDefinition();
   }


   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
