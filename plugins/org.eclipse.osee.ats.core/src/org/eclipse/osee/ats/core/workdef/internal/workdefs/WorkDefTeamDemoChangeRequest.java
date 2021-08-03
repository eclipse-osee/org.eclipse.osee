/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.AUTO_SAVE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_HORIZONTALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORIZONTAL_LABEL;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.LABEL_AFTER;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.VALIDATE_DATE;
import static org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption.AllowAssigneeToAll;
import static org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption.AllowEditToAll;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.GroupCompositeLayoutItem;
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

      addCompositeHeader(bld);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andRules(AllowAssigneeToAll, AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.ExternalReference, "XTextDam", AUTO_SAVE), //

            new WidgetDefinition("Create/Open Great Escape Workflow", "XCreateEscapeDemoWfXButton"), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION,
               AUTO_SAVE), //
            new CompositeLayoutItem(4,
               new WidgetDefinition(AtsAttributeTypes.HowToReproduceProblem, "XTextDam", FILL_VERTICALLY),
               new WidgetDefinition(AtsAttributeTypes.WorkaroundDescription, "XTextDam", FILL_VERTICALLY) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.CrashOrBlankDisplay, "XCheckBoxThreeStateDam", HORIZONTAL_LABEL,
                  REQUIRED_FOR_TRANSITION, LABEL_AFTER, AUTO_SAVE),
               new WidgetDefinition(AtsAttributeTypes.NonFunctionalProblem, "XCheckBoxThreeStateDam", HORIZONTAL_LABEL,
                  REQUIRED_FOR_TRANSITION, LABEL_AFTER, AUTO_SAVE) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.ImpactToMissionOrCrew, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //

            new GroupCompositeLayoutItem(1, "Build Impact(s)",
               new CompositeLayoutItem(4, new WidgetDefinition("Feature(s) Impacted",
                  AtsAttributeTypes.FeatureImpactReference, "XHyperlinkFeatureDam", FILL_HORIZONTALLY) //
               ) //
            ), //

            new GroupCompositeLayoutItem(1, "Environment Configuration", new CompositeLayoutItem(4, //
               new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
               new WidgetDefinition("Introduced-In Version", "XIntroducedInVersionWithPersistWidget") //
            )), //

            new GroupCompositeLayoutItem(1, "Estimates and Funding", //
               new WidgetDefinition(AtsAttributeTypes.ProblemFirstObserved, "XHyperlinkLabelValueSelectionDam",
                  VALIDATE_DATE) //
            ) //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Completed, StateToken.Cancelled) //
         .andRules(AllowAssigneeToAll, AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Create/Open Great Escape Workflow", "XCreateEscapeDemoWfXButton"), //

            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.RiskAnalysis, "XHyperlinkLabelValueSelectionDam"), //
               new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
               new WidgetDefinition("Introduced-In Version", "XIntroducedInVersionWithPersistWidget"), //
               new WidgetDefinition(AtsAttributeTypes.RevisitDate, "XHyperlinkLabelValueSelectionDam") //
            ), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION,
               AUTO_SAVE), //
            new WidgetDefinition(AtsAttributeTypes.WorkaroundDescription, "XTextDam", FILL_VERTICALLY,
               REQUIRED_FOR_TRANSITION, AUTO_SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.RootCause, "XTextDam", FILL_VERTICALLY, AUTO_SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY, AUTO_SAVE) //
            ), //

            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget") //

         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andRules(AllowAssigneeToAll, AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.RevisitDate, "XHyperlinkLabelValueSelectionDam", VALIDATE_DATE), //
            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget"), //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andRules(AllowAssigneeToAll, AllowEditToAll) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget"), //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(AllowEditToAll) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget") //
         );

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andRules(RuleDefinitionOption.AllowEditToAll) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
