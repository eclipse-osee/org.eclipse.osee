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

package org.eclipse.osee.framework.ui.swt.hex;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;

public class HexEditingSupport extends EditingSupport {
   private final TextCellEditor textEditor;
   private final int column;
   private final TableViewer viewer;

   public HexEditingSupport(Font font, TableViewer viewer, int column) {
      super(viewer);
      this.viewer = viewer;
      textEditor = new CustomTextCellEditor(viewer.getTable(), SWT.SINGLE);
      ((Text) textEditor.getControl()).setTextLimit(2);
      textEditor.getControl().setFont(font);
      this.column = column;

   }

   @Override
   protected boolean canEdit(Object element) {
      return ((HexTableRow) element).length > column;
   }

   @Override
   protected CellEditor getCellEditor(Object element) {
      HexTableRow row = (HexTableRow) element;
      int index = row.offset / row.length;
      Rectangle rect = viewer.getTable().getItem(index).getBounds(column);
      rect.width = 20;
      textEditor.getControl().setBounds(rect);
      return textEditor;
   }

   @Override
   protected Object getValue(Object element) {
      HexTableRow row = (HexTableRow) element;
      return String.format("%02X", row.array[row.offset + column] & 0xFF);
   }

   @Override
   protected void setValue(Object element, Object value) {
      HexTableRow row = (HexTableRow) element;
      String strValue = value.toString();
      int val;
      try {
         val = Integer.parseInt(strValue, 16);
         if (val >= 0 && val <= 255) {
            row.array[row.offset + column] = (byte) val;
            getViewer().update(row, null);
         } else {
            throw new NumberFormatException("value out of range. Must be between 00 and FF");
         }
      } catch (NumberFormatException e) {
         MessageDialog.openError(Displays.getActiveShell(), "Numeric Entry Error", e.getMessage());
      }
   }

}