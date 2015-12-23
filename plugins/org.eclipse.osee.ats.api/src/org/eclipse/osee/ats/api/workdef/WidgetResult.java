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
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public class WidgetResult implements ITransitionResult {

   private final WidgetStatus status;
   private final IAtsWidgetDefinition widgetDef;
   private final String message;
   public static WidgetResult Valid = new WidgetResult(WidgetStatus.Valid, null, "");
   private final Exception exception;

   public WidgetResult(WidgetStatus status, IAtsWidgetDefinition widgetDef, String message) {
      this(status, widgetDef, null, message);
   }

   public WidgetResult(WidgetStatus status, IAtsWidgetDefinition widgetDef, String format, Object... object) {
      this(status, widgetDef, null, format, object);
   }

   public WidgetResult(WidgetStatus status, IAtsWidgetDefinition widgetDef, Exception exception, String message) {
      this.status = status;
      this.widgetDef = widgetDef;
      this.exception = exception;
      this.message = message;
   }

   public WidgetResult(WidgetStatus status, IAtsWidgetDefinition widgetDef, Exception exception, String format, Object... object) {
      this(status, widgetDef, exception, String.format(format, object));
   }

   public boolean isValid() {
      return status.isValid();
   }

   public WidgetStatus getStatus() {
      return status;
   }

   public IAtsWidgetDefinition getWidgetDef() {
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
