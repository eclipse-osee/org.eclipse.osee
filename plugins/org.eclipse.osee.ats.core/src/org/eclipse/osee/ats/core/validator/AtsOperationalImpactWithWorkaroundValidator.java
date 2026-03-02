/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsOperationalImpactWithWorkaroundValidator implements IAtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      if (!workItem.isTeamWorkflow()) {
         return result;
      }
      WidgetId widgetId = widgetDef.getWidgetId();
      if (widgetId.equals(WidgetIdAts.XOperationalImpactWithWorkaroundArtWidget)) {
         String impact =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.OperationalImpact, "No");
         if (impact.equals("Yes")) {
            String desc = atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
               AtsAttributeTypes.OperationalImpactDescription, "");
            if (!Strings.isValid(desc)) {
               return new WidgetResult(WidgetStatus.Invalid_Incompleted, "Must enter [%s]",
                  AtsAttributeTypes.OperationalImpactDescription.getName());
            }
         }
         String workaroundChecked = atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.OperationalImpactWorkaround, "No");
         if (workaroundChecked.equals("Yes")) {
            String desc = atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
               AtsAttributeTypes.OperationalImpactWorkaroundDescription, "");
            if (!Strings.isValid(desc)) {
               return new WidgetResult(WidgetStatus.Invalid_Incompleted, "Must enter [%s]",
                  AtsAttributeTypes.OperationalImpactWorkaroundDescription.getName());
            }
         }

      }
      return result;
   }

}
