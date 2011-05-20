/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workflow.transition.ITransitionResult;

/**
 * @author Donald G. Dunne
 */
public class WidgetResult implements ITransitionResult {

   private final WidgetStatus status;
   private final WidgetDefinition widgetDef;
   private final String message;
   public static WidgetResult Valid = new WidgetResult(WidgetStatus.Valid, null, "");
   private final Exception exception;

   public WidgetResult(WidgetStatus status, WidgetDefinition widgetDef, String message) {
      this(status, widgetDef, null, message);
   }

   public WidgetResult(WidgetStatus status, WidgetDefinition widgetDef, String format, Object... object) {
      this(status, widgetDef, null, format, object);
   }

   public WidgetResult(WidgetStatus status, WidgetDefinition widgetDef, Exception exception, String message) {
      this.status = status;
      this.widgetDef = widgetDef;
      this.exception = exception;
      this.message = message;
   }

   public WidgetResult(WidgetStatus status, WidgetDefinition widgetDef, Exception exception, String format, Object... object) {
      this(status, widgetDef, exception, String.format(format, object));
   }

   public boolean isValid() {
      return status.isValid();
   }

   public WidgetStatus getStatus() {
      return status;
   }

   public WidgetDefinition getWidgetDef() {
      return widgetDef;
   }

   @Override
   public String getDetails() {
      if (this == Valid) {
         return "Valid";
      }
      return message;
   }

   @Override
   public String toString() {
      return String.format("%s - %s - %s", status, widgetDef.getName(), message);
   }

   @Override
   public Exception getException() {
      return exception;
   }
}
