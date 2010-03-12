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

package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Roberto E. Escobar
 */
public class BranchSelectSimpleComposite extends Composite implements Listener {
   protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

   private Button branchSelectButton;
   private Combo branchSelectCombo;
   private boolean entryChanged;
   private Branch currentBranch;
   private final Set<Listener> listeners;
   private final boolean allowOnlyWorkingBranches;

   private BranchSelectSimpleComposite(Composite parent, int style, boolean allowOnlyWorkingBranches) {
      super(parent, style);
      this.allowOnlyWorkingBranches = allowOnlyWorkingBranches;
      this.entryChanged = false;
      this.listeners = Collections.synchronizedSet(new HashSet<Listener>());
      this.currentBranch = null;
      createControl(this);
   }

   public static BranchSelectSimpleComposite createWorkingBranchSelectComposite(Composite parent, int style) {
      return new BranchSelectSimpleComposite(parent, style, true);
   }

   public static BranchSelectSimpleComposite createBranchSelectComposite(Composite parent, int style) {
      return new BranchSelectSimpleComposite(parent, style, false);
   }

   private void createControl(Composite parent) {
      parent.setLayout(new GridLayout(2, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      branchSelectCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      branchSelectCombo.setLayoutData(data);
      branchSelectCombo.setFont(parent.getFont());
      branchSelectCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            updateFromSourceField();
         }
      });

      branchSelectCombo.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            // If there has been a key pressed then mark as dirty
            entryChanged = true;
         }
      });

      branchSelectCombo.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent e) {
            // Clear the flag to prevent constant update
            if (entryChanged) {
               entryChanged = false;
               updateFromSourceField();
            }
         }
      });

      branchSelectButton = new Button(parent, SWT.PUSH);
      branchSelectButton.setText("Select Branch...");
      branchSelectButton.addListener(SWT.Selection, this);
      branchSelectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
      branchSelectButton.setFont(parent.getFont());
   }

   public Branch getSelectedBranch() {
      Branch toReturn = null;
      if (branchSelectCombo != null && branchSelectCombo.isDisposed() != true) {
         String branchName = branchSelectCombo.getText();
         if (Strings.isValid(branchName)) {
            toReturn = (Branch) branchSelectCombo.getData(branchName);
            if (toReturn == null) {
               try {
                  toReturn = BranchManager.getBranch(branchName);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
         currentBranch = toReturn;
      }
      return currentBranch;
   }

   private boolean areOnlyWorkingBranchesAllowed() {
      return allowOnlyWorkingBranches;
   }

   private void updateFromSourceField() {
      setBranchName(getSelectedBranch());
      notifyListener(new Event());
   }

   private Branch getCurrentBranch() {
      return currentBranch;
   }

   private void setBranchName(Branch branch) {
      if (branch != null) {
         String branchName = branch.getName();
         String[] currentItems = this.branchSelectCombo.getItems();
         int selectionIndex = -1;
         for (int i = 0; i < currentItems.length; i++) {
            if (currentItems[i].equals(branchName)) {
               selectionIndex = i;
            }
         }
         if (selectionIndex < 0) {
            int oldLength = currentItems.length;
            String[] newItems = new String[oldLength + 1];
            System.arraycopy(currentItems, 0, newItems, 0, oldLength);
            newItems[oldLength] = branchName;
            this.branchSelectCombo.setItems(newItems);
            selectionIndex = oldLength;
            branchSelectCombo.setData(branch.getName(), branch);
            branchSelectCombo.setData(String.valueOf(branch.getId()), branch);
         }
         this.branchSelectCombo.select(selectionIndex);
      }
   }

   public void restoreWidgetValues(String[] branchIds, String lastSelected) {
      Branch currentBranch = getCurrentBranch();

      // Add stored directories into selector
      if (Strings.isValid(lastSelected) == false && currentBranch != null) {
         lastSelected = Integer.toString(currentBranch.getId());
      }

      if (branchIds == null) {
         if (Strings.isValid(lastSelected)) {
            branchIds = new String[] {lastSelected};
         } else {
            branchIds = new String[0];
         }
      }

      List<String> branchIdsToUse = new ArrayList<String>();
      for (String id : branchIds) {
         try {
            Branch branch = BranchManager.getBranch(Integer.parseInt(id));
            if (branch != null) {
               branchIdsToUse.add(id);
            }
         } catch (Exception ex) {
            // Do nothing
         }
      }

      setCombo(branchIdsToUse.toArray(new String[branchIdsToUse.size()]), lastSelected);
   }

   private void setCombo(String[] values, String lastSelected) {
      int toSelect = 0;
      for (int i = 0; i < values.length; i++) {
         String toStore = values[i];
         if (Strings.isValid(toStore)) {
            try {
               Branch branch = BranchManager.getBranch(Integer.parseInt(toStore));

               if (isBranchAllowed(branch) != false) {
                  String branchName = branch.getName();
                  branchSelectCombo.add(branchName);
                  branchSelectCombo.setData(String.valueOf(branch.getId()), branch);
                  branchSelectCombo.setData(branchName, branch);
                  if (toStore.equals(lastSelected)) {
                     toSelect = i;
                     branchSelectCombo.select(toSelect);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                     "Unable to add invalid branch id [%s] to selection list.", toStore));
            }
         }
      }
   }

   private boolean isBranchAllowed(Branch branch) throws Exception {
      if (areOnlyWorkingBranchesAllowed() && !branch.hasParentBranch()) {
         return false;
      }
      return true;
   }

   public String[] getBranchIds() {
      String[] items = branchSelectCombo.getItems();
      List<String> toReturn = new ArrayList<String>();
      for (String item : items) {
         Branch branch = (Branch) branchSelectCombo.getData(item);
         if (branch != null) {
            toReturn.add(String.valueOf(branch.getId()));
         }
      }
      return toReturn.toArray(new String[toReturn.size()]);
   }

   public void handleEvent(Event event) {
      if (event.widget == branchSelectButton) {
         if (areOnlyWorkingBranchesAllowed() != false) {
            setBranchName(BranchSelectionDialog.getWorkingBranchFromUser());
         } else {
            setBranchName(BranchSelectionDialog.getBranchFromUser());
         }
      }
      notifyListener(event);
   }

   private void notifyListener(Event event) {
      synchronized (listeners) {
         for (Listener listener : listeners) {
            listener.handleEvent(event);
         }
      }
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }
}
