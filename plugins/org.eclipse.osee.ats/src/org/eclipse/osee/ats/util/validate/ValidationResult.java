/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import java.util.Collection;

/**
 * @author Shawn F. Cook
 */
public class ValidationResult {
   private final Collection<String> errorMessages;
   private final boolean validationPassed;

   public ValidationResult(Collection<String> errorMessages, boolean validationPassed) {
      this.errorMessages = errorMessages;
      this.validationPassed = validationPassed;
   }

   public Collection<String> getErrorMessages() {
      return errorMessages;
   }

   public boolean didValidationPass() {
      return validationPassed;
   }
}
