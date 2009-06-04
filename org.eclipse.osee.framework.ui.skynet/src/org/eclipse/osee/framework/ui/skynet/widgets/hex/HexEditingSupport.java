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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class HexEditingSupport extends EditingSupport {
	private final TextCellEditor textEditor;
	private final int column;
	private final TableViewer viewer;
	private final Font font;
	
	public HexEditingSupport(Font font, TableViewer viewer, int column) {
		super(viewer);
		this.font = font;
		this.viewer = viewer;
		textEditor = new CustomTextCellEditor(viewer.getTable(), SWT.SINGLE);
		((Text) textEditor.getControl()).setTextLimit(2);
		textEditor.getControl().setFont(font);
		this.column = column;

	}

	protected boolean canEdit(Object element) {
		return ((HexTableRow) element).length > column;
	}

	protected CellEditor getCellEditor(Object element) {
		HexTableRow row = (HexTableRow) element;
		int index = row.offset / row.length;
		Rectangle rect = viewer.getTable().getItem(index).getBounds(column);
		rect.width = 20;
		textEditor.getControl().setBounds(rect);
		return textEditor;
	}

	protected Object getValue(Object element) {
		HexTableRow row = (HexTableRow) element;
		return String.format("%02X", row.array[row.offset + column] & 0xFF);
	}

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
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Numeric Entry Error", e.getMessage());
		}
	}

}