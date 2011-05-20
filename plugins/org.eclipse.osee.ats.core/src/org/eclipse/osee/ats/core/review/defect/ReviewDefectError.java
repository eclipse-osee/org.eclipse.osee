/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.defect;

import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.validator.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public enum ReviewDefectError {

   None("", WidgetStatus.Valid),
   ExceptionValidatingRoles("Exception validating defects. See log for details.", WidgetStatus.Exception),
   AllItemsMustBeMarkedAndClosed("All items must be marked for severity, disposition and closed.", WidgetStatus.Invalid_Incompleted);

   private final String error;
   private final WidgetStatus widgetStatus;

   private ReviewDefectError(String error, WidgetStatus widgetStatus) {
      this.error = error;
      this.widgetStatus = widgetStatus;
   }

   public String getError() {
      return error;
   }

   public boolean isOK() {
      return this == None;
   }

   public WidgetResult toWidgetResult(WidgetDefinition widgetDef) {
      if (this == None) {
         return WidgetResult.Valid;
      }
      return new WidgetResult(widgetStatus, widgetDef, getError());
   }
}
