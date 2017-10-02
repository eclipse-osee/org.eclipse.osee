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

import java.io.File;
import java.util.logging.Level;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class EditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   private static String CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN =
      "change.report.close.editors.on.shutdown";
   private static String USE_EXTERNAL_COMPARE_EDITOR_FOR_TEXT = "use.external.compare.editor.for.text";
   private static String EXTERNAL_COMPARE_EDITOR_FOR_TEXT = "external.compare.editor.for.text";
   private static String ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR = "artifact.editor.include.attribute.tab";
   private Button artifactEditorButton;
   private Button editButton;
   private Button useCompareEditorForTextCompares;
   private Button includeAttributeTabOnArtifactEditor;
   private Button showTokenForChangeName;
   private Text compareEditorTextBox;

   public static boolean isCloseChangeReportEditorsOnShutdown()  {
      return UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
   }

   public static boolean isIncludeAttributeTabOnArtifactEditor()  {
      return UserManager.getBooleanSetting(ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR);
   }

   public static boolean isUseExternalCompareEditorForText()  {
      return UserManager.getBooleanSetting(USE_EXTERNAL_COMPARE_EDITOR_FOR_TEXT);
   }

   public static String getExternalCompareEditorForText()  {
      return UserManager.getSetting(EXTERNAL_COMPARE_EDITOR_FOR_TEXT);
   }

   @Override
   protected Control createContents(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      setDefaultPresentation(composite);

      useCompareEditorForTextCompares = new Button(composite, SWT.CHECK);
      useCompareEditorForTextCompares.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      useCompareEditorForTextCompares.setText("Close Change Report Editors on Shutdown");
      try {
         boolean value = UserManager.getBooleanSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN);
         useCompareEditorForTextCompares.setSelection(value);
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

      try {
         showTokenForChangeName = new Button(composite, SWT.CHECK);
         showTokenForChangeName.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
         showTokenForChangeName.setText("Show Tokens for Change Names");
         try {
            boolean value = UserManager.isShowTokenForChangeName();
            showTokenForChangeName.setSelection(value);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      createCompareEditorPreference(composite);

      return composite;
   }

   private void createCompareEditorPreference(Composite parent) {
      useCompareEditorForTextCompares = new Button(parent, SWT.CHECK);
      useCompareEditorForTextCompares.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      useCompareEditorForTextCompares.setText("Use External Compare Editor for text compares");
      try {
         boolean value = UserManager.getBooleanSetting(USE_EXTERNAL_COMPARE_EDITOR_FOR_TEXT);
         useCompareEditorForTextCompares.setSelection(value);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(3, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      compareEditorTextBox = new Text(composite, SWT.BORDER);
      try {
         String value = UserManager.getSetting(EXTERNAL_COMPARE_EDITOR_FOR_TEXT);
         compareEditorTextBox.setText(value);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Button fileDialog = new Button(composite, SWT.NONE);
      fileDialog.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
      final Shell shell = composite.getShell();
      final Text fUserNameTextBox = compareEditorTextBox;
      fileDialog.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(shell, SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.*"});
            String defaultDir = fUserNameTextBox.getText();
            File dir = new File(defaultDir);
            if (dir.isFile() || dir.isDirectory()) {
               dialog.setFilterPath(defaultDir);
            } else {
               dialog.setFilterPath("c:\\");
            }

            String result = dialog.open();
            if (Strings.isValid(result)) {
               fUserNameTextBox.setText(result);
            }
         }
      });
      Button helpButton = new Button(composite, SWT.NONE);
      helpButton.setImage(ImageManager.getImage(FrameworkImage.HELP));
      helpButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            HtmlDialog dialog = new HtmlDialog("External Compare Editor", "", AHTML.simplePage(
               "Example: </br></br>C:\\Program Files (x86)\\Beyond Compare 4\\BComp.exe \"%s\" \"%s\"</br></br>" + //
            "%s will be replaced with filenames."));
            dialog.open();
         }
      });
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

         boolean result = useCompareEditorForTextCompares.getSelection();
         UserManager.setSetting(CHANGE_REPORT_CLOSE_CHANGE_REPORT_EDITORS_ON_SHUTDOWN, String.valueOf(result));

         if (showTokenForChangeName != null) {
            boolean set = showTokenForChangeName.getSelection();
            UserManager.setShowTokenForChangeName(set);
         }
         if (includeAttributeTabOnArtifactEditor != null) {
            result = includeAttributeTabOnArtifactEditor.getSelection();
            UserManager.setSetting(ADMIN_INCLUDE_ATTRIBUTE_TAB_ON_ARTIFACT_EDITOR, String.valueOf(result));
         }

         if (useCompareEditorForTextCompares != null) {
            result = useCompareEditorForTextCompares.getSelection();
            UserManager.setSetting(USE_EXTERNAL_COMPARE_EDITOR_FOR_TEXT, String.valueOf(result));
         }

         if (compareEditorTextBox != null) {
            String editor = compareEditorTextBox.getText();
            UserManager.setSetting(EXTERNAL_COMPARE_EDITOR_FOR_TEXT, editor);
         }

         UserManager.getUser().persist(getClass().getSimpleName());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return super.performOk();
   }
}