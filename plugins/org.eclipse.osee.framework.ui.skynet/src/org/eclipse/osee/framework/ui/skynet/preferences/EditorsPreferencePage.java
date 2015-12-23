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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   private static String CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN =
      "change.report.close.editors.on.shutdown";
   private static String ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR = "artifact.editor.include.attribute.tab";
   private Button artifactEditorButton;
   private Button editButton;
   private Button closeChangeReportEditorsOnShutdown;
   private Button includeAttributeTabOnArtifactEditor;

   public static boolean isCloseChangeReportEditorsOnShutdown() throws OseeCoreException {
      return UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
   }

   public static boolean isIncludeAttributeTabOnArtifactEditor() throws OseeCoreException {
      return UserManager.getBooleanSetting(ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR);
   }

   @Override
   protected Control createContents(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      setDefaultPresentation(composite);

      closeChangeReportEditorsOnShutdown = new Button(composite, SWT.CHECK);
      closeChangeReportEditorsOnShutdown.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      closeChangeReportEditorsOnShutdown.setText("Close Change Report Editors on Shutdown");
      try {
         boolean value = UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
         closeChangeReportEditorsOnShutdown.setSelection(value);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         if (SystemGroup.OseeAdmin.isCurrentUserMember()) {
            includeAttributeTabOnArtifactEditor = new Button(composite, SWT.CHECK);
            includeAttributeTabOnArtifactEditor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
            includeAttributeTabOnArtifactEditor.setText("Admin - Include Attribute Tab on Artifact Editor");
            try {
               boolean value = UserManager.getBooleanSetting(ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR);
               includeAttributeTabOnArtifactEditor.setSelection(value);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return composite;
   }

   private void setDefaultPresentation(Composite composite) {
      // TODO Temporary until editor opening can be configured by users
      Group group = new Group(composite, SWT.NULL);
      group.setText("Default Presentation (if applicable)");
      group.setLayout(new RowLayout(SWT.VERTICAL));
      artifactEditorButton = new Button(group, SWT.CHECK);
      artifactEditorButton.setText("Artifact Editor");
      artifactEditorButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (artifactEditorButton.getSelection()) {
               editButton.setEnabled(false);
               editButton.setSelection(false);
            } else {
               editButton.setEnabled(true);
            }
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //
         }
      });

      editButton = new Button(group, SWT.CHECK);
      editButton.setText("Edit Mode");
      editButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (editButton.getSelection()) {
               artifactEditorButton.setEnabled(false);
               artifactEditorButton.setSelection(false);
            } else {
               artifactEditorButton.setEnabled(true);
            }
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //
         }
      });

      try {
         if (UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT)) {
            artifactEditorButton.setSelection(
               UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT));
         } else if (UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT)) {
            editButton.setSelection(UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
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
         UserManager.setSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT,
            String.valueOf(artifactEditorButton.getSelection()));
         UserManager.setSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT, String.valueOf(editButton.getSelection()));

         boolean result = closeChangeReportEditorsOnShutdown.getSelection();
         UserManager.setSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN, String.valueOf(result));

         if (includeAttributeTabOnArtifactEditor != null) {
            result = includeAttributeTabOnArtifactEditor.getSelection();
            UserManager.setSetting(ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR, String.valueOf(result));
         }

         UserManager.getUser().persist(getClass().getSimpleName());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return super.performOk();
   }
}