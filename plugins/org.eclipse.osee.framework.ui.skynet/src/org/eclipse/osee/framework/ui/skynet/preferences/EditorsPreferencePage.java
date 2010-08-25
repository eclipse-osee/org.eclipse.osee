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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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

   private static String PreviewOnDoubleClickForWordArtifacts = "PreviewOnDoubleClickForWordArtifacts";
   private static String CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN =
      "change.report.close.editors.on.shutdown";
   private Button previewOnDoubleClickForWordArtifacts;
   private Button closeChangeReportEditorsOnShutdown;

   public static boolean isCloseChangeReportEditorsOnShutdown() throws OseeCoreException {
      return UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
   }

   public static boolean isPreviewOnDoubleClickForWordArtifacts() throws OseeCoreException {
      return StaticIdManager.hasValue(UserManager.getUser(), PreviewOnDoubleClickForWordArtifacts);
   }

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
         previewOnDoubleClickForWordArtifacts.setSelection(EditorsPreferencePage.isPreviewOnDoubleClickForWordArtifacts());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      closeChangeReportEditorsOnShutdown = new Button(composite, SWT.CHECK);
      closeChangeReportEditorsOnShutdown.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      closeChangeReportEditorsOnShutdown.setText("Close Change Report Editors on Shutdown");
      try {
         boolean value = UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
         closeChangeReportEditorsOnShutdown.setSelection(value);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return composite;
   }

   /**
    * initialize the preference store to use with the workbench
    */
   @Override
   public void init(IWorkbench workbench) {
      // do nothing
   }

   @Override
   protected void performDefaults() {
      // do nothing
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
            user.deleteAttribute(CoreAttributeTypes.StaticId, PreviewOnDoubleClickForWordArtifacts);
         }

         boolean result = closeChangeReportEditorsOnShutdown.getSelection();
         user.setSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN, String.valueOf(result));

         user.persist();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return super.performOk();
   }
}