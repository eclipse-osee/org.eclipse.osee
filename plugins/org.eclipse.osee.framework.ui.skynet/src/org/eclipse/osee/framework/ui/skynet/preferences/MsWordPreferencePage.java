/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.preferences;

import java.util.logging.Level;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Theron Virgin
 * @author Ryan D. Brooks
 */
public class MsWordPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   public static final String IDENTFY_IMAGE_CHANGES = "IdentifyImageChangesInWordDiff";
   public static final String MUTI_EDIT_SAVE_ALL_CHANGES = "multieditSaveAllChanges";
   public static final String IGNORE_FIELD_CODE_CHANGES = "ignoreFieldCodeChanges";

   private Button identifyImageChangesInWord;
   private Button saveAllChanges;
   private Button ignoreFieldCodes;

   @Override
   protected Control createContents(Composite parent) {
      noDefaultAndApplyButton();

      identifyImageChangesInWord = new Button(parent, SWT.CHECK);
      identifyImageChangesInWord.setText("Do Not Display OSEE Detected Image Change Indication in Differences");
      identifyImageChangesInWord.setSelection(getUserBooleanSetting(IDENTFY_IMAGE_CHANGES));

      saveAllChanges = new Button(parent, SWT.CHECK);
      saveAllChanges.setText("When multi-editing, save all changed artifacts (even those with no textual changes)");
      saveAllChanges.setSelection(getUserBooleanSetting(MUTI_EDIT_SAVE_ALL_CHANGES));

      ignoreFieldCodes = new Button(parent, SWT.CHECK);
      ignoreFieldCodes.setText("Ignore Field Codes when performing Word diff");
      ignoreFieldCodes.setSelection(getUserBooleanSetting(IGNORE_FIELD_CODE_CHANGES));

      return parent;
   }

   private boolean getUserBooleanSetting(String settingKey) {
      try {
         return UserManager.getBooleanSetting(settingKey);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
   }

   private void setUserBooleanSetting(String settingKey, Button button) {
      try {
         UserManager.setSetting(settingKey, String.valueOf(button.getSelection()));
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
   public boolean performOk() {
      setUserBooleanSetting(IDENTFY_IMAGE_CHANGES, identifyImageChangesInWord);
      setUserBooleanSetting(MUTI_EDIT_SAVE_ALL_CHANGES, saveAllChanges);
      setUserBooleanSetting(IGNORE_FIELD_CODE_CHANGES, ignoreFieldCodes);
      try {
         UserManager.getUser().persist(getClass().getSimpleName());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.performOk();
   }
}
