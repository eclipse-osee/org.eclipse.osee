/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
