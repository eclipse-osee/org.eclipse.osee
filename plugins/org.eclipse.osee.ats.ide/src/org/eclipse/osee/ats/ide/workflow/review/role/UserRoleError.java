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
package org.eclipse.osee.ats.ide.workflow.review.role;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class UserRoleError extends OseeEnum {

   private static final Long ENUM_ID = 999223411L;

   // @formatter:off
   public static final UserRoleError None = new UserRoleError("None", "", WidgetStatus.Success);
   public static final UserRoleError OneRoleEntryRequired = new UserRoleError("OneRoleEntryRequired", "At least one role entry is required.", WidgetStatus.Invalid_Incompleted);
   public static final UserRoleError ExceptionValidatingRoles = new UserRoleError("ExceptionValidatingRoles", "Exception validating roles. See log for details.", WidgetStatus.Exception);
   public static final UserRoleError MustHaveAtLeastOneAuthor = new UserRoleError("MustHaveAtLeastOneAuthor", "Must have at least one Author.", WidgetStatus.Invalid_Incompleted);
   public static final UserRoleError MustHaveAtLeastOneReviewer = new UserRoleError("MustHaveAtLeastOneReviewer", "Must have at least one Reviewer (a ModeratorReviewer can fulfill this requirement).", WidgetStatus.Invalid_Incompleted);
   public static final UserRoleError HoursSpentMustBeEnteredForEachRole = new UserRoleError("HoursSpentMustBeEnteredForEachRole", "Hours spent must be entered for each role.", WidgetStatus.Invalid_Incompleted);
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
