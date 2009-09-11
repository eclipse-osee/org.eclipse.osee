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
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
   public static final String REMOVE_TRACKED_CHANGES = "RemoveTrackedChangesInWordDiff";
   public static final String MUTI_EDIT_SAVE_ALL_CHANGES = "multieditSaveAllChanges";
   private Button identifyImageChangesInWord;
   private Button removeTrackedChangesInWord;
   private Button saveAllChanges;

   @Override
   protected Control createContents(Composite parent) {
      noDefaultAndApplyButton();

      identifyImageChangesInWord = new Button(parent, SWT.CHECK);
      identifyImageChangesInWord.setText("Do Not Display OSEE Detected Image Change Indication in Differences");
      identifyImageChangesInWord.setSelection(getUserBooleanSetting(IDENTFY_IMAGE_CHANGES));

      removeTrackedChangesInWord = new Button(parent, SWT.CHECK);
      removeTrackedChangesInWord.setText("Do Not Remove Word Tracked Changes prior to Diffing");
      removeTrackedChangesInWord.setSelection(getUserBooleanSetting(REMOVE_TRACKED_CHANGES));

      saveAllChanges = new Button(parent, SWT.CHECK);
      saveAllChanges.setText("When multi-editing save all chaneged artifacts (even those with no textual changes)");
      saveAllChanges.setSelection(getUserBooleanSetting(MUTI_EDIT_SAVE_ALL_CHANGES));

      return parent;
   }

   private boolean getUserBooleanSetting(String settingKey) {
      try {
         return UserManager.getUser().getBooleanSetting(settingKey);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return false;
      }
   }

   private void setUserBooleanSetting(String settingKey, Button button) {
      try {
         UserManager.getUser().setSetting(settingKey, String.valueOf(button.getSelection()));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /**
    * initialize the preference store to use with the workbench
    */
   public void init(IWorkbench workbench) {
   }

   @Override
   public boolean performOk() {

      setUserBooleanSetting(IDENTFY_IMAGE_CHANGES, identifyImageChangesInWord);
      setUserBooleanSetting(REMOVE_TRACKED_CHANGES, removeTrackedChangesInWord);
      setUserBooleanSetting(MUTI_EDIT_SAVE_ALL_CHANGES, saveAllChanges);
      try {
         UserManager.getUser().persist();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return super.performOk();
   }
}
