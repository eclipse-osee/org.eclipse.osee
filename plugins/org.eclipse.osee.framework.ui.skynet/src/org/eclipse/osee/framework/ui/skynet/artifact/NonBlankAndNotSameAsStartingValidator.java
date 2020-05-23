/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class NonBlankAndNotSameAsStartingValidator implements IInputValidator {
   private final String startingName;

   public NonBlankAndNotSameAsStartingValidator(String startingName) {
      this.startingName = startingName;
   }

   @Override
   public String isValid(String newText) {
      String errorMessage = null;
      if (!Strings.isValid(newText)) {
         errorMessage = "The new name cannot be blank";
      } else if (Strings.isValid(startingName) && startingName.equals(newText)) {
         errorMessage = "The new name must be different";
      }
      return errorMessage;
   }
}