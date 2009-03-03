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
package org.eclipse.osee.framework.ui.data.model.editor.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.wizard.ODMExportWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ODMExportAction extends Action {

   private final ODMEditor editor;

   public ODMExportAction(ODMEditor editor) {
      super("Osee Data Model Export");
      this.editor = editor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#getId()
    */
   @Override
   public String getId() {
      return ActionFactory.EXPORT.getId();
   }

   @Override
   public void run() {
      super.run();
      ODMExportWizard wizard = new ODMExportWizard(editor.getEditorInput().getDataTypeCache());
      WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      dialog.create();
      dialog.open();
   }
}
