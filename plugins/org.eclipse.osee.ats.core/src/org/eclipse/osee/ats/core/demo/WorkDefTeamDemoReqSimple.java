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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Jaden W. Puckett
 */
@SuppressWarnings("unused")
public class WorkDefTeamDemoReqSimple extends AbstractWorkDef {

   public WorkDefTeamDemoReqSimple() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Req_Simple);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget) //
            ));

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT), //
            new CompositeLayoutItem(4, //
               getWorkingBranchWidgetComposite(), //
               new WidgetDefinition("Validate Requirement Changes", XValidateReqChangesButtonArtWidget), //
               new WidgetDefinition("Commit Manager", XCommitManagerArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget) //
            ));

      bld.andState(3, "Completed", StateType.Completed) //
         .andColor(StateColor.BLACK);

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
