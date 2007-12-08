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

import java.text.MessageFormat;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A cell editor that presents a list of items in a combo box. The cell editor's value is the zero-based index of the
 * selected item.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class SearchCellEditor extends CellEditor {

   /**
    * The zero-based index of the selected item.
    */
   int selection;
   int maxDisplayed = 5;

   /**
    * The custom combo box control.
    */
   SearchCCombo comboBox;

   private Search searchControl;

   /**
    * Default SearchCellEditor style
    */
   private static final int defaultStyle = SWT.NONE;

   public SearchCellEditor() {
      setStyle(defaultStyle);
   }

   /**
    * Creates a new cell editor with a combo containing the given list of choices and parented under the given control.
    * The cell editor value is the zero-based index of the selected item. Initially, the cell editor has no cell
    * validator and the first item in the list is selected.
    * 
    * @param parent the parent control
    * @param items the list of strings for the combo box
    */
   public SearchCellEditor(Composite parent, String[] items) {
      this(parent, items, defaultStyle);
   }

   /**
    * Creates a new cell editor with a combo containing the given list of choices and parented under the given control.
    * The cell editor value is the zero-based index of the selected item. Initially, the cell editor has no cell
    * validator and the first item in the list is selected.
    * 
    * @param parent the parent control
    * @param items the list of strings for the combo box
    * @param style the style bits
    * @since 2.1
    */
   public SearchCellEditor(Composite parent, String[] items, int style) {
      super(parent, style);
      searchControl = new Search(items);
      setItems(items, true);
   }

   /**
    * Returns the list of choices for the combo box
    * 
    * @return the list of choices for the combo box
    */
   public String[] getItems() {
      return searchControl.getItems();
   }

   /**
    * Sets the list of choices for the combo box
    * 
    * @param items the list of choices for the combo box
    */
   public void setItems(String[] items, boolean reset) {
      Assert.isNotNull(items);
      populateComboBoxItems(items, reset);
   }

   /**
    * Sets the number of choices displayed
    * 
    * @param count - number of items to display in list
    */
   public void setVisibleItemCount(int count) {
      comboBox.setVisibleItemCount(count);
   }

   /**
    * Sets the number of choices displayed up to maxDisplayed
    */
   public void setVisibleItemCount() {
      if (comboBox.getItemCount() > maxDisplayed)
         this.setVisibleItemCount(maxDisplayed);
      else
         this.setVisibleItemCount(comboBox.getItemCount());
      comboBox.redraw();
   }

   /**
    * Sets the maximum number of items to display
    * 
    * @param max - maximum number of items to display
    */
   public void setMaxDisplayed(int max) {
      maxDisplayed = max;
      this.setVisibleItemCount();
   }

   /**
    * Gets the maximum number of items to display
    * 
    * @return number of items to display in list
    */
   public int getMaxDisplayed() {
      return maxDisplayed;
   }

   /* (non-Javadoc)
    * Method declared on CellEditor.
    */
   protected Control createControl(Composite parent) {

      comboBox = new SearchCCombo(parent, getStyle());
      comboBox.setFont(parent.getFont());

      comboBox.addKeyListener(new KeyAdapter() {
         // hook key pressed - see PR 14201  
         public void keyPressed(KeyEvent e) {
            keyReleaseOccured(e);
         }

         public void keyReleased(KeyEvent e) {
            comboBox.setSelection(0, searchControl.getLength());
         }
      });

      comboBox.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent event) {
            applyEditorValueAndDeactivate();
         }

         public void widgetSelected(SelectionEvent event) {
            selection = comboBox.getSelectionIndex();
         }
      });

      comboBox.addTraverseListener(new TraverseListener() {
         public void keyTraversed(TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
               e.doit = false;
            }
         }
      });

      comboBox.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            SearchCellEditor.this.focusLost();
         }
      });

      return comboBox;
   }

   /**
    * The <code>SearchCellEditor</code> implementation of this <code>CellEditor</code> framework method returns the
    * zero-based index of the current selection.
    * 
    * @return the zero-based index of the current selection wrapped as an <code>Integer</code>
    */
   protected Object doGetValue() {
      if (searchControl.getFirstIndex() == -1)
         return new Integer(selection);
      else
         return new Integer(selection + searchControl.getFirstIndex());
   }

   /* (non-Javadoc)
    * Method declared on CellEditor.
    */
   protected void doSetFocus() {
      comboBox.setFocus();
   }

   /**
    * The <code>SearchCellEditor</code> implementation of this <code>CellEditor</code> framework method sets the
    * minimum width of the cell. The minimum width is 10 characters if <code>comboBox</code> is not <code>null</code>
    * or <code>disposed</code> eles it is 60 pixels to make sure the arrow button and some text is visible. The list
    * of CCombo will be wide enough to show its longest item.
    */
   public LayoutData getLayoutData() {
      LayoutData layoutData = super.getLayoutData();
      if ((comboBox == null) || comboBox.isDisposed())
         layoutData.minimumWidth = 60;
      else {
         // make the comboBox 10 characters wide
         GC gc = new GC(comboBox);
         layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
         gc.dispose();
      }
      return layoutData;
   }

   /**
    * The <code>SearchCellEditor</code> implementation of this <code>CellEditor</code> framework method accepts a
    * zero-based index of a selection.
    * 
    * @param value the zero-based index of the selection wrapped as an <code>Integer</code>
    */
   protected void doSetValue(Object value) {
      Assert.isTrue(comboBox != null && ((value instanceof Integer) || (value instanceof String)));
      if (value instanceof Integer) {
         selection = ((Integer) value).intValue();
         comboBox.select(selection);
      } else if (value instanceof String) {
         comboBox.setText((String) value);
      }
   }

   /**
    * Updates the list of choices for the combo box for the current control.
    */
   private void populateComboBoxItems(String[] items, boolean reset) {
      if (comboBox != null && items != null) {
         comboBox.removeAll();
         for (int i = 0; i < items.length; i++)
            comboBox.add(items[i], i);

         setValueValid(true);
         this.setVisibleItemCount();
         if (reset) selection = 0;
      }
   }

   /**
    * Applies the currently selected value and deactiavates the cell editor
    */
   void applyEditorValueAndDeactivate() {
      //  must set the selection before getting value
      selection = comboBox.getSelectionIndex();
      Object newValue = doGetValue();
      markDirty();
      boolean isValid = isCorrect(newValue);
      setValueValid(isValid);
      if (!isValid) {
         // try to insert the current value into the error message.
         setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] {searchControl.getItems()[selection]}));
      }
      fireApplyEditorValue();
      deactivate();
   }

   /*
    *  (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#focusLost()
    */
   protected void focusLost() {
      if (isActivated()) {
         applyEditorValueAndDeactivate();
      }
      selection = (Integer) this.doGetValue();
      searchControl.reset();
      setItems(searchControl.getItems(), false);
   }

   /*
    *  (non-Javadoc)
    * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
    */
   protected void keyReleaseOccured(KeyEvent keyEvent) {
      if (keyEvent.character == '\u001b') { // Escape character
         fireCancelEditor();
      } else if (keyEvent.character == '\t') { // tab key
         applyEditorValueAndDeactivate();
      } else if (keyEvent.character != 0x00) {
         searchControl.progressiveSearch(keyEvent);
         if (searchControl.getDirty()) {
            setItems(searchControl.getItems(), true);
            comboBox.select(selection);
            comboBox.updateWidget();
            comboBox.setSelection(0, searchControl.getLength());
            searchControl.setDirty(false);
         }
      }
   }
}