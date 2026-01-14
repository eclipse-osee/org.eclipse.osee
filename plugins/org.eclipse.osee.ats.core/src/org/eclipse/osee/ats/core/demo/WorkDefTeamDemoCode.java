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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CreateChangeReportTasksWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.SignByAndDateWidgetDefinition;
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
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken, AtsArtifactTypes.DemoCodeTeamWorkflow);

      bld.andHeader() //
         .andLayout(getChangeTypeComposite(), //
            new WidgetDefinition("Work Package", "XHyperlinkWorkPackageDam" //
            )).isShowMetricsHeader(false); //

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection", RFT), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, "XComboBooleanDam"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection", RFT), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new SignByAndDateWidgetDefinition("Manager Approved", AtsAttributeTypes.ApproveRequestedHoursBy,
               AtsAttributeTypes.ApproveRequestedHoursByDate) //
                  .andImage(AtsImage.CHECK_CLIPBOARD) //
         );

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Referenced Applicability", "XHyperlabelWorkflowApplicabilitySelection", RFT), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam"), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam"), //
            new CreateChangeReportTasksWidgetDefinition("Create Tasks from Requirement Changes",
               TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERT) //
         );

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }
}
