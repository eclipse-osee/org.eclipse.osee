/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

   private XText configText;

   @Override
   protected Control createContents(Composite parent) {
      Composite body = createComposite(parent, 2);

      //Text Box
      Composite textBoxC = createComposite(body, 1);
      configText = new XText("ATS Configuration\n(requires restart)");
      configText.setFillHorizontally(true);
      configText.createWidgets(textBoxC, 1);
      updateConfigurationField();

      Button changeConfiguration = new Button(body, SWT.NONE);
      changeConfiguration.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
      changeConfiguration.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               AtsConfigurationDialog dialog =
                  new AtsConfigurationDialog(AtsClientService.get().getConfigurations().getConfigs());

               if (dialog.open() == 0) {
                  AtsConfiguration config = (AtsConfiguration) dialog.getResult()[0];
                  BranchId branch = BranchId.valueOf(config.getBranchId());

                  AtsClientService.get().storeAtsBranch(branch, config.getName());

                  updateConfigurationField();
                  AWorkbench.popup("ATS Configuration Changed; OSEE will be restarted.");
                  PlatformUI.getWorkbench().restart();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      return body;
   }

   private void updateConfigurationField() {
      configText.setEditable(true);
      configText.setText(AtsClientService.get().getAtsBranch().getName());
      configText.setEditable(false);
   }

   /**
    * initialize the preference store to use with the workbench
    */
   @Override
   public void init(IWorkbench workbench) {
      IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
      setPreferenceStore(preferenceStore);
   }

   @Override
   protected void performApply() {
      performOk();
   }

   @Override
   public boolean performOk() {
      return super.performOk();
   }

   /**
    * Creates composite control and sets the default layout data.
    *
    * @param parent the parent of the new composite
    * @param numColumns the number of columns for the new composite
    * @return the newly-created composite
    */
   private Composite createComposite(Composite parent, int numColumns) {
      Composite composite = new Composite(parent, SWT.NULL);

      //GridLayout
      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      composite.setLayout(layout);

      //GridData
      GridData data = new GridData();
      data.verticalAlignment = GridData.FILL;
      data.horizontalAlignment = GridData.FILL;
      composite.setLayoutData(data);
      return composite;
   }

}