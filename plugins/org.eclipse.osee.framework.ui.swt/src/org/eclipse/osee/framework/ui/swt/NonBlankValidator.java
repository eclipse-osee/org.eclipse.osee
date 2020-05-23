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

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author Ryan D. Brooks
 */
public class NonBlankValidator implements IInputValidator {
   private final String blankErrorMessage;

   public NonBlankValidator(String blankErrorMessage) {
      this.blankErrorMessage = blankErrorMessage;
   }

   @Override
   public String isValid(String newText) {
      if (newText == null || newText.length() == 0) {
         return blankErrorMessage;
      }
      return null; // to indicate the input is valid
   }
}