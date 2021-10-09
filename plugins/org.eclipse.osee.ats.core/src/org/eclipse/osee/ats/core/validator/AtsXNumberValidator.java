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
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsXNumberValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Success;
      String name = widgetDef.getXWidgetName();

      if ("XFloatDam".equals(name)) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
         if (!result.isSuccess()) {
            return result;
         }
         return isValidFloat(provider, widgetDef);
      }
      if ("XIntegerDam".equals(name)) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
         if (!result.isSuccess()) {
            return result;
         }
         return isValidInteger(provider, widgetDef);
      }
      return result;
   }
}
