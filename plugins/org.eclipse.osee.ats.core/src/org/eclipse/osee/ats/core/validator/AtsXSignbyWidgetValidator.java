/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsXSignbyWidgetValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      if (widgetDef.getXWidgetName().equals("XSignbyWidget")) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
         if (!result.isSuccess()) {
            return result;
         }
      }
      return result;
   }
}
