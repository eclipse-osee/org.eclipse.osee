/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.review;

import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class UserRoleError extends OseeEnum {

   private static final Long ENUM_ID = 999223411L;

   // @formatter:off
   public static final UserRoleError None = new UserRoleError("None", "", WidgetStatus.Success);
   public static final UserRoleError ExceptionValidatingRoles = new UserRoleError("ExceptionValidatingRoles", "Exception validating roles. See log for details.", WidgetStatus.Exception);
   public static final UserRoleError HoursSpentMustBeEnteredForEachRole = new UserRoleError("HoursSpentMustBeEnteredForEachRole", "Hours spent must be entered for each role.", WidgetStatus.Invalid_Incompleted);
   public static final UserRoleError MustMeetMinimumRole = new UserRoleError("MustMeetMinimumRole", "Must meet Minimum Role", WidgetStatus.Invalid_Incompleted);
   // @formatter:on

   private final String error;
   private final WidgetStatus widgetStatus;

   public UserRoleError() {
      this("", "", WidgetStatus.Success);
   }

   public UserRoleError(String name, String error, WidgetStatus widgetStatus) {
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

   public WidgetResult toWidgetResult(WidgetDefinition widgetDef) {
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
