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
package org.eclipse.osee.framework.ui.plugin.util;

public class DialogSelectionHelper implements Runnable {

   private int selection = -1;
   private boolean saveSelection;
   private Object[] availableSelections;

   public DialogSelectionHelper(Object[] availableSelections) {
      this.availableSelections = availableSelections;
   }

   public DialogSelectionHelper(Object[] availableSelections, String msg) {
      this.availableSelections = availableSelections;
   }

   public void run() {
      ListSelectionDialog dlg =
            new ListSelectionDialog(availableSelections, null, "File Selection", null, "String dialogMessage", 3,
                  new String[] {"OK", "Cancel"}, 0);

      int result = dlg.open();
      if (result == 0) {
         selection = dlg.getSelection();
         saveSelection = dlg.saveSelection();
      }
   }

   public int getSelectionIndex() {
      return selection;
   }

   /**
    * @return Returns the saveSelection.
    */
   public boolean isSaveSelection() {
      return saveSelection;
   }
}
