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

import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class PersonValue extends UniversalCellEditorValue {
   private String name;
   private String[] choices;

   /**
    * 
    */
   public PersonValue() {
      super();
      choices = SkynetAuthentication.getInstance().getUserNames();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.swt.universalCellEditor.UniversalCellEditorValue#selectControl(org.eclipse.swt.widgets.Control[])
    */
   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      Combo comboBox = universalEditor.getPersonComboControl();
      comboBox.setItems(choices);
      comboBox.setText(name);
      return comboBox;
   }

   public void setValue(String name) {
      this.name = name;
   }

}