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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.CreateChangeReportTasksWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDemoCode extends AbstractWorkDef {

   public WorkDefTeamDemoCode() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Code);
   }

   @Override
   public WorkDefinition build() {
      /**
       * Artifact types used can be declared through Team Definition or Workflow Definition. This is an example of the
       * config through Work Definition.
       */
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken, AtsDemoOseeTypes.DemoCodeTeamWorkflow);

      bld.andHeader() //
         .andLayout(getChangeTypeComposite()) //
         .isShowWorkPackageHeader(true) //
         .isShowMetricsHeader(false); //

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection",
               REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, "XComboBooleanDam"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection",
               REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new WidgetDefinition("Manager Approval", "XRequestedHoursApprovalWidget") //
         );

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection",
               REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new CreateChangeReportTasksWidgetDefinition("Create Tasks from Requirement Changes",
               TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsDemoOseeTypes.LocAffected, "XIntegerDam", WidgetOption.EDITABLE), //
               new WidgetDefinition(AtsDemoOseeTypes.LocReviewed, "XIntegerDam", WidgetOption.EDITABLE), //
               new WidgetDefinition(AtsDemoOseeTypes.LocRemaining, "XComputedCharacteristicWidget",
                  WidgetOption.NOT_EDITABLE, WidgetOption.NO_SELECT) //
            ));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
