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

import java.util.Date;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionFloatMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionListMinMaxSelectedConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsXWidgetValidator implements IAtsXWidgetValidator {

   public boolean isTransitionToComplete(StateDefinition toStateDef) {
      return toStateDef.isCompletedPage();
   }

   public boolean isRequiredForTransition(WidgetDefinition widgetDef) {
      return widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_TRANSITION);
   }

   public boolean isRequiredForCompletion(WidgetDefinition widgetDef) {
      return widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_COMPLETION);
   }

   public boolean isEmptyValue(IValueProvider provider) throws OseeCoreException {
      return provider.isEmpty();
   }

   public WidgetResult validateWidgetIsRequired(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      if (isRequiredForTransition(widgetDef) && isEmptyValue(provider)) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef, "[%s] is required for transition",
            widgetDef.getName());
      } else if (isTransitionToComplete(toStateDef) && isRequiredForCompletion(widgetDef) && isEmptyValue(provider)) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
            "[%s] is required for transition to [%s]", widgetDef.getName(), toStateDef.getName());
      }
      return WidgetResult.Valid;
   }

   @Override
   public abstract WidgetResult validateTransition(IValueProvider valueProvider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException;

   @SuppressWarnings("unchecked")
   public <A> A getConstraintOfType(WidgetDefinition widgetDef, Class<A> clazz) {
      for (WidgetConstraint constraint : widgetDef.getConstraints()) {
         if (clazz.isInstance(constraint)) {
            return (A) constraint;
         }
      }
      return null;
   }

   public WidgetResult isValidDate(IValueProvider valueProvider, WidgetDefinition widgetDef) throws OseeCoreException {
      for (Date date : valueProvider.getDateValues()) {
         if (widgetDef.is(WidgetOption.FUTURE_DATE_REQUIRED)) {
            if (date.before(new Date())) {
               return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%s] must be in future",
                  valueProvider.getName(), DateUtil.get(date, DateUtil.MMDDYYHHMM));
            }
         }
      }
      return WidgetResult.Valid;
   }

   public WidgetResult isValidInteger(IValueProvider valueProvider, WidgetDefinition widgetDef) throws OseeCoreException {
      for (String attrStr : valueProvider.getValues()) {
         if (!isInteger(attrStr)) {
            return new WidgetResult(WidgetStatus.Invalid_Type, widgetDef, "[%s] value [%s] is not a valid integer",
               valueProvider.getName(), attrStr);
         }
         Integer value = getInteger(attrStr);

         Integer minValue = getIntMinValueSet(widgetDef);
         Integer maxValue = getIntMaxValueSet(widgetDef);
         if (minValue != null && value < minValue) {
            return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%d] must be >= [%d]",
               valueProvider.getName(), value, minValue);
         } else if (maxValue != null && value > maxValue) {
            return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%d] must be < [%d]",
               valueProvider.getName(), value, maxValue);
         }
      }
      return WidgetResult.Valid;
   }

   public boolean isInteger(String text) {
      return getInteger(text) != null;
   }

   public Integer getInteger(String text) {
      try {
         return new Integer(text);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   public WidgetResult isValidFloat(IValueProvider valueProvider, WidgetDefinition widgetDef) throws OseeCoreException {
      for (String attrStr : valueProvider.getValues()) {
         if (!isFloat(attrStr)) {
            return new WidgetResult(WidgetStatus.Invalid_Type, widgetDef, "[%s] value [%s] is not a valid float",
               valueProvider.getName(), attrStr);
         }
         Double value = getFloat(attrStr);

         Double minValue = getFloatMinValueSet(widgetDef);
         Double maxValue = getFloatMaxValueSet(widgetDef);
         if (minValue != null && value < minValue) {
            return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%f] must be >= [%f]",
               valueProvider.getName(), value, minValue);
         } else if (maxValue != null && value > maxValue) {
            return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%f] must be < [%f]",
               valueProvider.getName(), value, maxValue);
         }
      }
      return WidgetResult.Valid;
   }

   public boolean isFloat(String text) {
      return getFloat(text) != null;
   }

   public Double getFloat(String text) {
      try {
         return new Double(text);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   public Integer getIntMinValueSet(WidgetDefinition widgetDef) {
      WidgetDefinitionIntMinMaxConstraint intCon =
         getConstraintOfType(widgetDef, WidgetDefinitionIntMinMaxConstraint.class);
      if (intCon != null) {
         return intCon.getMinValue();
      }
      return null;
   }

   public Integer getIntMaxValueSet(WidgetDefinition widgetDef) {
      WidgetDefinitionIntMinMaxConstraint intCon =
         getConstraintOfType(widgetDef, WidgetDefinitionIntMinMaxConstraint.class);
      if (intCon != null) {
         return intCon.getMaxValue();
      }
      return null;
   }

   public Double getFloatMinValueSet(WidgetDefinition widgetDef) {
      WidgetDefinitionFloatMinMaxConstraint floatCon =
         getConstraintOfType(widgetDef, WidgetDefinitionFloatMinMaxConstraint.class);
      if (floatCon != null) {
         return floatCon.getMinValue();
      }
      return null;
   }

   public Double getFloatMaxValueSet(WidgetDefinition widgetDef) {
      WidgetDefinitionFloatMinMaxConstraint floatCon =
         getConstraintOfType(widgetDef, WidgetDefinitionFloatMinMaxConstraint.class);
      if (floatCon != null) {
         return floatCon.getMaxValue();
      }
      return null;
   }

   public Integer getListMinSelected(WidgetDefinition widgetDef) {
      WidgetDefinitionListMinMaxSelectedConstraint intCon =
         getConstraintOfType(widgetDef, WidgetDefinitionListMinMaxSelectedConstraint.class);
      if (intCon != null) {
         return intCon.getMinSelected();
      }
      return null;
   }

   public Integer getListMaxSelected(WidgetDefinition widgetDef) {
      WidgetDefinitionListMinMaxSelectedConstraint intCon =
         getConstraintOfType(widgetDef, WidgetDefinitionListMinMaxSelectedConstraint.class);
      if (intCon != null) {
         return intCon.getMaxSelected();
      }
      return null;
   }

   public WidgetResult isValidList(IValueProvider valueProvider, WidgetDefinition widgetDef) throws OseeCoreException {
      int selected = valueProvider.getValues().size();

      Integer minSelected = getListMinSelected(widgetDef);
      Integer maxSelected = getListMaxSelected(widgetDef);
      if (minSelected != null && selected < minSelected) {
         return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] values selected [%d] must be >= [%d]",
            valueProvider.getName(), selected, minSelected);
      } else if (maxSelected != null && selected > maxSelected) {
         return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] values selected [%d] must be < [%d]",
            valueProvider.getName(), selected, maxSelected);
      }
      return WidgetResult.Valid;
   }

}
