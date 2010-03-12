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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;

/**
 * @author Robert A. Fisher
 */
public class DialogUtil {

   /**
    * Applies the status to a dialog page
    */
   public static void applyToStatusLine(DialogPage page, IStatus status) {
      String errorMessage = null;
      String warningMessage = null;
      String statusMessage = status.getMessage();
      if (statusMessage.length() > 0) {
         if (status.matches(IStatus.ERROR)) {
            errorMessage = statusMessage;
         } else if (!status.isOK()) {
            warningMessage = statusMessage;
         }
      }
      page.setErrorMessage(errorMessage);
      page.setMessage(warningMessage);
   }
}
