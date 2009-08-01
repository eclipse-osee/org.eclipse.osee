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
package org.eclipse.osee.framework.ui.data.model.editor.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ODMImportWizard extends Wizard implements IImportWizard {

   private final ODMEditor editor;
   private ODMImportPage importPage;
   private ODMSelectPage selectTypesPage;
   private DataTypeCache dataTypeCache;

   public ODMImportWizard(ODMEditor editor) {
      this.editor = editor;
      dataTypeCache = new DataTypeCache();
      setDefaultPageImageDescriptor(ImageManager.getImageDescriptor(ODMImage.IMPORT_IMAGE));
      setNeedsProgressMonitor(true);
      setWindowTitle("Osee Data Model Import Wizard");
   }

   @Override
   public boolean performFinish() {
      return false;
   }

   @Override
   public void addPages() {
      addPage(importPage = new ODMImportPage("Osee Data Type Source", "Select Osee Data Type Sources."));
      addPage(selectTypesPage = new ODMSelectPage("Osee Data Type Select", "Select Osee Data Types to import."));
      selectTypesPage.setInput(dataTypeCache);
   }

   @Override
   public boolean canFinish() {
      return super.canFinish();
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }

}
