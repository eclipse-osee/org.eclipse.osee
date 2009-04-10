/*
 * Created on Apr 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author b1529404
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#getLayoutData()
	 */
	@Override
	public LayoutData getLayoutData() {
		LayoutData data = super.getLayoutData();
		data.minimumWidth = 20;
		return data;
	}

}
