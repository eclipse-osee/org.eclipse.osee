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
package org.eclipse.osee.ote.container;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OteContainerPage extends WizardPage implements IClasspathContainerPage {

   public OteContainerPage() {
      super("OTE CONTAINER");
   }

   @Override
   public boolean finish() {
      return true;
   }

   @Override
   public IClasspathEntry getSelection() {
      return JavaCore.newContainerEntry(OteClasspathContainer.ID);
   }

   @Override
   public void setSelection(IClasspathEntry containerEntry) {
   }

   @Override
   public void createControl(Composite parent) {
      Composite comp = new Composite(parent, SWT.None);
      GridLayout layout = new GridLayout(1, true);
      comp.setLayout(layout);

      GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
      comp.setLayoutData(data);

      Label label = new Label(comp, SWT.BORDER);
      label.setText("JUST CLICK FINISH YOU FOOL!!!");

      data = new GridData(SWT.FILL, SWT.FILL, true, true);
      label.setLayoutData(data);
      setControl(comp);

   }

}
