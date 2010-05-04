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
import org.eclipse.osee.framework.skynet.core.User;
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
   private Button changeReportAsEditor;

   @Override
   protected Control createContents(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // TODO Temporary until editor opening can be configured by users
      previewOnDoubleClickForWordArtifacts = new Button(composite, SWT.CHECK);
      previewOnDoubleClickForWordArtifacts.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      previewOnDoubleClickForWordArtifacts.setText("Open MS Word preview on double-click of MS Word Artifact");
      try {
         previewOnDoubleClickForWordArtifacts.setSelection(StaticIdManager.hasValue(UserManager.getUser(),
               PreviewOnDoubleClickForWordArtifacts));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      changeReportAsEditor = new Button(composite, SWT.CHECK);
      changeReportAsEditor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      changeReportAsEditor.setText("Open Change Reports in an Editor");
      try {
         boolean value = UserManager.getUser().getBooleanSetting("change.report.as.editor");
         changeReportAsEditor.setSelection(value);
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
         User user = UserManager.getUser();
         if (previewOnDoubleClickForWordArtifacts.getSelection()) {
            StaticIdManager.setSingletonAttributeValue(user, PreviewOnDoubleClickForWordArtifacts);
         } else {
            user.deleteAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, PreviewOnDoubleClickForWordArtifacts);
         }

         boolean result = changeReportAsEditor.getSelection();
         user.setSetting("change.report.as.editor", String.valueOf(result));

         user.persist();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return super.performOk();
   }
}