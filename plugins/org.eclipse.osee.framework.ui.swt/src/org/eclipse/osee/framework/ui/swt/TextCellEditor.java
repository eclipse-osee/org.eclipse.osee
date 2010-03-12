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
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Allows editing of cells within a table widget
 * 
 * @author Ken J. Aguilar
 */
public abstract class TextCellEditor extends CustomTableCellEditor<String> {
   private final TableEditor editor;
   private final Table table;
   private final int columnIndex;
   private final int textStyle;
   private final String toolTip;
   private boolean enabled;

   /**
    * Listern that handles displaying and closing the edit box on the cell to be editted
    * 
    * @author Ken J. Aguilar
    */
   private final class EditCellListener implements Listener {
      public void handleEvent(Event event) {
         final Point pt = new Point(event.x, event.y);
         int index = table.getTopIndex();
         while (index < table.getItemCount()) {
            if (!doNotEditList.get(index)) {
               final int rowIndex = index;
               final TableItem item = table.getItem(index);
               final Rectangle rect = item.getBounds(columnIndex);
               if (rect.contains(pt)) {
                  final String oldText = item.getText(columnIndex);
                  final Text text = new Text(table, textStyle);
                  text.setToolTipText(toolTip);
                  final Listener textListener = new Listener() {
                     public void handleEvent(final Event e) {
                        final String value = text.getText();
                        switch (e.type) {
                           case SWT.FocusOut:
                              item.setText(columnIndex, focusLost(rowIndex, value, oldText));
                              text.dispose();
                              break;
                           case SWT.Traverse:
                              switch (e.detail) {
                                 case SWT.TRAVERSE_RETURN:
                                    item.setText(columnIndex, applyValue(rowIndex, value, oldText));
                                    // FALL THROUGH
                                 case SWT.TRAVERSE_ESCAPE:
                                    text.dispose();
                                    e.doit = false;
                              }
                              break;
                        }
                     }
                  };
                  text.addListener(SWT.FocusOut, textListener);
                  text.addListener(SWT.Traverse, textListener);
                  editor.setEditor(text, item, columnIndex);
                  text.setText(oldText);
                  text.selectAll();
                  text.setFocus();
                  return;
               }
            }
            index++;
         }
      }
   }

   private final EditCellListener tblListener;

   /**
    * Creates a new Text Cell Editor on te specified table for the given column index.
    * 
    * @param table the table that will host this Cell Editor
    * @param columnIndex the index of the column this editor will edit cells for
    * @param textStyle the style of the edit box
    * @param toolTip the tool tip text of the edit box
    */
   public TextCellEditor(final Table table, final int columnIndex, final int textStyle, final String toolTip) {
      this.table = table;
      this.columnIndex = columnIndex;
      this.textStyle = textStyle;
      this.toolTip = toolTip;
      this.enabled = true;
      editor = new TableEditor(table);
      editor.horizontalAlignment = SWT.LEFT;
      editor.grabHorizontal = true;
      tblListener = new EditCellListener();
      table.addListener(SWT.MouseDown, tblListener);
   }

   /**
    * Called when the user has typed a value and then presses <I><B>Enter</B></I>.
    * 
    * @param itemIndex the zero relative index of the {@link TableItem} of the cell to be edited
    * @param value the value that the user typed
    * @param previousValue the value that was in the cell before the edit took place
    * @return the value that will be actually written into the table's cell
    */
   abstract protected String applyValue(final int itemIndex, final String value, final String previousValue);

   /**
    * Called when the cell editor has lost focus.
    * 
    * @param itemIndex
    * @param value
    * @param previousValue
    * @return String
    */
   abstract protected String focusLost(final int itemIndex, final String value, final String previousValue);

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

   public boolean isEnabled() {
      return enabled;
   }

}
