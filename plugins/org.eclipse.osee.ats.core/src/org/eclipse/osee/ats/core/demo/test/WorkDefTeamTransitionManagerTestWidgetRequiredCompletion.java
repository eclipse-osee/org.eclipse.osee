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

package org.eclipse.osee.ats.core.demo.test;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.EstimatedHours;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.WorkPackage;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import static org.eclipse.osee.framework.core.widget.XOption.RFC;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitionTokens;
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
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamTransitionManagerTestWidgetRequiredCompletion extends AbstractWorkDef {

   public WorkDefTeamTransitionManagerTestWidgetRequiredCompletion() {
      super(DemoWorkDefinitionTokens.WorkDefTeamTransitionManagerTestWidgetRequiredCompletion);
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
               new AttrWidgetDef(EstimatedHours).set(RFC), //
               new AttrWidgetDef(WorkPackage).set(RFC) //
            ));

      bld.andState(2, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours).set(RFC), //
               new AttrWidgetDef(WorkPackage).set(RFC) //
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
