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

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

class HexTableContentProvider implements ILazyContentProvider {
	private TableViewer viewer;
	private byte[] array;
	private int bytesPerRow;
	private HexTableRow[] elements;

	HexTableContentProvider(TableViewer viewer, int bytesPerRow) {
		this.viewer = viewer;
		this.bytesPerRow = bytesPerRow;
	}

	HexTableRow[] getElements() {
		return elements;
	}

	int getBytesPerRow() {
		return bytesPerRow;
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null && newInput != null) {
			byte[] oldArray = (byte[]) oldInput;
			byte[] newArray = (byte[]) newInput;
			if (oldArray.length == newArray.length) {
				// same array length so we are done
				this.array = newArray;
				return;
			}
		}
		if (newInput != null) {
			this.array = (byte[]) newInput;
			int rowCOunt = (array.length + bytesPerRow - 1) / bytesPerRow;
			elements = new HexTableRow[rowCOunt];
			int offset = 0;
			int bytesLeft = array.length;
			for (int i = 0; i < rowCOunt; i++) {
				elements[i] = new HexTableRow(offset, bytesLeft >= bytesPerRow ? bytesPerRow : bytesLeft, array);
				offset += bytesPerRow;
				bytesLeft -= bytesPerRow;
			}
		}
	}

	/**
	 * @return the viewer
	 */
	TableViewer getViewer() {
		return viewer;
	}

	public void updateElement(int index) {
		viewer.replace(elements[index], index);
	}
}