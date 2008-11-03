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
package org.eclipse.osee.framework.ui.skynet.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.CorePreferences;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ArbitrationServerPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

   private DefaultWithStringAndIntegerFields fields;

   public ArbitrationServerPage() {
      super(GRID);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
    */
   @Override
   protected void createFieldEditors() {
      Composite parent = getFieldEditorParent();

      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group httpGroup = new Group(content, SWT.NONE);
      httpGroup.setLayout(new GridLayout());
      httpGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      httpGroup.setText("OSEE Arbitration Server");

      String defaultRemoteAddress =
            CoreClientActivator.getInstance().getPluginPreferences().getDefaultString(
                  CorePreferences.ARBITRATION_SERVER);
      fields =
            new DefaultWithStringAndIntegerFields(CorePreferences.ARBITRATION_SERVER, defaultRemoteAddress,
                  "Enter Address:", "Enter Port:", httpGroup);
      addField(fields);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
    */
   @Override
   public boolean performOk() {
      super.performOk();
      Preferences preferences = CoreClientActivator.getInstance().getPluginPreferences();
      String current = fields.getSelected();
      String lastSelected = preferences.getString(CorePreferences.ARBITRATION_SERVER);
      preferences.setValue(CorePreferences.ARBITRATION_SERVER, current);
      if (!lastSelected.equals(current)) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {

               MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Shutdown Requested", "Arbitration server change - restart required.");
               PlatformUI.getWorkbench().restart();
            }
         });
      }
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("Select an arbitration server or specify a server address and port in the entry boxes below.");
   }
}
