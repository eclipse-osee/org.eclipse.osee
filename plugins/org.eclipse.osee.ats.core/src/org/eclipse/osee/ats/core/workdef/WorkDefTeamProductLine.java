/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.SignByAndDateWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
public class WorkDefTeamProductLine extends AbstractWorkDef {

   public WorkDefTeamProductLine() {
      super(AtsWorkDefinitionTokens.WorkDef_Team_ProductLine);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andHeader() //
         .andLayout( //
            getChangeTypeComposite(), //
            new WidgetDefinition("Work Package", XHyperlinkWorkPackageArtWidget //
            ) //
         ).isShowMetricsHeader(false); //

      bld.andState(1, "InWork", StateType.Working).isStartState() //
         .andToStates(StateToken.Review, StateToken.Cancelled) //

         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andColor(StateColor.DARK_BLUE) //
         .andLayout( //
            new WidgetDefinition("Description", AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", XCommitManagerArtWidget));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //

         .andRules(RuleDefinitionOption.AllowTransitionWithWorkingBranch) //
         .andColor(StateColor.DARK_YELLOW) //
         .andLayout( //
            new WidgetDefinition("Description", AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", XCommitManagerArtWidget), //
            new SignByAndDateWidgetDefinition("PL ARB Approved", ProductLineApprovedBy, ProductLineApprovedDate) //
               .andRequiredByTeamLead() //
         );

      bld.andState(3, "Completed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Description", AtsAttributeTypes.Description, XXTextWidget, FILL_VERT) //
         );

      bld.andState(4, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_RED);

      return bld.getWorkDefinition();
   }
}
