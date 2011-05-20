/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      WidgetResult result = WidgetResult.Valid;
      if ("XComboBooleanDam".equals(widgetDef.getXWidgetName())) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, fromStateDef);
         if (!result.isValid()) {
            return result;
         }
         for (String value : provider.getValues()) {
            if (!"yes".equals(value) && !"no".equals(value)) {
               return new WidgetResult(WidgetStatus.Invalid_Range, widgetDef, "[%s] value [%s] must be yes or no",
                  provider.getName(), value);
            }
         }
      }
      return result;
   }
}
