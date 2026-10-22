/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.core.workdef;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCommitManagerWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkPackageEnumArtWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamMIM extends AbstractWorkDef {

   public WorkDefTeamMIM() {
      super(AtsWorkDefinitionTokens.WorkDefTeamMIM);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andCustomHeader() //
         .andLayout( //
            bld.getChangeTypeComposite(), //
            new WidgetDef(XXWorkPackageEnumArtWidget //
            ) //
         ).showMetricsHeader(false); //

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Review, StateToken.Cancelled) //
         .andColor(StateColor.DARK_BLUE) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new WidgetDef(XXWorkingBranchWfeWidget), // //
            new WidgetDef(XCommitManagerWfeWidget));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new WidgetDef(XXWorkingBranchWfeWidget), // //
            new WidgetDef(XCommitManagerWfeWidget) //
         );

      bld.andState(3, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andLayout( //
            new AttrWidgetDef(Description) //
         );

      bld.andState(4, "Cancelled", StateType.Cancelled);

      return bld.getWorkDefinition();
   }
}
