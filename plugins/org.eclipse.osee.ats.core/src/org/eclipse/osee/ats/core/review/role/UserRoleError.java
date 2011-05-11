/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

/**
 * @author Donald G. Dunne
 */
public enum UserRoleError {

   None(""),
   OneRoleEntryRequired("At least one role entry is required."),
   ExceptionValidatingRoles("Exception validating roles. See log for details."),
   MustHaveAtLeastOneAuthor("Must have at least one Author"),
   MustHaveAtLeastOneReviewer("Must have at least one Reviewer"),
   HoursSpentMustBeEnteredForEachRole("Hours spent must be entered for each role.");

   private final String error;

   private UserRoleError(String error) {
      this.error = error;
   }

   public String getError() {
      return error;
   }

   public boolean isOK() {
      return this == None;
   }
}
