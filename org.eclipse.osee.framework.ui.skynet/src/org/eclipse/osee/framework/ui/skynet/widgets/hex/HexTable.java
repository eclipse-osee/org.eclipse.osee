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

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * A simple TableViewer to demonstrate usage of an ILazyContentProvider. You can compare this snippet to the
 * Snippet029VirtualTableViewer to see the small but needed difference.
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 */
public abstract class HexTable extends Composite{

	private final TableViewer v;
	private final int bytesPerRow;

	public HexTable(Composite parent, byte[] array, int bytesPerRow) {
		super(parent, SWT.NONE);
		this.bytesPerRow = bytesPerRow;
		v = new TableViewer(this, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		v.setContentProvider(new HexTableContentProvider(v, bytesPerRow));
		v.setUseHashlookup(true);

		TableColumnLayout layout = new TableColumnLayout();
		setLayout(layout);
		createAndConfigureColumns(v, layout, bytesPerRow);
		v.setInput(array);
		v.setItemCount((array.length + bytesPerRow - 1) / bytesPerRow);
		//  v.getTable().setLinesVisible(true);
		v.getTable().setHeaderVisible(false);

	}
	
	public int getBytesPerRow() {
		return bytesPerRow;
	}

	abstract protected void createAndConfigureColumns(TableViewer viewer, TableColumnLayout layout, int bytesPerRow);
	
	public IHexTblHighlighter createHighlighter(final int index, final int length, Color color) {
		return new Highlighter((HexTableContentProvider)v.getContentProvider(), index, length, color);
	}



}