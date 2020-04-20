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
package org.eclipse.osee.ats.core.review;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectError extends OseeEnum {

   private static final Long ENUM_ID = 345345222L;

   // @formatter:off
   public static final ReviewDefectError None = new ReviewDefectError("None", "", WidgetStatus.Success);
   public static final ReviewDefectError ExceptionValidatingRoles = new ReviewDefectError("ExceptionValidatingRoles", "Exception validating defects. See log for details.", WidgetStatus.Exception);
   public static final ReviewDefectError AllItemsMustBeMarkedAndClosed = new ReviewDefectError("AllItemsMustBeMarkedAndClosed", "All items must be marked for severity, disposition and closed.", WidgetStatus.Invalid_Incompleted);
   // @formatter:on

   private final String error;
   private final WidgetStatus widgetStatus;

   public ReviewDefectError() {
      this("", "", WidgetStatus.Success);
   }

   public ReviewDefectError(String name, String error, WidgetStatus widgetStatus) {
      super(ENUM_ID, name);
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
         return WidgetResult.Success;
      }
      return new WidgetResult(widgetStatus, getError());
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @Override
   public OseeEnum getDefault() {
      return None;
   }
}
