/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXUserRoleValidator extends AtsXWidgetValidator {

   public static String WIDGET_NAME = "XUserRoleViewer";

   @Override
   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      WidgetResult result = WidgetResult.Valid;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         // ReviewDefectValidation converted to provider IValueProvider
         UserRoleManager mgr = new UserRoleManager(provider);
         UserRoleError error = UserRoleValidator.isValid(mgr, fromStateDef, toStateDef);
         return error.toWidgetResult(widgetDef);
      }
      return result;
   }

}
