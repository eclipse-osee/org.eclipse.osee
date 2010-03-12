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
import org.eclipse.osee.framework.ui.data.model.editor.wizard.ODMImportWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ODMImportAction extends Action {

   private final ODMEditor editor;

   public ODMImportAction(ODMEditor editor) {
      super("Import");
      this.editor = editor;
   }

   @Override
   public String getId() {
      return ActionFactory.IMPORT.getId();
   }

   @Override
   public void run() {
      super.run();
      ODMImportWizard wizard = new ODMImportWizard(editor);
      WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      dialog.create();
      dialog.open();
   }
}
