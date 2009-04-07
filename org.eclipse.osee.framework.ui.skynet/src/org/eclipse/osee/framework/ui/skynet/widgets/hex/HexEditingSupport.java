package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class HexEditingSupport extends EditingSupport {
	private final TextCellEditor textEditor;
	private final int column;
	private final TableViewer viewer;
	
	public HexEditingSupport(TableViewer viewer, int column) {
		super(viewer);
		this.viewer = viewer;
		textEditor = new TextCellEditor(viewer.getTable());
		((Text) textEditor.getControl()).setTextLimit(2);
		this.column = column;

	}

	protected boolean canEdit(Object element) {
		return ((HexTableRow) element).length > column;
	}

	protected CellEditor getCellEditor(Object element) {
		HexTableRow row = (HexTableRow) element;
		int index = row.offset / row.length;
		Rectangle rect = viewer.getTable().getItem(index).getBounds(column);
		textEditor.getControl().setBounds(rect);
		return textEditor;
	}

	protected Object getValue(Object element) {
		HexTableRow row = (HexTableRow) element;
		return Integer.toHexString((row.array[row.offset + column] & 0xFF)).toUpperCase();
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