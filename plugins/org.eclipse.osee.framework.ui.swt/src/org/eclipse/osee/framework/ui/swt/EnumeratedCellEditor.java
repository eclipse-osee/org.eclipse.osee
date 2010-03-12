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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Edits column data in a table by using a combo control. The options in the combo box are enumeration constants that
 * this is parameterized with.
 * 
 * @author Ken J. Aguilar
 */
public abstract class EnumeratedCellEditor<T extends Enum<T>> extends CustomTableCellEditor<T> {
   private final TableEditor editor;
   private final Table table;
   private final int columnIndex;
   private final int comboStyle;
   private final String toolTip;
   private final Class<T> clazz;
   private final String[] comboItems;
   private boolean enabled;
   private Color color;
   private final class EditCellListener implements Listener {

      public void handleEvent(Event event) {
         final Point pt = new Point(event.x, event.y);
         int index = table.getTopIndex();
         while (index < table.getItemCount()) {
            final int rowIndex = index;
            final TableItem item = table.getItem(index);
            final Rectangle rect = item.getBounds(columnIndex);
            if (rect.contains(pt)) {
               final T oldValue = clazz.getEnumConstants()[getIndexByText(item.getText(columnIndex))];
               final CCombo combo = new CCombo(table, comboStyle);
               combo.setBackground(color);
               combo.setItems(comboItems);
               combo.setToolTipText(toolTip);
               final Listener textListener = new Listener() {
                  public void handleEvent(final Event e) {
                     final T value = clazz.getEnumConstants()[combo.getSelectionIndex()];
                     switch (e.type) {
                        case SWT.FocusOut:
                           item.setText(columnIndex, focusLost(rowIndex, value, oldValue).toString());
                           combo.dispose();
                           break;
                        case SWT.Traverse:
                           switch (e.detail) {
                              case SWT.TRAVERSE_RETURN:
                                 item.setText(columnIndex, applyValue(rowIndex, value, oldValue).toString());
                                 // FALL THROUGH
                              case SWT.TRAVERSE_ESCAPE:
                                 combo.dispose();
                                 e.doit = false;
                           }
                           break;
                        case SWT.Selection:
                           item.setText(columnIndex, applyValue(rowIndex, value, oldValue).toString());
                           combo.dispose();
                           break;
                     }
                  }
               };
               combo.addListener(SWT.FocusOut, textListener);
               combo.addListener(SWT.Traverse, textListener);
               combo.addListener(SWT.Selection, textListener);
               combo.select(oldValue.ordinal());
               editor.setEditor(combo, item, columnIndex);
               combo.setFocus();
               return;
            }
            index++;
         }
      }
   }

   private final EditCellListener tblListener = new EditCellListener();

   /**
    * Constructs a new Combo Cell Editor
    * 
    * @param table
    * @param columnIndex
    * @param comboStyle
    * @param toolTip
    * @param clazz the Class of the <CODE>Enum</CODE> whose enumerations will appear as options in the cell editor's
    *           combo box
    */
   public EnumeratedCellEditor(final Table table, final int columnIndex, final int comboStyle, final String toolTip, final Class<T> clazz) {
      this.table = table;
      this.columnIndex = columnIndex;
      this.comboStyle = comboStyle;
      this.toolTip = toolTip;
      this.clazz = clazz;
      this.enabled = true;

      comboItems = new String[clazz.getEnumConstants().length];
      for (int i = 0; i < clazz.getEnumConstants().length; i++) {
         comboItems[i] = clazz.getEnumConstants()[i].toString();
      }

      editor = new TableEditor(table);
      editor.horizontalAlignment = SWT.LEFT;
      editor.grabHorizontal = true;

      color = table.getDisplay().getSystemColor(SWT.COLOR_WHITE);
      table.addListener(SWT.MouseDown, tblListener);
   }

   private int getIndexByText(final String text) {
      for (int i = 0; i < comboItems.length; i++) {
         if (text.equals(comboItems[i])) {
            return i;
         }
      }
      throw new IllegalArgumentException("No combo item matching text: " + text);
   }

   /**
    * Called when the user has typed a value and then presses <I><B>Enter</B></I>.
    * 
    * @param itemIndex the zero relative index of the {@link TableItem} of the cell to be edited
    * @param value the current selection in the cell before it lost focus
    * @param previousValue the value that was in the cell before the edit took place
    * @return the value that will be actually written into the table's cell
    */
   abstract protected T applyValue(final int itemIndex, final T value, final T previousValue);

   /**
    * Called when the cell editor has lost focus.
    * 
    * @param itemIndex the row index of the TableItem's cell that has lost focus
    * @param value the current value of the cell just before it lost focus
    * @param previousValue the value of the cell before editing began
    * @return the value that cekk will be set to when focus is lost
    */
   abstract protected T focusLost(final int itemIndex, final T value, final T previousValue);

   /**
    * Either enables or disables this cell editor. A disabled cell editor will not edit any cells in the entire column
    * that this cell editor is attached to.
    * 
    * @param enabled
    */
   public void setEnabled(boolean enabled) {
      // do nothing if already enabled/disabled
      if (this.enabled == enabled) return;

      if (enabled) {
         table.addListener(SWT.MouseDown, tblListener);
         this.enabled = true;
      } else {
         table.removeListener(SWT.MouseDown, tblListener);
         this.enabled = false;
      }
   }

}