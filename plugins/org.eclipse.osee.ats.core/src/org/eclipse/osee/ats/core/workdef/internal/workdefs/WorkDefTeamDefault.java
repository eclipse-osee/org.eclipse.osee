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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
public class WorkDefTeamDefault extends AbstractWorkDef {

   public WorkDefTeamDefault() {
      super(AtsWorkDefinitionTokens.WorkDef_Team_Default);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andHeader() //
         .andLayout( //
            getChangeTypeComposite(), //
            new WidgetDefinition("Work Package", XHyperlinkWorkPackageArtWidget) //
         ) //
         .isShowMetricsHeader(false); //

      bld.andState(1, "Endorse", StateType.Working) //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ValidationRequired, XCheckBoxArtWidget, HORZ_LABEL));

      bld.andState(2, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Authorize, StateToken.Implement, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.Problem, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.ProposedResolution, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget, RFT));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget, HORZ_LABEL));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            getWorkingBranchWidgetComposite(), //
            new WidgetDefinition("Commit Manager", XCommitManagerArtWidget), //
            new WidgetDefinition(AtsAttributeTypes.WorkPackage, XXTextWidget), //
            new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget, HORZ_LABEL), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
