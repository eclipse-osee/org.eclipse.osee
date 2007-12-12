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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.swt.Search;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XMembersCombo extends XWidget {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(XMembersCombo.class);
   private static SkynetAuthentication skyAuth = SkynetAuthentication.getInstance();
   private static final String DEFAULT_SELECTION = "--select--";
   private Combo dataCombo;
   private Composite composite;
   private User selectedUser;
   private Search searchControl;
   private boolean resetCommand = false;

   public XMembersCombo(String displayLabel) {
      this(displayLabel, "", "");
   }

   @SuppressWarnings("unchecked")
   public XMembersCombo(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
   }

   @SuppressWarnings("unchecked")
   public XMembersCombo(String displayLabel, Collection<User> members) {
      super(displayLabel, displayLabel, "user");
   }

   public XMembersCombo(String displayLabel, String xmlRoot) {
      this(displayLabel, xmlRoot, "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return dataCombo;
   }

   public boolean equals(User user) {
      return user.equals(selectedUser);
   }

   public boolean equals(Object obj) {
      if (obj instanceof XMembersCombo)
         return ((XMembersCombo) obj).selectedUser.equals(selectedUser);
      else
         return super.equals(obj);
   }

   public void set(User user) {
      selectedUser = user;
      updateComboWidget();
   }

   public String toString() {
      return label + ": *" + get() + "*";
   }

   /**
    * Create Data Widgets. Widgets Created: Data: DEFAULT_SELECTION horizonatalSpan takes up 2 columns; horizontalSpan
    * must be >=2 the string DEFAULT_SELECTION will be added to the sent in dataStrings array
    */
   public void createWidgets(Composite parent, int horizontalSpan) {
      composite = parent;

      if (horizontalSpan < 2) horizontalSpan = 2;

      // Create Data Widgets
      if (!label.equals("")) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      dataCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      dataCombo.add(DEFAULT_SELECTION);
      dataCombo.setData(DEFAULT_SELECTION, null);
      for (User user : SkynetAuthentication.getInstance().getUsers()) {
         dataCombo.add(user.getName());
         dataCombo.setData(user.getName(), user);
      }
      searchControl = new Search(dataCombo.getItems());

      GridData gridData = new GridData();
      if (fillHorizontally) gridData.grabExcessHorizontalSpace = true;
      if (fillVertically) gridData.grabExcessVerticalSpace = true;
      gridData.horizontalSpan = horizontalSpan - 1;
      dataCombo.setLayoutData(gridData);

      if (dataCombo.getItemCount() > 20) dataCombo.setVisibleItemCount(20);

      ModifyListener dataComboListener = new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            String selectedUserName = dataCombo.getText();
            selectedUser = (User) dataCombo.getData(selectedUserName);
            setLabelError();

            if (resetCommand) {
               resetCommand = false;
               searchControl.reset();
               dataCombo.setItems(searchControl.getItems());
               refresh();
            }
            notifyXModifiedListeners();
         }
      };
      dataCombo.addModifyListener(dataComboListener);

      dataCombo.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            resetCommand = true;
         }
      });

      dataCombo.addKeyListener(new KeyAdapter() {
         // hook key pressed - see PR 14201
         public void keyPressed(KeyEvent e) {
            keyReleaseOccured(e);
         }
      });

      refresh();
      dataCombo.setEnabled(editable);
   }

   @Override
   public void dispose() {
      if (composite != null && !composite.isDisposed()) composite.dispose();
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

   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (dataCombo != null && !dataCombo.isDisposed()) dataCombo.setEnabled(editable);
   }

   public void setFocus() {
      if (dataCombo != null) dataCombo.setFocus();
   }

   public void setFromXml(String xml) {
      Matcher matcher;
      if (xmlSubRoot.equals("")) {
         matcher =
               Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
                     xml);
      } else {
         matcher =
               Pattern.compile("<" + xmlRoot + "><" + xmlSubRoot + ">(.*?)</" + xmlSubRoot + "></" + xmlRoot + ">",
                     Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      }
      while (matcher.find()) {
         String userId = matcher.group(1);
         User user = null;
         try {
            user = skyAuth.getUserByIdWithError(userId);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error Searching for User in DB.\n", ex);
         }
         set(user);
      }
      refresh();
   }

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

   public String getReportData() {
      return get();
   }

   public String getXmlData() {
      return get();
   }

   public void setXmlData(String str) {
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
      setLabelError();
   }

   public void clear() {
      selectedUser = null;
      updateComboWidget();
   }

   public boolean isValid() {
      if (requiredEntry && !isAssigned()) return false;
      return true;
   }

   public String toXml() {
      return toXml(xmlRoot);
   }

   public String toXml(String xmlRoot) {
      String s;
      String dataStr = selectedUser.getUserId();
      if (xmlSubRoot == null || xmlSubRoot.equals("")) {
         s = "<" + xmlRoot + ">" + dataStr + "</" + xmlRoot + ">\n";
      } else {
         s = "<" + xmlRoot + "><" + xmlSubRoot + ">" + dataStr + "</" + xmlSubRoot + "></" + xmlRoot + ">\n";
      }
      return s;
   }

   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, label + ": ") + get();
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
   }

   @Override
   public Object getData() {
      return dataCombo.getText();
   }
}