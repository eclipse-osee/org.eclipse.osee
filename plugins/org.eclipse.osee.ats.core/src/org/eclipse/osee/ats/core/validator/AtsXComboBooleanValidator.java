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

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidator extends AtsXWidgetValidator {

   public interface IAllowedBooleanValueProvider {
      Collection<String> getValues() throws OseeCoreException;
   }

   private final IAllowedBooleanValueProvider provider;

   public AtsXComboBooleanValidator(IAllowedBooleanValueProvider provider) {
      this.provider = provider;
   }

   @Override
   public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) throws OseeCoreException {
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

   private boolean isValid(String value) throws OseeCoreException {
      Collection<String> allowed = provider.getValues();
      return allowed.contains(value);
   }
}
