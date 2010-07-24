/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class ChildSelectCheckedTreeSelectionDialog extends
		CheckedTreeSelectionDialog {

	@Override
	public Object[] getResult() {
		Object[] objs =  super.getResult();
		return objs;
	}


	private CheckboxTreeViewer viewer;
	/**
	 * @param parent
	 * @param labelProvider
	 * @param contentProvider
	 */
	public ChildSelectCheckedTreeSelectionDialog(Shell parent,
			ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
	}

	
	@Override
	protected CheckboxTreeViewer createTreeViewer(Composite parent) {
		viewer =  super.createTreeViewer(parent);
		viewer.addCheckStateListener(new ICheckStateListener(){
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				viewer.expandToLevel(event.getElement(), 1);
				viewer.setSubtreeChecked(event.getElement(), event.getChecked());
			}
		});
		return viewer;
	}
}
