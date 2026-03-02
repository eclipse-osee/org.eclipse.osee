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
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
public class WorkDefTaskDefault extends AbstractWorkDef {

   public WorkDefTaskDefault() {
      super(AtsWorkDefinitionTokens.WorkDef_Task_Default);
   }

   public WorkDefTaskDefault(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, XHyperlinkWfdForRelatedStateArtWidget, FILL_VERT) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.WorkflowNotes, XXTextWidget), //
               new WidgetDefinition(AtsAttributeTypes.Category1, XXTextWidget) //
            ));

      bld.andState(2, "Completed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }
}
