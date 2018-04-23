/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.validate;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsOperationalImpactWithWorkaroundValidator implements IAtsXWidgetValidator {

   public static String WIDGET_NAME = "OperationalImpactWithWorkaroundXWidget";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Valid;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         if (provider instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) provider;
            String impact = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, "No");
            if (impact.equals("Yes")) {
               String desc = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, "");
               if (!Strings.isValid(desc)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef, "Must enter [%s]",
                     AtsAttributeTypes.OperationalImpactDescription.getName());
               }
            }
            String workaroundChecked =
               teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, "No");
            if (workaroundChecked.equals("Yes")) {
               String desc =
                  teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, "");
               if (!Strings.isValid(desc)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef, "Must enter [%s]",
                     AtsAttributeTypes.OperationalImpactWorkaroundDescription.getName());
               }
            }

         }
      }
      return result;
   }

}
