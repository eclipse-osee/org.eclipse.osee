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

package org.eclipse.osee.framework.ui.plugin.util;

public class DialogSelectionHelper implements Runnable {

   private int selection = -1;
   private boolean saveSelection;
   private final Object[] availableSelections;

   public DialogSelectionHelper(Object[] availableSelections) {
      this.availableSelections = availableSelections;
   }

   public DialogSelectionHelper(Object[] availableSelections, String msg) {
      this.availableSelections = availableSelections;
   }

   @Override
   public void run() {
      ListSelectionDialog dlg = new ListSelectionDialog(availableSelections, null, "File Selection", null,
         "String dialogMessage", 3, new String[] {"OK", "Cancel"}, 0);

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
