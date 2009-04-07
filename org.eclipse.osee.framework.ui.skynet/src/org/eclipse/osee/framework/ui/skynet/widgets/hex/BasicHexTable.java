/*
 * Created on Apr 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author b1529404
 *
 */
public class BasicHexTable extends HexTable{

	/**
	 * @param shell
	 * @param array
	 * @param bytesPerRow
	 */
	public BasicHexTable(Composite parent, byte[] array, int bytesPerRow) {
		super(parent, array, bytesPerRow);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.widgets.hex.HexTable#createAndConfigureColumns(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void createAndConfigureColumns(TableViewer v, TableColumnLayout layout, int bytesPerRow) {
		ColumnViewerToolTipSupport.enableFor(v, ToolTip.NO_RECREATE);
		TableViewerColumn column = new TableViewerColumn(v, SWT.RIGHT);

		column.setLabelProvider(new OffsetColumnLabelProvider());
		column.getColumn().setText("Offset");
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		layout.setColumnData(column.getColumn(), new ColumnPixelData(50));
		for (int i = 0; i < bytesPerRow; i++) {
			TableViewerColumn c = new TableViewerColumn(v, SWT.LEFT);
			c.setLabelProvider(new ByteColumnLabelProvider(i));
			c.getColumn().setText(Integer.toHexString(i));
			c.getColumn().setResizable(false);
			c.getColumn().setMoveable(false);
			c.setEditingSupport(new HexEditingSupport(v, i));
			layout.setColumnData(c.getColumn(), new ColumnPixelData(26));
		}

		TableViewerColumn divider = new TableViewerColumn(v, SWT.LEFT);
		divider.getColumn().setResizable(false);
		divider.setLabelProvider(new DividerLabel());
		layout.setColumnData(divider.getColumn(), new ColumnPixelData(1));
		for (int i = 0; i < bytesPerRow; i++) {
			TableViewerColumn c = new TableViewerColumn(v, SWT.LEFT);

			c.setLabelProvider(new AsciiColumnLabelProvider(i));
			c.getColumn().setResizable(false);
			c.getColumn().setMoveable(false);
			layout.setColumnData(c.getColumn(), new ColumnPixelData(20));
		}

		TableViewerFocusCellManager focusCellManager =
			new TableViewerFocusCellManager(v, new FocusCellOwnerDrawHighlighter(v));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(
				v,
				focusCellManager,
				actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());
		byte[] array = new byte[702];
		for (int i = 0; i < array.length; i++) {
			array[i] = (byte) i;
		}
		int index = 50;
		String str = "this is a test";
		array[48] = 3;
		array[49] = 3;
		for (byte b : str.getBytes()) {
			array[index] = b;
			index++;
		}
		array[index] = 3;
		array[index + 1] = 3;
		BasicHexTable t = new BasicHexTable(shell, array, 16);
		shell.open();
		IHexTblHighlighter yellowHL = t.createHighlighter(30, 3, Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
		IHexTblHighlighter blueHL =
			t.createHighlighter(50, str.length(), Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		yellowHL.highlight();
		blueHL.highlight();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}

		display.dispose();

	}
}
