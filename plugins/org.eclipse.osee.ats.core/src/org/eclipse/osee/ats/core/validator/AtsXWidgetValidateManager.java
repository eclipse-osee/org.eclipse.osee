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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {
   private static List<IAtsXWidgetValidator> atsValidators;
   public static AtsXWidgetValidateManager instance = new AtsXWidgetValidateManager();

   private AtsXWidgetValidateManager() {
      atsValidators = new ArrayList<IAtsXWidgetValidator>();
      atsValidators.add(new AtsXIntegerValidator());
      atsValidators.add(new AtsXFloatValidator());
      atsValidators.add(new AtsXTextValidator());
      atsValidators.add(new AtsXDateValidator());
      atsValidators.add(new AtsXComboValidator());
      atsValidators.add(new AtsXComboBooleanValidator());
      atsValidators.add(new AtsXListValidator());

   }

   public void validateTransition(List<WidgetResult> results, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) {
      for (IAtsXWidgetValidator validator : atsValidators) {
         try {
            WidgetResult status = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
            if (!status.isValid()) {
               results.add(status);
            }
         } catch (Exception ex) {
            results.add(new WidgetResult(WidgetStatus.Exception, widgetDef, ex, String.format(
               "Exception retriving validation for widget [%s] Exception [%s]", validator.getClass().getSimpleName(),
               ex.getLocalizedMessage()), ex));
            return;
         }
      }
   }

   public static void add(IAtsXWidgetValidator iAtsXWidgetValidator) {
      atsValidators.add(iAtsXWidgetValidator);
   }
}
