/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.validator.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public enum UserRoleError {

   None("", WidgetStatus.Valid),
   OneRoleEntryRequired("At least one role entry is required.", WidgetStatus.Invalid_Incompleted),
   ExceptionValidatingRoles("Exception validating roles. See log for details.", WidgetStatus.Exception),
   MustHaveAtLeastOneAuthor("Must have at least one Author", WidgetStatus.Invalid_Incompleted),
   MustHaveAtLeastOneReviewer("Must have at least one Reviewer", WidgetStatus.Invalid_Incompleted),
   HoursSpentMustBeEnteredForEachRole("Hours spent must be entered for each role.", WidgetStatus.Invalid_Incompleted);

   private final String error;
   private final WidgetStatus widgetStatus;

   private UserRoleError(String error, WidgetStatus widgetStatus) {
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
