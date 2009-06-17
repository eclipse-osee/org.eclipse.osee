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
package org.eclipse.osee.ote.ui.test.manager.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.preferences.environment.EnvironmentPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class TestManagerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

   public static final String TEST_MANAGER_KEY = "org.eclipse.osee.ote.ui.test.manager.PreferencePage";
   private EnvironmentPreferencePage environmentPreferencePage;

   public void init(IWorkbench workbench) {
      // Initialize the preference store we wish to use
      setPreferenceStore(TestManagerPlugin.getInstance().getPreferenceStore());
   }

   public boolean performOk() {
      environmentPreferencePage.storeVariables();

      return super.performOk();
   }

   private Control createBlankArea(Composite parent, int height, boolean allVertical) {
      Composite blank = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      GridData gd = new GridData();
      gd.minimumHeight = height;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = allVertical;
      blank.setLayout(gridLayout);
      blank.setLayoutData(gd);
      return parent;
   }

   private Control createDefaultEvironmentVariablesArea(Composite parent) {

      SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;

      GridData d = new GridData(GridData.FILL_BOTH);
      d.grabExcessHorizontalSpace = true;
      d.grabExcessVerticalSpace = true;
      sashForm.setLayout(gridLayout);
      sashForm.setLayoutData(d);

      environmentPreferencePage = new EnvironmentPreferencePage(sashForm);

      return parent;
   }

   private void createPageDescription(Composite parent) {
      (new Label(parent, SWT.NONE)).setText("Test Manager Settings:");
   }

   protected Control createContents(Composite parent) {

      createPageDescription(parent);
      createBlankArea(parent, 0, false);
      createDefaultEvironmentVariablesArea(parent);
      // createBlankArea(parent, 300, true);

      return parent;
   }

   protected void performApply() {
      performOk();
   }

   protected void performDefaults() {

   }

}
