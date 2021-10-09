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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidator extends AtsXWidgetValidator {

   private static final List<String> TRUE_FALSE_VALUES = Arrays.asList("true", "false");

   public AtsXComboBooleanValidator() {
   }

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Success;
      if ("XComboBooleanDam".equals(widgetDef.getXWidgetName())) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, fromStateDef);
         if (!result.isSuccess()) {
            return result;
         }
         for (String value : provider.getValues()) {
            if (!isValid(value)) {
               return new WidgetResult(WidgetStatus.Invalid_Range, "[%s] value [%s] must be true or false",
                  provider.getName(), value);
            }
         }
      }
      return result;
   }

   private boolean isValid(String value) {
      return TRUE_FALSE_VALUES.contains(value);
   }
}
