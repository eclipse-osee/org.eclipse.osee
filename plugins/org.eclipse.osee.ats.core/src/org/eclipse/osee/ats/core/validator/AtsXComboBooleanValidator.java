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
package org.eclipse.osee.ats.core.validator;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;

/**
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidator extends AtsXWidgetValidator {

   private static final List<String> TRUE_FALSE_VALUES = Arrays.asList("true", "false");

   public AtsXComboBooleanValidator() {
   }

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
      WidgetResult result = WidgetResult.Valid;
      if ("XComboBooleanDam".equals(widgetDef.getXWidgetName())) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, fromStateDef);
         if (!result.isValid()) {
            return result;
         }
         for (String value : provider.getValues()) {
            if (!isValid(value)) {
               return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%s] must be true or false",
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
