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

import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class WidgetResult extends TransitionResult {

   private WidgetStatus status;
   private String message;
   public static WidgetResult Success = new WidgetResult(WidgetStatus.Success, "");
   private String exception = "";

   public WidgetResult() {
      this(WidgetStatus.Success, "");
   }

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

   public boolean isSuccess() {
      return status.isSuccess();
   }

   public WidgetStatus getStatus() {
      return status;
   }

   @Override
   public String getDetails() {
      if (this == Success) {
         return "Success";
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

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void setStatus(WidgetStatus status) {
      this.status = status;
   }

   @Override
   public void setException(String exception) {
      this.exception = exception;
   }
}
