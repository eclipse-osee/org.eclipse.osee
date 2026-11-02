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

package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXListRelationWidget;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import static org.eclipse.osee.framework.core.widget.XOption.VAL_NO_SEL;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoTest extends AbstractDemoWorkDef {

   public WorkDefTeamDemoTest() {
      super(DemoWorkDefinitions.WorkDefTeamDemoTest);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andPointsAttributeType(PointsEnum, "1", "2", "3", "Epic");
      addDemoCustomHeader(bld);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(ProposedResolution), //
            new WidgetDef("Related Tasks", AtsRelationTypes.TeamWfToTask_Task, XXListRelationWidget, VAL_NO_SEL), //
            new AttrWidgetDef(ValidationRequired), //
            new AttrWidgetDef(WorkPackage));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(Problem), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(EstimatedHours));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate), //
            new AttrWidgetDef(Resolution));

      addCancelledState(bld, 6);
      addCompletedState(bld, 5);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
