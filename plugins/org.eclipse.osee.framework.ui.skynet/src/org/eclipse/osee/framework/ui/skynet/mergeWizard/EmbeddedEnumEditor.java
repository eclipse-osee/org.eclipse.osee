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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Theron Virgin
 */
public class EmbeddedEnumEditor {

   private final String dialogMessage;
   private Combo comboBox;
   public final static String NO_SELECTION = "";

   public EmbeddedEnumEditor(String dialogMessage) {
      this.dialogMessage = dialogMessage;
   }

   public boolean createEditor(Composite composite) {
      new Label(composite, SWT.NONE).setText(dialogMessage);

      comboBox = new Combo(composite, SWT.READ_ONLY);
      comboBox.setBounds(50, 50, 650, 65);

      comboBox.add(NO_SELECTION);

      return true;
   }

   public void addSelectionChoice(String choice) {
      if (choice != null) {
         comboBox.add(choice);
      }
   }

   public String getSelected() {
      int index = comboBox.getSelectionIndex();
      if (index == -1) {
         return NO_SELECTION;
      } else {
         return comboBox.getItem(index);
      }
   }

   public void setSelected(String selection) {
      int index = comboBox.indexOf(selection);
      if (index == -1) {
         AWorkbench.popup("ERROR", "Attempting to set Enumeration to invalid value " + selection);
      }
      comboBox.select(index);
   }

}
