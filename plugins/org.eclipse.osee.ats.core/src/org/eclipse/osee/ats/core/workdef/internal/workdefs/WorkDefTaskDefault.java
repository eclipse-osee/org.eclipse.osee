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
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTaskDefault extends AbstractWorkDef {

   public WorkDefTaskDefault() {
      super(AtsWorkDefinitionTokens.WorkDefTaskDefault);
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
            new AttrWidgetDef(Description), //
            new CompositeLayoutItem(6, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(EstimatedCompletionDate), //
               new AttrWidgetDef(RelatedToState) //
            ), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(WorkflowNotes), //
               new AttrWidgetDef(Category1) //
            ));

      bld.andState(2, "Completed", StateType.Completed);

      bld.andState(3, "Cancelled", StateType.Cancelled);

      return bld.getWorkDefinition();
   }
}
