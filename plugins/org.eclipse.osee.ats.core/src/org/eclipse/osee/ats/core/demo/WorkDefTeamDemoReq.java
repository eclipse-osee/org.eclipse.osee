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
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCommitManagerWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXValidateReqChangesButtonWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXCheckBoxWidget;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.ChangeReportTasksWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.framework.core.widget.XOption;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoReq extends AbstractDemoWorkDef {

   public WorkDefTeamDemoReq() {
      super(DemoWorkDefinitions.WorkDefTeamDemoReq);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andPointsAttributeType(PointsEnum);
      addDemoCustomHeader(bld);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(Description, XOption.REQUIRED), //
            new AttrWidgetDef(ProposedResolution), //
            new CompositeLayoutItem(8, //
               new AttrWidgetDef(ValidationRequired), //
               new AttrWidgetDef(WorkPackage), //
               new AttrWidgetDef(AllowWebExport), //
               new AttrWidgetDef("Crash Display w/ User Group w/o Joe", CrashOrBlankDisplay,
                  XXCheckBoxWidget).andUserGroup(AtsUserGroups.PeerReviewCreationNotify) //
            ), //
            new SignDateWidgetDef("Lead Sign Off", SignedOffBy, SignedOffByDate), //
            new SignDateWidgetDef("Manager Signoff", ManagerSignedOffBy, ManagerSignedOffByDate), //
            new SignDateWidgetDef("Approved Hours", ApproveRequestedHoursBy, ApproveRequestedHoursByDate) //
               .andImage(AtsImage.CHECK_CLIPBOARD));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Implement, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Problem), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(EstimatedHours));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andTransitionListener(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges) //
         .andLayout( //
            new WidgetDef(XXWorkingBranchWfeWidget), //
            new WidgetDef(XXValidateReqChangesButtonWfeWidget), //
            new WidgetDef(XCommitManagerWfeWidget), //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate), //
            new ChangeReportTasksWidgetDef(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges), //
            new AttrWidgetDef(Resolution));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK) //
         .andTransitionListener(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
