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
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTaskAtsConfig2Example extends AbstractWorkDef {

   public WorkDefTaskAtsConfig2Example() {
      super(AtsWorkDefinitionTokens.WorkDefTaskAtsConfig2Example);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "None", StateType.Working).isStartState() //
         .andToStates(StateToken.InWork, StateToken.InReview, StateToken.Complete, StateToken.Not_Required) //
         .andRules(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion,
            RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andRecommendedPercentComplete(0) //
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

      bld.andState(2, "InWork", StateType.Working) //
         .andToStates(StateToken.InReview, StateToken.Complete, StateToken.Not_Required) //
         .andRules(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion,
            RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.BLACK) //
         .andRecommendedPercentComplete(15) //
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

      bld.andState(3, "InReview", StateType.Working) //
         .andToStates(StateToken.Complete, StateToken.Not_Required) //
         .andRules(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion,
            RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.DARK_RED) //
         .andRecommendedPercentComplete(60) //
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

      bld.andState(4, "Complete", StateType.Completed) //
         .andRules(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion,
            RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.DARK_GREEN) //
         .andRecommendedPercentComplete(100) //
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

      bld.andState(5, "Not_Required", StateType.Cancelled) //
         .andRules(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion,
            RuleDefinitionOption.RequireStateHourSpentPrompt) //
         .andColor(StateColor.DARK_GREEN) //
         .andRecommendedPercentComplete(100) //
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

      return bld.getWorkDefinition();
   }
}
