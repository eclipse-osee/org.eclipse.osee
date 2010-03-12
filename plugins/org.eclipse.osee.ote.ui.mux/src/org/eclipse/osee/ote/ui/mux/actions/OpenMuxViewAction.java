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
package org.eclipse.osee.ote.ui.mux.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ote.ui.mux.view.MuxView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class OpenMuxViewAction extends Action {

    public OpenMuxViewAction() {
	super("Open Mux View");
    }

    @Override
    public void run() {
	try {
	    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		    .getActivePage().showView(MuxView.VIEW_ID);
	} catch (Exception e) {
	    MessageDialog.openError(Display.getDefault().getActiveShell(),
		    "Error", "got an exception");
	}
    }



}
