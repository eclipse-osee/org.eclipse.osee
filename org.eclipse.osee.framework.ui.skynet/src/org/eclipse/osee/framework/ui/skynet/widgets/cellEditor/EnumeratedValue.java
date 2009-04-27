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
package org.eclipse.osee.framework.ui.skynet.widgets.cellEditor;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedValue extends UniversalCellEditorValue {
   private String value;
   private String[] choices;

   /**
    * 
    */
   public EnumeratedValue() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.swt.universalCellEditor.UniversalCellEditorValue#selectControl(org.eclipse.swt.widgets.Control[])
    */
   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      Combo comboBox = universalEditor.getEnumeratedControl();
      comboBox.setItems(choices);
      if (value != null) comboBox.setText(value);

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