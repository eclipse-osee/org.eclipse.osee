/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.AgilePiTrackingEnum;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXAgileFeatureSelWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXAgileSprintSelWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkPackageEnumArtWidget;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * Common base for Demo Work Definitions (Code, Req, Test) that share the same custom header layout.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractDemoWorkDef extends AbstractWorkDef {

   public AbstractDemoWorkDef(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   /**
    * Adds the standard demo custom header: ChangeType + WorkPackage on first row, Sprint + AgilePiTracking on second
    * row, no metrics header.
    */
   protected void addDemoCustomHeader(WorkDefBuilder bld) {
      bld.andCustomHeader() //
         .andLayout( //
            new CompositeLayoutItem(4, //
               bld.getChangeTypeComposite(), //
               new WidgetDef(XXWorkPackageEnumArtWidget) //
            ), new CompositeLayoutItem(6, //
               new WidgetDef(XXAgileSprintSelWidget), //
               new WidgetDef(XXAgileFeatureSelWidget), //
               new AttrWidgetDef(AgilePiTrackingEnum) //
            )) //
         .showMetricsHeader(false);
   }

   /**
    * Adds the standard Completed state with AddDecisionValidateBlockingReview rule.
    */
   protected void addCompletedState(WorkDefBuilder bld, int stateNum) {
      bld.andState(stateNum, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK);
   }

   /**
    * Adds the standard Cancelled state.
    */
   protected void addCancelledState(WorkDefBuilder bld, int stateNum) {
      bld.andState(stateNum, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);
   }
}
