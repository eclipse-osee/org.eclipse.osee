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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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

/**
 * @author Theron Virgin
 */
public class DiffPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   public static final String IDENTFY_IMAGE_CHANGES = "IdentifyImageChangesInWordDiff";
   public static final String REMOVE_TRACKED_CHANGES = "RemoveTrackedChangesInWordDiff";
   private Button identifyImageChangesInWord;
   private Button removeTrackedChangesInWord;

   @Override
   protected Control createContents(Composite parent) {
      noDefaultAndApplyButton();
      // TODO Temporary until editor opening can be configured by users
      identifyImageChangesInWord = new Button(parent, SWT.CHECK);
      identifyImageChangesInWord.setText("Do Not Display OSEE Detected Image Change Indication in Differences");
      try {
         identifyImageChangesInWord.setSelection(StaticIdManager.hasValue(UserManager.getUser(), IDENTFY_IMAGE_CHANGES));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      // TODO Temporary until editor opening can be configured by users
      removeTrackedChangesInWord = new Button(parent, SWT.CHECK);
      removeTrackedChangesInWord.setText("Do Not Remove Word Tracked Changes prior to Diffing");
      try {
         removeTrackedChangesInWord.setSelection(StaticIdManager.hasValue(UserManager.getUser(), IDENTFY_IMAGE_CHANGES));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return parent;
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
         if (identifyImageChangesInWord.getSelection()) {
            StaticIdManager.setSingletonAttributeValue(UserManager.getUser(), IDENTFY_IMAGE_CHANGES);
         } else {
            UserManager.getUser().deleteAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, IDENTFY_IMAGE_CHANGES);
         }
         UserManager.getUser().persistAttributes();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      try {
         if (removeTrackedChangesInWord.getSelection()) {
            StaticIdManager.setSingletonAttributeValue(UserManager.getUser(), REMOVE_TRACKED_CHANGES);
         } else {
            UserManager.getUser().deleteAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, REMOVE_TRACKED_CHANGES);
         }
         UserManager.getUser().persistAttributes();
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
