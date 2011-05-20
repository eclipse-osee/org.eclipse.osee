/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.defect;

import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXDefectValidator extends AtsXWidgetValidator {

   public static String WIDGET_NAME = "XDefectViewer";

   @Override
   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      WidgetResult result = WidgetResult.Valid;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         ReviewDefectManager mgr = new ReviewDefectManager(provider);
         ReviewDefectError error = ReviewDefectValidator.isValid(mgr.getDefectItems());
         return error.toWidgetResult(widgetDef);
      }
      return result;
   }
}
