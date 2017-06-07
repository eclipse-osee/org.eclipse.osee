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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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
   private IOseeBranch currentBranch;
   private final Set<Listener> listeners;

   public BranchSelectSimpleComposite(Composite parent, int style) {
      super(parent, style);
      this.entryChanged = false;
      this.listeners = Collections.synchronizedSet(new HashSet<Listener>());
      this.currentBranch = null;
      createControl(this);
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

   public IOseeBranch getSelectedBranch() {
      IOseeBranch toReturn = null;
      if (branchSelectCombo != null && branchSelectCombo.isDisposed() != true) {
         String branchName = branchSelectCombo.getText();
         if (Strings.isValid(branchName)) {
            toReturn = (IOseeBranch) branchSelectCombo.getData(branchName);
            if (toReturn == null) {
               try {
                  toReturn = BranchManager.getBranch(branchName);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
         currentBranch = toReturn;
      }
      return currentBranch;
   }

   private void updateFromSourceField() {
      setBranchName(getSelectedBranch());
      notifyListener(new Event());
   }

   private void setBranchName(BranchId branch) {
      if (branch != null) {
         String branchName = BranchManager.getBranchName(branch);
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
            branchSelectCombo.setData(branchName, branch);
            branchSelectCombo.setData(branch.getIdString(), branch);
         }
         this.branchSelectCombo.select(selectionIndex);
      }
   }

   public void restoreWidgetValues(String[] branchUuids, String lastSelected) {
      // Add stored directories into selector
      if (Strings.isValid(lastSelected) == false && currentBranch != null) {
         lastSelected = currentBranch.getIdString();
      }

      if (branchUuids == null) {
         if (Strings.isValid(lastSelected)) {
            branchUuids = new String[] {lastSelected};
         } else {
            branchUuids = new String[0];
         }
      }

      List<Long> branchUuidsToUse = new ArrayList<>();
      for (String id : branchUuids) {
         try {
            Long branchId = Long.parseLong(id);
            if (BranchManager.branchExists(branchId)) {
               branchUuidsToUse.add(branchId);
            }
         } catch (Exception ex) {
            // Do nothing
         }
      }

      setCombo(branchUuidsToUse, lastSelected);
   }

   private void setCombo(List<Long> values, String lastSelected) {
      int toSelect = 0;
      for (int i = 0; i < values.size(); i++) {
         Long toStore = values.get(i);
         try {
            IOseeBranch branch = BranchManager.getBranchToken(toStore);

            String branchName = branch.getName();
            branchSelectCombo.add(branchName);
            branchSelectCombo.setData(branch.getIdString(), branch);
            branchSelectCombo.setData(branchName, branch);
            if (toStore.equals(lastSelected)) {
               toSelect = i;
               branchSelectCombo.select(toSelect);
            }
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, "Unable to add invalid branch uuid [%s] to selection list.",
               toStore);
         }
      }
   }

   public String[] getBranchIds() {
      String[] items = branchSelectCombo.getItems();
      List<String> toReturn = new ArrayList<>();
      for (String item : items) {
         BranchId branch = (BranchId) branchSelectCombo.getData(item);
         if (branch != null) {
            toReturn.add(String.valueOf(branch.getId()));
         }
      }
      return toReturn.toArray(new String[toReturn.size()]);
   }

   @Override
   public void handleEvent(Event event) {
      if (event.widget == branchSelectButton) {
         setBranchName(BranchSelectionDialog.getWorkingBranchFromUser());
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
