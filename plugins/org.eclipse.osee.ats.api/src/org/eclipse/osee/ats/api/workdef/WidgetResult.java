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

import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class WidgetResult implements ITransitionResult {

   private final WidgetStatus status;
   private final String message;
   public static WidgetResult Valid = new WidgetResult(WidgetStatus.Valid, "");
   private String exception = "";

   public WidgetResult(WidgetStatus status, String format, Object... object) {
      this(status, null, format, object);
   }

   public WidgetResult(WidgetStatus status, Exception exception, String format, Object... objects) {
      this.status = status;
      if (exception != null) {
         this.exception = Lib.exceptionToString(exception);
      }
      this.message = String.format(format, objects);
   }

   public boolean isValid() {
      return status.isValid();
   }

   public WidgetStatus getStatus() {
      return status;
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
      return String.format("%s - %s", status, message);
   }

   @Override
   public String getException() {
      return exception;
   }
}
