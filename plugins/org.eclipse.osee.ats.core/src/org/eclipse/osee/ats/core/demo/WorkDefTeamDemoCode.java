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
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkflowApplicWidget;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.ChangeReportTasksWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoCode extends AbstractDemoWorkDef {

   public WorkDefTeamDemoCode() {
      super(DemoWorkDefinitions.WorkDefTeamDemoCode);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken, AtsArtifactTypes.DemoCodeTeamWorkflow);

      bld.andPointsAttributeType(PointsEnum);
      addDemoCustomHeader(bld);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(ProposedResolution), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(ValidationRequired), //
               new AttrWidgetDef(WorkPackage) //
            ) //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(XXWorkflowApplicWidget, REQUIRED), //
            new AttrWidgetDef(Problem), //
            new AttrWidgetDef(ProposedResolution), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(WorkPackage), //
               new AttrWidgetDef(EstimatedHours) //
            ) //
         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate), //
            new SignDateWidgetDef("Manager Approved", ApproveRequestedHoursBy, ApproveRequestedHoursByDate) //
               .andImage(AtsImage.CHECK_CLIPBOARD) //
         );

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(XXWorkflowApplicWidget, REQUIRED), //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate), //
            new ChangeReportTasksWidgetDef(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges), //
            new AttrWidgetDef(Resolution) //
         );

      addCompletedState(bld, 5);
      addCancelledState(bld, 6);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
