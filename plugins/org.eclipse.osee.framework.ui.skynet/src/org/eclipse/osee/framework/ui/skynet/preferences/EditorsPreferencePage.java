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

import java.util.logging.Level;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

   public static String PreviewOnDoubleClickForWordArtifacts = "PreviewOnDoubleClickForWordArtifacts";
   private Button previewOnDoubleClickForWordArtifacts;

   @Override
   protected Control createContents(Composite parent) {

      //Page Composite
      Composite composite = createComposite(parent, 3);

      // TODO Temporary until editor opening can be configured by users
      previewOnDoubleClickForWordArtifacts = new Button(composite, SWT.CHECK);
      previewOnDoubleClickForWordArtifacts.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
      previewOnDoubleClickForWordArtifacts.setText("Open MS Word preview on double-click of MS Word Artifact");
      try {
         previewOnDoubleClickForWordArtifacts.setSelection(StaticIdManager.hasValue(UserManager.getUser(),
               PreviewOnDoubleClickForWordArtifacts));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return composite;
   }

   /**
    * initialize the preference store to use with the workbench
    */
   public void init(IWorkbench workbench) {
   }

   @Override
   protected void performDefaults() {
   }

   @Override
   protected void performApply() {
      performOk();
   }

   @Override
   public boolean performOk() {
      try {
         if (previewOnDoubleClickForWordArtifacts.getSelection()) {
            StaticIdManager.setSingletonAttributeValue(UserManager.getUser(), PreviewOnDoubleClickForWordArtifacts);
         } else {
            UserManager.getUser().deleteAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE,
                  PreviewOnDoubleClickForWordArtifacts);
         }
         UserManager.getUser().persist();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
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