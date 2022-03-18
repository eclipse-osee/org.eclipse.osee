/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.icteam.reqif.dialogs.ImportReqIfDialog;
import org.eclipse.osee.icteam.reqif.export.ReqIfLoad;

/**
 * Handler to handle the Import of ReqIf Specifications
 * 
 * @author Manjunath Sangappa
 */
public class ImportReqIfHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) {
		
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		if (currentSelection instanceof IStructuredSelection) {
			try {
				IStructuredSelection structSel = (IStructuredSelection) currentSelection;

				if (!structSel.isEmpty()) {
					Object firstElement = structSel.getFirstElement();
					if (firstElement instanceof Artifact) {

						Artifact parentArtifact = (Artifact) firstElement;

						final ImportReqIfDialog dialog = new ImportReqIfDialog(Displays.getActiveShell(), parentArtifact);
						int open = dialog.open();
						if (open == 0) {
							ReqIfLoad load = new ReqIfLoad();
							load.load(parentArtifact, dialog.getReqIFFileName(), dialog.getFileName());
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
}
