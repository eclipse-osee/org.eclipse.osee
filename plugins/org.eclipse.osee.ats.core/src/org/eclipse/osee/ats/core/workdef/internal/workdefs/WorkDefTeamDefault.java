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
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCommitManagerWfeWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import static org.eclipse.osee.framework.core.widget.XOption.HORZ_LABEL;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
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

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDefault extends AbstractWorkDef {

   public WorkDefTeamDefault() {
      super(AtsWorkDefinitionTokens.WorkDefTeamDefault);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andCustomHeader() //
         .andLayout( //
            bld.getChangeTypeComposite(), //
            new AttrWidgetDef(AtsAttributeTypes.WorkPackage) //
         ) //
         .showMetricsHeader(false); //

      bld.andState(1, "Endorse", StateType.Working) //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(ValidationRequired).set(HORZ_LABEL));

      bld.andState(2, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Authorize, StateToken.Implement, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(Problem), //
            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(EstimatedHours).set(REQUIRED));

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate).set(HORZ_LABEL));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(XXWorkingBranchWfeWidget), // //
            new WidgetDef(XCommitManagerWfeWidget), //
            new AttrWidgetDef(WorkPackage), //
            new AttrWidgetDef(EstimatedCompletionDate).set(HORZ_LABEL), //
            new AttrWidgetDef(Resolution));

      bld.andState(5, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview);

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
