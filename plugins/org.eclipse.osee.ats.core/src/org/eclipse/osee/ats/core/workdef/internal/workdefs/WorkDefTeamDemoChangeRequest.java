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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.AUTO_SAVE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORIZONTAL_LABEL;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDemoChangeRequest extends AbstractWorkDef {

   public WorkDefTeamDemoChangeRequest() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Change_Request);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //

            new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
            new WidgetDefinition("Create/Open Change Request Analysis Workflow", "XCreateEscapementDemoWfXButton"), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION,
               AUTO_SAVE), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam", HORIZONTAL_LABEL, AUTO_SAVE) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.FeatureImpactReference, "XHyperlinkFeatureDam"));

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Implement, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //

            new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
            new WidgetDefinition("Create/Open Change Request Analysis Workflow", "XCreateEscapementDemoWfXButton"), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION,
               AUTO_SAVE), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam", AUTO_SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY, AUTO_SAVE) //
            ), //
            new CompositeLayoutItem(6, //
               new WidgetDefinition(AtsAttributeTypes.ChangeType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.Priority, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam", HORIZONTAL_LABEL, AUTO_SAVE) //
            ), //
            new WidgetDefinition("Task Estimating", "XTaskEstDemoWidget") //
         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.RevisitDate, "XDateWithValidateDam", AUTO_SAVE), //
            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget"), //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget") //
         );

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(RuleDefinitionOption.AllowAssigneeToAll, RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", "XCommitManager"), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, "XTextDam", AUTO_SAVE), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam", HORIZONTAL_LABEL, AUTO_SAVE), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY, AUTO_SAVE));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AllowEditToAll, RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"));

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
