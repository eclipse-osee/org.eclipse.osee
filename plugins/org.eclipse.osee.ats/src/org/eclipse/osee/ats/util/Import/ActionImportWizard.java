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
package org.eclipse.osee.ats.util.Import;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class ActionImportWizard extends Wizard implements IImportWizard {
   private ActionImportPage mainPage;
   private IStructuredSelection selection;

   public ActionImportWizard() {
      super();
      Bundle bundle = Platform.getBundle(AtsPlugin.PLUGIN_ID);
      setWindowTitle("Action Import Wizard");
   }

   @Override
   public boolean performFinish() {
      return mainPage.finish();
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   @Override
   public void addPages() {
      mainPage = new ActionImportPage("Import Actions into OSEE ATS", selection);
      addPage(mainPage);
   }

}
