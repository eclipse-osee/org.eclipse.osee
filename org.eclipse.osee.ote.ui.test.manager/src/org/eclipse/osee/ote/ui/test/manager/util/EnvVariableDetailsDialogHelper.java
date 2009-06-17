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
package org.eclipse.osee.ote.ui.test.manager.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

/**
 * @author Roberto E. Escobar
 */
public class EnvVariableDetailsDialogHelper implements Runnable {

   private String nameOfVariableToSet;
   private int result;
   private String selection;

   public EnvVariableDetailsDialogHelper(String nameOfVariableToSet, String oldValue) {
      this.nameOfVariableToSet = nameOfVariableToSet;
      this.selection = oldValue;
   }

   public int getResult() {
      return result;
   }

   public String getSelection() {
      return selection;
   }

   public void run() {
      EnvVariableDetailsDialog dlg = new EnvVariableDetailsDialog(null, "Edit " + nameOfVariableToSet, null,
            nameOfVariableToSet + " Value:", MessageDialog.NONE, new String[] {"OK", "Cancel"}, 0, selection);

      result = dlg.open();

      if (result == Window.OK) {
         if (dlg.isValid()) {
            String info = dlg.getSelection();
            if (info != null) {
               selection = info;
            }
            else {
               selection = "";
            }
         }
      }
   }
}
