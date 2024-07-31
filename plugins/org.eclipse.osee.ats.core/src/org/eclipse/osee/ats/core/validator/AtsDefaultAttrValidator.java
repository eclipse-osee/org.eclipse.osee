/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Vaibhav Patel
 */
public class AtsDefaultAttrValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      /*
       * Required flags are checked here to avoid validating not required fields. Actual logic is implemented in the
       * validateWidgetIsRequired method.
       */
      if ((widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_COMPLETION) || widgetDef.getOptions().contains(
         WidgetOption.REQUIRED_FOR_TRANSITION)) && widgetDef.getAttributeType() != AttributeTypeToken.SENTINEL) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
      }
      return result;
   }
}
