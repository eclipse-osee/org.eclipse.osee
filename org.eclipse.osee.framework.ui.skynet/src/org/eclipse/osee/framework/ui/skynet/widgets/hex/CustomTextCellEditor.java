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

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author author Ken J. Aguilar
 *
 */
public class CustomTextCellEditor extends TextCellEditor{

	/**
	 * 
	 */
	public CustomTextCellEditor() {
		super();
	}

	/**
	 * @param parent
	 * @param style
	 */
	public CustomTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 */
	public CustomTextCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	public LayoutData getLayoutData() {
		LayoutData data = super.getLayoutData();
		data.minimumWidth = 20;
		return data;
	}

}
