/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author Ryan D. Brooks
 */
public class NonBlankValidator implements IInputValidator {
   private String blankErrorMessage;

   /**
    * @param blankErrorMessage
    */
   public NonBlankValidator(String blankErrorMessage) {
      this.blankErrorMessage = blankErrorMessage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
    */
   public String isValid(String newText) {
      if (newText == null || newText.length() == 0) {
         return blankErrorMessage;
      }
      return null; // to indicate the input is valid
   }
}