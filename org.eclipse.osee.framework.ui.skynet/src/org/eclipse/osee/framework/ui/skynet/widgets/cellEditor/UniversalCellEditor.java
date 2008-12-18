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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class UniversalCellEditor extends CellEditor {
   private final Combo comboBox;
   private final Text textBox;
   private final CalendarCombo datePicker;
   private Control control;
   private boolean personCombo = false;

   /**
    * @param parent
    */
   public UniversalCellEditor(Composite parent) {
      this(parent, SWT.NONE);
   }

   /**
    * @param parent
    * @param style
    */
   public UniversalCellEditor(Composite parent, int style) {
      super(parent, style);
      this.comboBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
      this.textBox = new Text(parent, SWT.SINGLE);
      this.datePicker = new CalendarCombo(parent, SWT.BORDER);

      setValueValid(true);
   }

   public Text getStringControl() {
      return textBox;
   }

   public Combo getEnumeratedControl() {
      return comboBox;
   }

   public Combo getPersonComboControl() {
      personCombo = true;
      return comboBox;
   }

   public CalendarCombo getDateControl() {
      return datePicker;
   }

   /**
    * Since UniveralCellEditor is managing its own controls and the CellEditor's mechanisms for managing the control
    * have been completly overridden, return null back to CellEditor when it calls this method
    */
   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createControl(Composite parent) {
      return null;
   }

   /**
    * Returns whether this cell editor is activated.
    * 
    * @return <code>true</code> if this cell editor's control is currently visible, and <code>false</code> if not
    *         visible
    */
   @Override
   public boolean isActivated() {
      return control != null && control.isVisible();
   }

   /**
    * Returns the control used to implement this cell editor.
    * 
    * @return the control, or <code>null</code> if this cell editor has no control
    */
   @Override
   public Control getControl() {
      return control;
   }

   /**
    * Hides this cell editor's control. Does nothing if this cell editor is not visible.
    */
   @Override
   public void deactivate() {
      if (control != null && !control.isDisposed()) {
         control.setVisible(false);
      }
   }

   /**
    * Disposes of this cell editor and frees any associated SWT resources.
    */
   @Override
   public void dispose() {
      if (control != null && !control.isDisposed()) {
         control.dispose();
      }
      control = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
    */
   @Override
   protected Object doGetValue() {
      if ((control instanceof Combo) && personCombo) {
         return comboBox.getText();
      }
      if (control instanceof Combo) {
         return comboBox.getText();
      }
      if (control instanceof Text) {
         return textBox.getText();
      }
      if (control instanceof CalendarCombo) {
         return datePicker.getDate().getTime();
      }
      throw new IllegalArgumentException("Control was of an unexpected type: " + control.getClass().getName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
    */
   @Override
   protected void doSetFocus() {
      control.setFocus();
   }

   /**
    * called just before a cell is to be edited
    */
   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
    */
   @Override
   protected void doSetValue(Object value) {
      if (value instanceof UniversalCellEditorValue) {
         UniversalCellEditorValue editorValue = (UniversalCellEditorValue) value;
         control = editorValue.prepareControl(this);
      } else {
         throw new IllegalArgumentException("value of unexpected type: " + value.getClass().getName());
      }
   }
}
