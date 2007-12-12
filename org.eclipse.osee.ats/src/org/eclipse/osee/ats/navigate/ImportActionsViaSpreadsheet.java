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
package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.util.Import.ActionImportWizard;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheet extends XNavigateItemAction {

   public static String strs[] = new String[] {};

   /**
    * @param parent
    */
   public ImportActionsViaSpreadsheet(XNavigateItem parent) {
      super(parent, "Import Actions Via Spreadsheet");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      ActionImportWizard actionWizard = new ActionImportWizard();
      WizardDialog dialog =
            new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), actionWizard);
      dialog.create();
      dialog.open();
   }
}
