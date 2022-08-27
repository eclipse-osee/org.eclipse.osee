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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Add any xwidgets that need attr type(s) to be valid
 *
 * @author Donald G. Dunne
 */
public class AtsXWidgetAttrValidator extends AtsXWidgetValidator {

   private final List<AttributeWidget> widgets = Arrays.asList( //
      new AttributeWidget("XReviewedWidget", AtsAttributeTypes.ReviewedBy, AtsAttributeTypes.ReviewedByDate) //
   );

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      for (AttributeWidget sdWidget : widgets) {
         if (sdWidget.getWidgetName().equals(widgetDef.getXWidgetName())) {
            result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
            if (!result.isSuccess()) {
               return result;
            }
            for (AttributeTypeToken attrType : sdWidget.getAttrTypes()) {
               if (atsApi.getAttributeResolver().getAttributeCount(workItem, attrType) == 0) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, "[%s] is required for transition to [%s]",
                     widgetDef.getName(), toStateDef.getName());
               }
            }
         }
      }
      return result;
   }
}
