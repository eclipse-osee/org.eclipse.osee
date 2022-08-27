/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AtsWalktrhoughAttrValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      if (widgetDef.getOptions().contains(WidgetOption.WALKTHROUGH) && widgetDef.getOptions().contains(
         WidgetOption.REQUIRED_FOR_TRANSITION)) {
         AttributeTypeToken attrType = widgetDef.getAttributeType();
         if (atsApi.getAttributeResolver().getAttributeCount(workItem, attrType) == 0) {
            result = new WidgetResult(WidgetStatus.Invalid_Incompleted, "[%s] is required for transition to [%s]",
               widgetDef.getName(), toStateDef.getName());
         }
      }
      return result;
   }
}
