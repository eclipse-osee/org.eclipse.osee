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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.panels.BranchSelectSimpleComposite;
import org.eclipse.osee.ote.define.TestRunStorageKey;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

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

      branchSelectComposite = BranchSelectSimpleComposite.createWorkingBranchSelectComposite(content, SWT.NONE);
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

   public Branch getSelection() {
      return branchSelectComposite.getSelectedBranch();
   }

   private boolean isValid() {
      return getSelection() != null;
   }

   private void checkState() {
      getButton(IDialogConstants.OK_ID).setEnabled(isValid());
      if (isValid() != true) {
         setErrorMessage("Branch cannot be empty.");
      } else {
         setErrorMessage(null);
      }
   }

   @Override
   protected void okPressed() {
      saveWidgetValues();
      super.okPressed();
   }

   public void handleEvent(Event event) {
      checkState();
   }

   protected void restoreWidgetValues() {
      IDialogSettings settings = OteUiDefinePlugin.getInstance().getDialogSettings();
      if (settings != null) {
         String[] branchIds = settings.getArray(TestRunStorageKey.BRANCH_IDS);
         String lastSelected = settings.get(TestRunStorageKey.SELECTED_BRANCH_ID);
         branchSelectComposite.restoreWidgetValues(branchIds, lastSelected);
      }
   }

   protected void saveWidgetValues() {
      IDialogSettings settings = OteUiDefinePlugin.getInstance().getDialogSettings();
      if (settings != null) {
         // update source names history
         String[] branchIds = settings.getArray(TestRunStorageKey.BRANCH_IDS);
         if (branchIds == null) {
            branchIds = new String[0];
         }

         try {
            Branch branch = getSelection();
            if (branch != null && branch.hasParentBranch()) {
               String lastBranchSelected = Integer.toString(branch.getId());

               List<String> history = new ArrayList<String>(Arrays.asList(branchIds));
               history.remove(lastBranchSelected);
               history.add(0, lastBranchSelected);
               if (history.size() > COMBO_HISTORY_LENGTH) {
                  history.remove(COMBO_HISTORY_LENGTH);
               }
               branchIds = new String[history.size()];
               history.toArray(branchIds);

               settings.put(TestRunStorageKey.BRANCH_IDS, branchIds);
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

   public static Branch getBranchFromUser() throws OseeCoreException {
      Branch toReturn = null;
      BranchComboDialog branchSelection =
            new BranchComboDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
      int result = branchSelection.open();
      if (result == Window.OK) {
         toReturn = branchSelection.getSelection();
         if (toReturn != null && toReturn.hasParentBranch() == false) {
            toReturn = null;
         }
      }
      return toReturn;
   }
}
