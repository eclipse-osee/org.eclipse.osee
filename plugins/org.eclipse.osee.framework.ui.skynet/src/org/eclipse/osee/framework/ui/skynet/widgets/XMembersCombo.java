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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Search;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XMembersCombo extends GenericXWidget {
   private static final String DEFAULT_SELECTION = "--select--";
   private Combo dataCombo;
   private Composite composite;
   private User selectedUser;
   private Search searchControl;
   private boolean allUsers = false;

   public XMembersCombo(String displayLabel, boolean allUsers) {
      super(displayLabel);
      this.allUsers = allUsers;
   }

   public XMembersCombo(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return dataCombo;
   }

   public boolean equals(User user) {
      return user.equals(selectedUser);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (selectedUser == null ? 0 : selectedUser.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      XMembersCombo other = (XMembersCombo) obj;
      if (selectedUser == null) {
         if (other.selectedUser != null) {
            return false;
         }
      } else if (!selectedUser.equals(other.selectedUser)) {
         return false;
      }
      return true;
   }

   public void set(User user) {
      selectedUser = user;
      updateComboWidget();
   }

   @Override
   public String toString() {
      return getLabel() + ": *" + get() + "*";
   }

   /**
    * Create Data Widgets. Widgets Created: Data: DEFAULT_SELECTION horizonatalSpan takes up 2 columns; horizontalSpan
    * must be >=2 the string DEFAULT_SELECTION will be added to the sent in dataStrings array
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = parent;

      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      // Create Data Widgets
      if (!getLabel().equals("")) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      dataCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      dataCombo.add(DEFAULT_SELECTION);
      dataCombo.setData(DEFAULT_SELECTION, null);
      try {
         for (User user : allUsers ? UserManager.getUsersAllSortedByName() : UserManager.getUsersSortedByName()) {
            dataCombo.add(user.getName());
            dataCombo.setData(user.getName(), user);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      searchControl = new Search(dataCombo.getItems());

      GridData gridData = new GridData();
      if (fillHorizontally) {
         gridData.grabExcessHorizontalSpace = true;
      }
      if (fillVertically) {
         gridData.grabExcessVerticalSpace = true;
      }
      gridData.horizontalSpan = horizontalSpan - 1;
      dataCombo.setLayoutData(gridData);

      if (dataCombo.getItemCount() > 20) {
         dataCombo.setVisibleItemCount(20);
      }

      dataCombo.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            String selectedUserName = dataCombo.getText();
            selectedUser = (User) dataCombo.getData(selectedUserName);
            validate();
            notifyXModifiedListeners();
         }
      });

      dataCombo.addFocusListener(new FocusListener() {
         @Override
         public void focusGained(FocusEvent e) {
            resetSelectionList();
         }

         @Override
         public void focusLost(FocusEvent e) {
            // do nothing
         }
      });

      dataCombo.addKeyListener(new KeyAdapter() {
         // hook key pressed - see PR 14201
         @Override
         public void keyPressed(KeyEvent e) {
            keyReleaseOccured(e);
         }
      });

      refresh();
      dataCombo.setEnabled(isEditable());
   }

   private void resetSelectionList() {
      // store off current selection
      User currSelUser = selectedUser;
      searchControl.reset();
      dataCombo.setItems(searchControl.getItems());
      // restore current selection
      selectedUser = currSelUser;
      refresh();
   }

   @Override
   public void dispose() {
      if (composite != null && !composite.isDisposed()) {
         composite.dispose();
      }
   }

   public User getUser() {
      return selectedUser;
   }

   public boolean isAssigned() {
      return selectedUser != null;
   }

   public boolean isAssigned(User user) {
      return selectedUser != null && selectedUser.equals(user);
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (dataCombo != null && !dataCombo.isDisposed()) {
         dataCombo.setEnabled(editable);
      }
   }

   @Override
   public void refresh() {
      updateComboWidget();
   }

   public void addModifyListener(ModifyListener modifyListener) {
      dataCombo.addModifyListener(modifyListener);
   }

   public Combo getComboBox() {
      return dataCombo;
   }

   /**
    * @return selected display value (eg. Dunne, Donald G)
    */
   public String get() {
      return selectedUser == null ? "" : selectedUser.getName();
   }

   @Override
   public String getReportData() {
      return get();
   }

   private void updateComboWidget() {
      if (dataCombo != null) {
         int index = 0;

         if (selectedUser != null) {
            index = dataCombo.indexOf(selectedUser.getName());
            if (index == -1) {
               index = 0;
            }
         }
         dataCombo.select(index);
      }
      validate();
   }

   public void clear() {
      selectedUser = null;
      updateComboWidget();
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must select " + getLabel());
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return !isAssigned();
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + get();
   }

   protected void keyReleaseOccured(KeyEvent keyEvent) {
      if (keyEvent.character != 0x00 && keyEvent.character != SWT.CR) {
         searchControl.progressiveSearch(keyEvent);
         if (searchControl.getDirty()) {
            dataCombo.setItems(searchControl.getItems());
            searchControl.setDirty(false);
            refresh();
         }
      }
      // If delete key pressed, reset
      if (keyEvent.character == SWT.DEL || keyEvent.character == SWT.BS || keyEvent.character == SWT.ESC) {
         resetSelectionList();
      }
   }

   @Override
   public Object getData() {
      return selectedUser;
   }
}