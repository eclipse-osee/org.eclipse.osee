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

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsXWidgetValidator implements IAtsXWidgetValidator {

   public boolean isTransitionToComplete(StateDefinition toStateDef) {
      return toStateDef.isCompleted();
   }

   public boolean isRequiredForTransition(WidgetDefinition widgetDef) {
      return widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_TRANSITION);
   }

   public boolean isRequiredForCompletion(WidgetDefinition widgetDef) {
      return widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_COMPLETION);
   }

   public boolean isEmptyValue(IValueProvider provider) {
      return provider.isEmpty();
   }

   public WidgetResult validateWidgetIsRequired(IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef) {
      if (isRequiredForTransition(widgetDef) && isEmptyValue(provider)) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, "[%s] is required for transition",
            widgetDef.getName());
      } else if (isTransitionToComplete(toStateDef) && isRequiredForCompletion(widgetDef) && isEmptyValue(provider)) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, "[%s] is required for transition to [%s]",
            widgetDef.getName(), toStateDef.getName());
      }
      return WidgetResult.Success;
   }

   @Override
   public abstract WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider valueProvider,
      WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi);

   public WidgetResult isValidDate(IValueProvider valueProvider, WidgetDefinition widgetDef) {
      if (valueProvider.getDateValues() == null) {
         return new WidgetResult(WidgetStatus.Exception, "Date Values is null");

      }
      for (Date date : valueProvider.getDateValues()) {
         if (widgetDef.is(WidgetOption.FUTURE_DATE_REQUIRED)) {
            if (date.before(new Date())) {
               return new WidgetResult(WidgetStatus.Invalid_Range, "[%s] value [%s] must be in future",
                  valueProvider.getName(), DateUtil.get(date, DateUtil.MMDDYYHHMM));
            }
         }
      }
      return WidgetResult.Success;
   }

   public WidgetResult isValidFloat(IValueProvider valueProvider, WidgetDefinition widgetDef) {
      for (Object obj : valueProvider.getValues()) {
         if (obj instanceof Double) {
            return WidgetResult.Success;
         }
         if (obj instanceof String) {
            String attrStr = (String) obj;
            if (attrStr.matches("[-+]?\\d*\\.?\\d*")) {
               WidgetResult result = checkValid(widgetDef, Double.parseDouble(attrStr), valueProvider.getName());
               if (!result.isSuccess()) {
                  return result;
               }
            } else {
               return new WidgetResult(WidgetStatus.Invalid_Type, "[%s] value [%s] is not a valid float",
                  valueProvider.getName(), attrStr);
            }
         }
      }
      return WidgetResult.Success;
   }

   public WidgetResult isValidInteger(IValueProvider valueProvider, WidgetDefinition widgetDef) {
      for (Object obj : valueProvider.getValues()) {
         if (obj instanceof Integer) {
            return WidgetResult.Success;
         }
         if (obj instanceof String) {
            String attrStr = (String) obj;
            if (Strings.isValid(attrStr)) {
               WidgetResult result = checkValid(widgetDef, Integer.parseInt(attrStr), valueProvider.getName());
               if (!result.isSuccess()) {
                  return result;
               }
            } else {
               return new WidgetResult(WidgetStatus.Invalid_Type, "[%s] value [%s] is not a valid integer",
                  valueProvider.getName(), attrStr);
            }
         }
      }
      return WidgetResult.Success;
   }

   private WidgetResult checkValid(WidgetDefinition widgetDef, double value, String valueProviderName) {
      Double minValue = widgetDef.getMin();
      Double maxValue = widgetDef.getMax();

      if (minValue != null && Lib.lessThan(value, minValue)) {
         return new WidgetResult(WidgetStatus.Invalid_Range, "[%s] value [%s] must be >= [%s]", valueProviderName,
            value, minValue);
      } else if (maxValue != null && Lib.greaterThan(value, maxValue)) {
         return new WidgetResult(WidgetStatus.Invalid_Range, "[%s] value [%s] must be <= [%s]", valueProviderName,
            value, minValue, maxValue);
      }

      return WidgetResult.Success;
   }

   public WidgetResult isValidList(IValueProvider valueProvider, WidgetDefinition widgetDef) {
      return checkValid(widgetDef, valueProvider.getValues().size(), valueProvider.getName());
   }

}
