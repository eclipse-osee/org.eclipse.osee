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
package org.eclipse.osee.ote.ui.define.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TestRunStorageKey;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.panels.BranchSelectSimpleComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class BranchComboDialog extends TitleAreaDialog implements Listener {
   private static final Image TITLE_BAR_IMAGE = ImageManager.getImage(OteDefineImage.CHILD_BRANCH);
   private static final Image MESSAGE_IMAGE = ImageManager.getImage(OteDefineImage.COMMIT_WIZ);
   private static final String MESSAGE_TITLE = "Select a Working Branch";
   private static final String TITLE_BAR_TEXT = "Working Branch";
   private static final String MESSAGE = "Select a working branch";

   protected static final int COMBO_HISTORY_LENGTH = 5;

   private BranchSelectSimpleComposite branchSelectComposite;

   private BranchComboDialog(Shell parentShell) {
      super(parentShell);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite content = (Composite) super.createDialogArea(parent);

      branchSelectComposite = new BranchSelectSimpleComposite(content, SWT.NONE);
      restoreWidgetValues();
      branchSelectComposite.addListener(this);
      setTitle(MESSAGE_TITLE);
      setTitleImage(MESSAGE_IMAGE);
      setMessage(MESSAGE);
      getShell().setText(TITLE_BAR_TEXT);
      getShell().setImage(TITLE_BAR_IMAGE);

      return branchSelectComposite;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control toReturn = super.createButtonBar(parent);
      checkState();
      return toReturn;
   }

   public BranchId getSelection() {
      return branchSelectComposite.getSelectedBranch();
   }

   private void checkState() {
      boolean isValid = getSelection().isValid();
      getButton(IDialogConstants.OK_ID).setEnabled(isValid);
      setErrorMessage(isValid ? null : "Branch cannot be empty.");
   }

   @Override
   protected void okPressed() {
      saveWidgetValues();
      super.okPressed();
   }

   @Override
   public void handleEvent(Event event) {
      checkState();
   }

   protected void restoreWidgetValues() {
      IDialogSettings settings = OteUiDefinePlugin.getInstance().getDialogSettings();
      if (settings != null) {
         String[] branchUuids = settings.getArray(TestRunStorageKey.BRANCH_IDS);
         String lastSelected = settings.get(TestRunStorageKey.SELECTED_BRANCH_ID);
         branchSelectComposite.restoreWidgetValues(branchUuids, lastSelected);
      }
   }

   protected void saveWidgetValues() {
      IDialogSettings settings = OteUiDefinePlugin.getInstance().getDialogSettings();
      if (settings != null) {
         // update source names history
         String[] branchUuids = settings.getArray(TestRunStorageKey.BRANCH_IDS);
         if (branchUuids == null) {
            branchUuids = new String[0];
         }

         try {
            BranchId branch = getSelection();
            if (branch.isValid()) {
               String lastBranchSelected = branch.getIdString();

               List<String> history = new ArrayList<>(Arrays.asList(branchUuids));
               history.remove(lastBranchSelected);
               history.add(0, lastBranchSelected);
               if (history.size() > COMBO_HISTORY_LENGTH) {
                  history.remove(COMBO_HISTORY_LENGTH);
               }
               branchUuids = new String[history.size()];
               history.toArray(branchUuids);

               settings.put(TestRunStorageKey.BRANCH_IDS, branchUuids);
               settings.put(TestRunStorageKey.SELECTED_BRANCH_ID, lastBranchSelected);
               try {
                  settings.save(this.getClass().getName());
               } catch (IOException ex) {
                  OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public static BranchId getBranchFromUser() throws OseeCoreException {
      BranchId toReturn = BranchId.SENTINEL;
      BranchComboDialog branchSelection = new BranchComboDialog(AWorkbench.getActiveShell());
      int result = branchSelection.open();
      if (result == Window.OK) {
         toReturn = branchSelection.getSelection();
      }
      return toReturn;
   }
}
