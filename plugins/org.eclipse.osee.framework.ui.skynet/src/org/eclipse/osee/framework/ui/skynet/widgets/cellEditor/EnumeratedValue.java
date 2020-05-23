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

package org.eclipse.osee.framework.ui.skynet.widgets.cellEditor;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedValue extends UniversalCellEditorValue {
   private String value;
   private String[] choices;

   public EnumeratedValue() {
      super();
   }

   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      Combo comboBox = universalEditor.getEnumeratedControl();
      comboBox.setItems(choices);
      if (value != null) {
         comboBox.setText(value);
      }

      comboBox.setVisibleItemCount(15);
      return comboBox;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setChoices(String[] choices) {
      this.choices = choices;
   }
}