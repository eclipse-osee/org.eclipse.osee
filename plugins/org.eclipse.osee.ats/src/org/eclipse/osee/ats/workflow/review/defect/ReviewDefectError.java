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
package org.eclipse.osee.ats.workflow.review.defect;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;

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

   public WidgetResult toWidgetResult(IAtsWidgetDefinition widgetDef) {
      if (this == None) {
         return WidgetResult.Valid;
      }
      return new WidgetResult(widgetStatus, widgetDef, getError());
   }
}
