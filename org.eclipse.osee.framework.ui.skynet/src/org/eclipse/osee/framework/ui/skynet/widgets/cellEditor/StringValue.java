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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class StringValue extends UniversalCellEditorValue {
   private String value;

   /**
    * 
    */
   public StringValue() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.swt.universalCellEditor.UniversalCellEditorValue#selectControl(org.eclipse.swt.widgets.Control[])
    */
   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      Text textBox = universalEditor.getStringControl();
      textBox.setText(value);
      return textBox;
   }

   public void setValue(String value) {
      this.value = value;
   }
}